package com.android.skip.vpnservice

import android.annotation.SuppressLint
import android.content.Context
import android.net.VpnService
import android.os.Build
import android.util.Base64
import android.util.Log
import com.android.skip.MyApp.Companion.context
import com.android.skip.vpnservice.protocol.IpUtil
import com.android.skip.vpnservice.protocol.Packet
import com.android.skip.vpnservice.protocol.Packet.TCPHeader
import com.android.skip.vpnservice.protocol.TCBStatus
import kotlinx.coroutines.*
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * 设备发向网络的UDP数据包队列
 */
val deviceToNetworkUDPQueue = ArrayBlockingQueue<Packet>(1024)

/**
 * 设备发向网络的TCP数据包队列
 */
val deviceToNetworkTCPQueue = ArrayBlockingQueue<Packet>(1024)

/**
 * 网络回复设备的数据包队列
 */
val networkToDeviceQueue = ArrayBlockingQueue<ByteBuffer>(1024)

/**
 * TCP转发网络选择器
 */
val tcpNioSelector: Selector = Selector.open()

/**
 * UDP转发通道队列
 */
val udpTunnelQueue = ArrayBlockingQueue<UdpTunnel>(1024)

/**
 * UDP转发网络选择器
 */
val udpNioSelector: Selector = Selector.open()

/**
 * 已存在的udp socket Map<br>
 * key为目标主机地址:目标端口:请求端口
 */
val udpSocketMap = HashMap<String, ManagedDatagramChannel>()

const val UDP_SOCKET_IDLE_TIMEOUT = 60


/**
 * 处理设备发往网络的数据包工作线程
 */
object ToNetworkQueueWorker : Runnable {
    private const val TAG = "ToNetworkQueueWorker"

    /**
     * 自身线程
     */
    private lateinit var thread: Thread

    /**
     * 读取来自设备的数据通道
     */
    private lateinit var vpnInput: FileChannel

    /**
     * 总读取数据字节计数
     */
    var totalInputCount = 0L


    fun start(vpnFileDescriptor: FileDescriptor) {
        if (this::thread.isInitialized && thread.isAlive) throw IllegalStateException("已经在运行")
        vpnInput = FileInputStream(vpnFileDescriptor).channel
        thread = Thread(this).apply {
            name = TAG
            start()
        }
    }

    fun stop() {
        if (this::thread.isInitialized) {
            thread.interrupt()
        }
    }

    override fun run() {
        val readBuffer = ByteBuffer.allocate(16384)
        while (!thread.isInterrupted) {
            var readCount = 0
            try {
                readCount = vpnInput.read(readBuffer)
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
            if (readCount > 0) {
                readBuffer.flip()
                val byteArray = ByteArray(readCount)
                readBuffer.get(byteArray)

                val byteBuffer = ByteBuffer.wrap(byteArray)
                totalInputCount += readCount

                val packet = Packet(byteBuffer)
                if (packet.isUDP) {
                    deviceToNetworkUDPQueue.offer(packet)
                } else if (packet.isTCP) {
                    deviceToNetworkTCPQueue.offer(packet)
                } else {
                    Log.d(TAG, "未知的数据包协议类型${packet.ip4Header.protocolNum}")
                }
            } else if (readCount < 0) {
                break
            }
            readBuffer.clear()
        }
        Log.i(TAG, "ToNetworkQueueWorker运行结束")
    }

}

/**
 * 处理网络发往设备的数据包工作线程
 */
object ToDeviceQueueWorker : Runnable {
    private const val TAG = "ToDeviceQueueWorker"

    /**
     * 自身线程
     */
    private lateinit var thread: Thread

    /**
     * 总写入数据字节计数
     */
    var totalOutputCount = 0L


    /**
     * 写入来自网络的数据通道
     */
    private lateinit var vpnOutput: FileChannel

    fun start(vpnFileDescriptor: FileDescriptor) {
        if (this::thread.isInitialized && thread.isAlive) throw IllegalStateException("已经在运行")
        vpnOutput = FileOutputStream(vpnFileDescriptor).channel
        thread = Thread(this).apply {
            name = TAG
            start()
        }
    }

    fun stop() {
        if (this::thread.isInitialized) {
            thread.interrupt()
        }
    }

    override fun run() {
        try {
            while (!thread.isInterrupted) {
                val byteBuffer = networkToDeviceQueue.take()
                byteBuffer.flip()
                while (byteBuffer.hasRemaining()) {
                    val count = vpnOutput.write(byteBuffer)
                    if (count > 0) {
                        totalOutputCount += count
                    }
                }
            }
        } catch (_: InterruptedException) {

        } catch (_: ClosedByInterruptException) {

        }

    }
}

/**
 * UDP转发通道数据
 */
data class UdpTunnel(val id: String, val local: InetSocketAddress, val remote: InetSocketAddress, val channel: DatagramChannel)

data class ManagedDatagramChannel(val id: String, val channel: DatagramChannel, var lastTime: Long = System.currentTimeMillis())

/**
 * UDP数据包发送工作线程
 */
@SuppressLint("StaticFieldLeak")
object UdpSendWorker : Runnable {
    private const val TAG = "UdpSendWorker"

    /**
     * 自身线程
     */
    private lateinit var thread: Thread

    private var vpnService: VpnService? = null

    fun start(vpnService: VpnService) {
        this.vpnService = vpnService
        udpTunnelQueue.clear()
        thread = Thread(this).apply {
            name = TAG
            start()
        }
    }

    fun stop() {
        if (this::thread.isInitialized) {
            thread.interrupt()
        }
        vpnService = null
    }

    override fun run() {
        while (!thread.isInterrupted) {
            val packet = deviceToNetworkUDPQueue.take()
            val destinationAddress = packet.ip4Header.destinationAddress
            val udpHeader = packet.udpHeader

            val destinationPort = udpHeader.destinationPort
            val sourcePort = udpHeader.sourcePort
            val ipAndPort = (destinationAddress.hostAddress?.plus(":") ?: "unknownHostAddress") + destinationPort + ":" + sourcePort

            //创建新的socket
            val managedChannel = if (!udpSocketMap.containsKey(ipAndPort)) {
                val channel = DatagramChannel.open()
                var channelConnectSuccess = false
                channel.apply {
                    val socket = socket()
                    vpnService?.protect(socket)
                    try {
                        connect(InetSocketAddress(destinationAddress, destinationPort))
                        channelConnectSuccess = true
                    } catch (_: ConnectException) {
                    }
                    configureBlocking(false)
                }
                if (!channelConnectSuccess) {
                    continue
                }

                val tunnel = UdpTunnel(
                    ipAndPort,
                    InetSocketAddress(packet.ip4Header.sourceAddress, udpHeader.sourcePort),
                    InetSocketAddress(packet.ip4Header.destinationAddress, udpHeader.destinationPort),
                    channel
                )
                udpTunnelQueue.offer(tunnel)
                udpNioSelector.wakeup()

                val managedDatagramChannel = ManagedDatagramChannel(ipAndPort, channel)
                synchronized(udpSocketMap) {
                    udpSocketMap[ipAndPort] = managedDatagramChannel
                }
                managedDatagramChannel
            } else {
                synchronized(udpSocketMap) {
                    udpSocketMap[ipAndPort] ?: throw IllegalStateException("udp:udpSocketMap[$ipAndPort]不应为null")
                }
            }
            managedChannel.lastTime = System.currentTimeMillis()
            val buffer = packet.backingBuffer
            kotlin.runCatching {
                while (!thread.isInterrupted && buffer.hasRemaining()) {
                    managedChannel.channel.write(buffer)
                }

            }.exceptionOrNull()?.let {
                Log.e(TAG, "发送udp数据包发生错误", it)
                managedChannel.channel.close()
                synchronized(udpSocketMap) {
                    udpSocketMap.remove(ipAndPort)
                }
            }
        }
    }
}

/**
 * UDP数据包接收工作线程
 */
@SuppressLint("StaticFieldLeak")
object UdpReceiveWorker : Runnable {

    private const val TAG = "UdpReceiveWorker"

    /**
     * 自身线程
     */
    private lateinit var thread: Thread

    private var regexListString: MutableList<String> = MutableList(36) { "" }

    private var regexListRegex:MutableList<Regex?> = MutableList(36) { null }

    private var vpnService: VpnService? = null

    private var ipId = AtomicInteger()

    private const val UDP_HEADER_FULL_SIZE = Packet.IP4_HEADER_SIZE + Packet.UDP_HEADER_SIZE

    fun start(vpnService: VpnService) {
        this.vpnService = vpnService
        thread = Thread(this).apply {
            name = TAG
            start()
        }
        GlobalScope.launch {
            loadDomainsAndCreateRegexByFirstCharacterAsync(vpnService, "OISD.txt").await()
            for (index in 0..35){
                regexListRegex[index] = getRegexForIndexAsync(index).await()
            }
        }
    }

    fun stop() {
        thread.interrupt()
    }

    fun loadDomainsAndCreateRegexByFirstCharacterAsync(context: Context, filePath: String): Deferred<Unit> = GlobalScope.async {
        try {
            // 读取文件内容并按行分割
            val inputStream = context.assets.open(filePath)
            val domains = inputStream.bufferedReader().readLines()

            // 遍历所有域名，根据第一个字符分类
            domains.forEach { domain ->
                // 获取第一个字符
                val firstChar = domain.firstOrNull()

                // 如果第一个字符是字母或者数字
                val index = when {
                    firstChar in '0'..'9' -> firstChar?.minus('0') ?: null // 直接使用 minus 减去字符
                    firstChar in 'a'..'z' -> firstChar?.minus('a')?.plus(10) ?:null // a-z 对应 10-35
                    firstChar in 'A'..'Z' -> firstChar?.lowercaseChar()?.minus('a')?.plus(10) ?:null // A-Z 转小写
                    else -> null
                }

                // 如果索引合法，生成正则并添加到对应的位置
                if (index != null && index in 0..35) {
                    val regexPart = "^([a-zA-Z0-9-]+\\.)*${domain.replace(".", "\\.")}$"

                    // 在对应位置追加正则表达式并用 | 连接
                    if (regexListString[index].isNotEmpty()) {
                        regexListString[index] += "|$regexPart"
                    } else {
                        regexListString[index] = regexPart
                    }
                }
            }

        } catch (e: Exception) {
            println("Error loading domains and creating regexes: ${e.message}")
        }
    }

    // 例子：通过索引检索相应的正则表达式
    fun getRegexForIndexAsync(index: Int): Deferred<Regex?> = GlobalScope.async {
        return@async if (index in 0..35) {
            Regex(regexListString[index]) // 异步创建 Regex
        } else {
            null
        }
    }

    private fun sendUdpPacket(tunnel: UdpTunnel, source: InetSocketAddress, data: ByteArray) {
        val packet = IpUtil.buildUdpPacket(tunnel.remote, tunnel.local, ipId.addAndGet(1))

        val byteBuffer = ByteBuffer.allocate(UDP_HEADER_FULL_SIZE + data.size)
        byteBuffer.apply {
            position(UDP_HEADER_FULL_SIZE)
            put(data)
        }
        packet.updateUDPBuffer(byteBuffer, data.size)
        byteBuffer.position(UDP_HEADER_FULL_SIZE + data.size)
        networkToDeviceQueue.offer(byteBuffer)
    }
    // 判断是否为 DNS 请求
    private fun isDnsRequest(data: ByteArray): Boolean {
        // DNS 请求通常使用 53 端口，且以 DNS 请求头部开始
        return data.size >= 12 && (data[2].toInt() shl 8 or data[3].toInt() and 0xFF) and 0x8000 == 0
    }

    fun extractDomainName(data: ByteArray): String? {
        val domainName = StringBuilder()
        var index = 12  // DNS 请求头部的偏移量，跳过 Transaction ID、Flags、Questions 等字段

        // 获取问题部分的数量
        val questionCount = (data[4].toInt() shl 8 or data[5].toInt()) // Questions 字段
        if (questionCount == 0) return null  // 如果没有问题部分，返回 null

        // 遍历每个查询问题部分（通常只会有一个）
        for (i in 0 until questionCount) {
            while (index < data.size) {
                val length = data[index].toInt()  // 获取标签长度
                if (length == 0) {  // 标签长度为 0 表示域名结束
                    index++
                    break
                }

                // 解析标签
                val label = String(data, index + 1, length)
                domainName.append(label).append(".")

                // 移动索引到下一个标签的位置
                index += length + 1
            }
        }

        // 去掉最后一个多余的点
        if (domainName.isNotEmpty()) {
            domainName.deleteCharAt(domainName.length - 1)
        }

        return domainName.toString()
    }
    fun matchWithRegex(index: Int, testString: String): Boolean {
        val regex = regexListRegex[index] // 获取对应的 Regex 对象
        return regex?.matches(testString) == true // 使用 matches() 方法进行匹配
    }
    // 处理 DNS 请求
    private fun handleDnsRequest(data: ByteArray): Boolean {
        // 此处可以添加对 DNS 请求的处理代码，解析请求并生成响应
        // 比如解析域名，判断查询类型等
        val domain = extractDomainName(data)

        if (domain!=null) {
            for(index in 0..35){
                if(matchWithRegex(index,domain))return true
            }
            return false
        }
        else {
            return false
        }
    }

    override fun run() {
        val receiveBuffer = ByteBuffer.allocate(16384)
        while (!thread.isInterrupted) {
            val readyChannels = udpNioSelector.select()
            while (!thread.isInterrupted) {
                val tunnel = udpTunnelQueue.poll() ?: break
                kotlin.runCatching {
                    val key = tunnel.channel.register(udpNioSelector, SelectionKey.OP_READ, tunnel)
                    key.interestOps(SelectionKey.OP_READ)
                }.exceptionOrNull()?.printStackTrace()
            }
            if (readyChannels == 0) {
                udpNioSelector.selectedKeys().clear()
                continue
            }
            val keys = udpNioSelector.selectedKeys()
            val iterator = keys.iterator()
            while (!thread.isInterrupted && iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()
                if (key.isValid && key.isReadable) {
                    val tunnel = key.attachment() as UdpTunnel
                    kotlin.runCatching {
                        val inputChannel = key.channel() as DatagramChannel
                        receiveBuffer.clear()
                        inputChannel.read(receiveBuffer)
                        receiveBuffer.flip()
                        val data = ByteArray(receiveBuffer.remaining())
                        receiveBuffer.get(data)
                        if (!isDnsRequest(data) || !handleDnsRequest(data)) {
                            sendUdpPacket(
                                tunnel,
                                inputChannel.socket().localSocketAddress as InetSocketAddress,
                                data
                            ) //todo api 21->24
                        }
                    }.exceptionOrNull()?.let {
                        it.printStackTrace()
                        synchronized(udpSocketMap) {
                            udpSocketMap.remove(tunnel.id)
                        }
                    }
                }
            }
        }
    }

}

/**
 * Udp失效socket清理工作线程
 */
object UdpSocketCleanWorker : Runnable {

    private const val TAG = "UdpSocketCleanWorker"

    /**
     * 自身线程
     */
    private lateinit var thread: Thread

    /**
     * 检查间隔，单位：秒
     */
    private const val INTERVAL_TIME = 5L

    fun start() {
        thread = Thread(this).apply {
            name = TAG
            start()
        }
    }

    fun stop() {
        thread.interrupt()
    }

    override fun run() {
        while (!thread.isInterrupted) {
            synchronized(udpSocketMap) {
                val iterator = udpSocketMap.iterator()
                var removeCount = 0
                while (!thread.isInterrupted && iterator.hasNext()) {
                    val managedDatagramChannel = iterator.next()
                    if (System.currentTimeMillis() - managedDatagramChannel.value.lastTime > UDP_SOCKET_IDLE_TIMEOUT * 1000) {
                        kotlin.runCatching {
                            managedDatagramChannel.value.channel.close()
                        }.exceptionOrNull()?.printStackTrace()
                        iterator.remove()
                        removeCount++
                    }
                }
                if (removeCount > 0) {
                    Log.d(TAG, "移除${removeCount}个超时未活动的UDP，当前有效${udpSocketMap.size}")
                }
            }
            Thread.sleep(INTERVAL_TIME * 1000)
        }
    }

}

class TcpPipe(val tunnelKey: String, packet: Packet) {
    var mySequenceNum: Long = 0
    var theirSequenceNum: Long = 0
    var myAcknowledgementNum: Long = 0
    var theirAcknowledgementNum: Long = 0
    val tunnelId = tunnelIds++

    val sourceAddress: InetSocketAddress = InetSocketAddress(packet.ip4Header.sourceAddress, packet.tcpHeader.sourcePort)
    val destinationAddress: InetSocketAddress = InetSocketAddress(packet.ip4Header.destinationAddress, packet.tcpHeader.destinationPort)
    val remoteSocketChannel: SocketChannel = SocketChannel.open().also { it.configureBlocking(false) }
    val remoteSocketChannelKey: SelectionKey = remoteSocketChannel.register(tcpNioSelector, SelectionKey.OP_CONNECT).also { it.attach(this) }

    var tcbStatus: TCBStatus = TCBStatus.SYN_SENT
    var remoteOutBuffer: ByteBuffer? = null

    var upActive = true
    var downActive = true
    var packId = 1
    var timestamp = System.currentTimeMillis()
    var synCount = 0


    fun tryConnect(vpnService: VpnService): Result<Boolean> {
        val result = kotlin.runCatching {
            vpnService.protect(remoteSocketChannel.socket())
            remoteSocketChannel.connect(destinationAddress)
        }
        return result
    }


    companion object {
        const val TAG = "TcpPipe"
        var tunnelIds = 0
    }
}

/**
 * TCP数据包工作线程<br>
 * NIO
 */
@SuppressLint("StaticFieldLeak")
object TcpWorker : Runnable {
    private const val TAG = "TcpSendWorker"

    private const val TCP_HEADER_SIZE = Packet.IP4_HEADER_SIZE + Packet.TCP_HEADER_SIZE

    private lateinit var thread: Thread

    private val pipeMap = HashMap<String, TcpPipe>()

    private var vpnService: VpnService? = null

    fun start(vpnService: VpnService) {
        this.vpnService = vpnService
        thread = Thread(this).apply {
            name = TAG
            start()
        }
    }

    fun stop() {
        thread.interrupt()
        vpnService = null
    }

    override fun run() {
        while (!thread.isInterrupted) {
            if (vpnService == null) {
                throw IllegalStateException("VpnService不应为null")
            }
            handleReadFromVpn()
            handleSockets()

            Thread.sleep(1)
        }
    }

    private fun handleReadFromVpn() {
        while (!thread.isInterrupted) {
            val vpnService = this.vpnService ?: return
            val packet = deviceToNetworkTCPQueue.poll() ?: return
            val destinationAddress = packet.ip4Header.destinationAddress
            val tcpHeader = packet.tcpHeader
            val destinationPort = tcpHeader.destinationPort
            val sourcePort = tcpHeader.sourcePort
            val ipAndPort = (destinationAddress.hostAddress?.plus(":") ?: "unknown-host-address") + destinationPort + ":" + sourcePort
            val tcpPipe = if (!pipeMap.containsKey(ipAndPort)) {
                val pipe = TcpPipe(ipAndPort, packet)
                pipe.tryConnect(vpnService)
                pipeMap[ipAndPort] = pipe
                pipe
            } else {
                pipeMap[ipAndPort] ?: throw IllegalStateException("pipeMap中不应存在null key:$ipAndPort")
            }
            /*TODO 在屏幕上打印packet.ip4Header.destinationAddress,就是弹出提示框*/
            //Log.e("TAG","TCP_Recive"+packet.ip4Header.destinationAddress.toString())
            //if(packet.ip4Header.destinationAddress.toString()=="111.13.149.108")continue
            handlePacket(packet, tcpPipe)
        }
    }

    private fun handleSockets() {
        while (!thread.isInterrupted && tcpNioSelector.selectNow() > 0) {
            val keys = tcpNioSelector.selectedKeys()
            val iterator = keys.iterator()
            while (!thread.isInterrupted && iterator.hasNext()) {
                val key = iterator.next()
                iterator.remove()
                val tcpPipe: TcpPipe? = key?.attachment() as? TcpPipe
                if (key.isValid) {
                    kotlin.runCatching {
                        if (key.isAcceptable) {
                            throw RuntimeException("key.isAcceptable")
                        } else if (key.isReadable) {
                            tcpPipe?.doRead()
                        } else if (key.isConnectable) {
                            tcpPipe?.doConnect()
                        } else if (key.isWritable) {
                            tcpPipe?.doWrite()
                        } else {
                            tcpPipe?.closeRst()
                        }
                        null
                    }.exceptionOrNull()?.let {

                        Log.d(TAG, "与目标通信发生错误:${Base64.encodeToString(tcpPipe?.destinationAddress.toString().toByteArray(), Base64.DEFAULT)}")
                        it.printStackTrace()
                        tcpPipe?.closeRst()
                    }
                }

            }
        }
    }

    private fun handlePacket(packet: Packet, tcpPipe: TcpPipe) {
        val tcpHeader = packet.tcpHeader
        when {
            tcpHeader.isSYN -> {
                handleSyn(packet, tcpPipe)
            }
            tcpHeader.isRST -> {
                handleRst(tcpPipe)
            }
            tcpHeader.isFIN -> {
                handleFin(packet, tcpPipe)
            }
            tcpHeader.isACK -> {
                handleAck(packet, tcpPipe)
            }
        }
    }

    private fun handleSyn(packet: Packet, tcpPipe: TcpPipe) {
        if (tcpPipe.tcbStatus == TCBStatus.SYN_SENT) {
            tcpPipe.tcbStatus = TCBStatus.SYN_RECEIVED
        }
        val tcpHeader = packet.tcpHeader
        tcpPipe.apply {
            if (synCount == 0) {
                mySequenceNum = 1
                theirSequenceNum = tcpHeader.sequenceNumber
                myAcknowledgementNum = tcpHeader.sequenceNumber + 1
                theirAcknowledgementNum = tcpHeader.acknowledgementNumber
                sendTcpPack(this, TCPHeader.SYN.toByte() or TCPHeader.ACK.toByte())
            } else {
                myAcknowledgementNum = tcpHeader.sequenceNumber + 1
            }
            synCount++
        }
    }

    private fun handleRst(tcpPipe: TcpPipe) {
        tcpPipe.apply {
            upActive = false
            downActive = false
            clean()
            tcbStatus = TCBStatus.CLOSE_WAIT
        }
    }

    private fun handleFin(packet: Packet, tcpPipe: TcpPipe) {
        tcpPipe.myAcknowledgementNum = packet.tcpHeader.sequenceNumber + 1
        tcpPipe.theirAcknowledgementNum = packet.tcpHeader.acknowledgementNumber + 1
        sendTcpPack(tcpPipe, TCPHeader.ACK.toByte())
        tcpPipe.closeUpStream()
        tcpPipe.tcbStatus = TCBStatus.CLOSE_WAIT
    }

    private fun handleAck(packet: Packet, tcpPipe: TcpPipe) {
        if (tcpPipe.tcbStatus == TCBStatus.SYN_RECEIVED) {
            tcpPipe.tcbStatus = TCBStatus.ESTABLISHED
        }

        val tcpHeader = packet.tcpHeader
        val payloadSize = packet.backingBuffer.remaining()

        if (payloadSize == 0) {
            return
        }

        val newAck = tcpHeader.sequenceNumber + payloadSize
        if (newAck <= tcpPipe.myAcknowledgementNum) {
            return
        }

        tcpPipe.apply {
            myAcknowledgementNum = tcpHeader.sequenceNumber + payloadSize
            theirAcknowledgementNum = tcpHeader.acknowledgementNumber
            remoteOutBuffer = packet.backingBuffer
            tryFlushWrite(this)
            sendTcpPack(this, TCPHeader.ACK.toByte())
        }

    }

    /**
     * 发送tcp包
     */
    private fun sendTcpPack(tcpPipe: TcpPipe, flag: Byte, data: ByteArray? = null) {
        val dataSize = data?.size ?: 0

        val packet = IpUtil.buildTcpPacket(
            tcpPipe.destinationAddress,
            tcpPipe.sourceAddress,
            flag,
            tcpPipe.myAcknowledgementNum,
            tcpPipe.mySequenceNum,
            tcpPipe.packId
        )
        tcpPipe.packId++

        val byteBuffer = ByteBuffer.allocate(TCP_HEADER_SIZE + dataSize)
        byteBuffer.position(TCP_HEADER_SIZE)
        /*TODO start debug*/
        // 启动协程并异步执行这两行代码
        // 使主线程等待协程完成，可以使用 delay 来模拟等待或 `join()` 等待协程
        /*TODO end debug*/
        data?.let {
            byteBuffer.put(it)
        }

        packet?.updateTCPBuffer(byteBuffer, flag, tcpPipe.mySequenceNum, tcpPipe.myAcknowledgementNum, dataSize)
        packet?.release()

        byteBuffer.position(TCP_HEADER_SIZE + dataSize)

        networkToDeviceQueue.offer(byteBuffer)

        if ((flag and TCPHeader.SYN.toByte()) != 0.toByte()) {
            tcpPipe.mySequenceNum++
        }
        if ((flag and TCPHeader.FIN.toByte()) != 0.toByte()) {
            tcpPipe.mySequenceNum++
        }
        if ((flag and TCPHeader.ACK.toByte()) != 0.toByte()) {
            tcpPipe.mySequenceNum += dataSize
        }

    }

    /**
     * 对外写数据
     */
    private fun tryFlushWrite(tcpPipe: TcpPipe): Boolean {
        val channel: SocketChannel = tcpPipe.remoteSocketChannel
        val buffer = tcpPipe.remoteOutBuffer

        if (tcpPipe.remoteSocketChannel.socket().isOutputShutdown && buffer?.remaining() != 0) {
            sendTcpPack(tcpPipe, TCPHeader.FIN.toByte() or TCPHeader.ACK.toByte())
            buffer?.compact()
            return false
        }

        if (!channel.isConnected) {
//            Log.w(TAG, "连接未就绪")
            val key = tcpPipe.remoteSocketChannelKey
            val ops = key.interestOps() or SelectionKey.OP_WRITE
            key.interestOps(ops)
            buffer?.compact()
            return false
        }

        while (!thread.isInterrupted && buffer?.hasRemaining() == true) {
            val n = kotlin.runCatching {
                channel.write(buffer)
            }
            if (n.isFailure) return false
            if (n.getOrThrow() <= 0) {
                val key = tcpPipe.remoteSocketChannelKey
                val ops = key.interestOps() or SelectionKey.OP_WRITE
                key.interestOps(ops)
                buffer.compact()
                return false
            }
        }
        buffer?.clear()
        if (!tcpPipe.upActive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tcpPipe.remoteSocketChannel.shutdownOutput()
            } else {
                //todo 下面这句会导致无法正确处理socket，但这里不处理没问题么？
//                tcpPipe.remoteSocketChannel.close()
            }
        }
        return true
    }

    private fun TcpPipe.closeRst() {
        Log.d(TAG, "closeRst $tunnelId")
        clean()
        sendTcpPack(this, TCPHeader.RST.toByte())
        upActive = false
        downActive = false
    }

    private fun TcpPipe.doRead() {
        val buffer = ByteBuffer.allocate(4096)
        var isQuitType = false

        while (!thread.isInterrupted) {
            buffer.clear()
            val length = remoteSocketChannel.read(buffer)
            if (length == -1) {
                isQuitType = true
                break
            } else if (length == 0) {
                break
            } else {
                if (tcbStatus != TCBStatus.CLOSE_WAIT) {
                    buffer.flip()
                    val dataByteArray = ByteArray(buffer.remaining())
                    buffer.get(dataByteArray)
                    sendTcpPack(this, TCPHeader.ACK.toByte(), dataByteArray)
                }
            }
        }

        if (isQuitType) {
            closeDownStream()
        }
    }

    private fun TcpPipe.doConnect() {
        val finishConnect = remoteSocketChannel.finishConnect()
        timestamp = System.currentTimeMillis()
        remoteOutBuffer?.flip()
        remoteSocketChannelKey.interestOps(SelectionKey.OP_READ or SelectionKey.OP_WRITE)
    }

    private fun TcpPipe.doWrite() {
        if (tryFlushWrite(this)) {
            remoteSocketChannelKey.interestOps(SelectionKey.OP_READ)
        }
    }

    private fun TcpPipe.clean() {
        kotlin.runCatching {
            if (remoteSocketChannel.isOpen) {
                remoteSocketChannel.close()
            }
            remoteOutBuffer = null
            pipeMap.remove(tunnelKey)
        }.exceptionOrNull()?.printStackTrace()
    }

    private fun TcpPipe.closeUpStream() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            kotlin.runCatching {
                if (remoteSocketChannel.isOpen && remoteSocketChannel.isConnected) {
                    remoteSocketChannel.shutdownOutput()
                }
            }.exceptionOrNull()?.printStackTrace()
            upActive = false

            if (!downActive) {
                clean()
            }
        } else {
            upActive = false
            downActive = false
            clean()
        }
    }

    private fun TcpPipe.closeDownStream() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            kotlin.runCatching {
                if (remoteSocketChannel.isConnected) {
                    remoteSocketChannel.shutdownInput()
                    val ops = remoteSocketChannelKey.interestOps() and SelectionKey.OP_READ.inv()
                    remoteSocketChannelKey.interestOps(ops)
                }
                sendTcpPack(this, (TCPHeader.FIN.toByte() or TCPHeader.ACK.toByte()))
                downActive = false
                if (!upActive) {
                    clean()
                }
            }
        } else {
            sendTcpPack(this, (TCPHeader.FIN.toByte() or TCPHeader.ACK.toByte()))
            upActive = false
            downActive = false
            clean()
        }
    }
}