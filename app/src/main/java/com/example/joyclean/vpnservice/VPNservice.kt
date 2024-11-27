package com.example.joyclean.vpnservice

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = true
    private var inputStream: FileInputStream? = null
    private var outputStream: FileOutputStream? = null
    private var trafficProcessingThread: Thread? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 配置 VPN 接口
        val builder = Builder()
        builder.setSession("MyVPN")
            .addAddress("10.0.0.2", 24)
            .addRoute("0.0.0.0", 0)
            .setMtu(1500)
        vpnInterface = builder.establish()

        // 启动流量处理线程
        vpnInterface?.let { startTrafficProcessing(it) }

        return START_STICKY
    }

    private fun startTrafficProcessing(vpnInterface: ParcelFileDescriptor) {
        val inputStream = FileInputStream(vpnInterface.fileDescriptor)
        val outputStream = FileOutputStream(vpnInterface.fileDescriptor)
        val bufferSize = 1024 * 1024
        Thread {
            val buffer = ByteArray(bufferSize) // 缓冲区大小
            while (isRunning) {
                try {
                    // 从 TUN 接口读取数据包
                    val length = inputStream.read(buffer)
                    if (length > 0) {
                        // 处理数据包
                        handlePacket(buffer, length)
                        // 可以选择转发数据包
                        //outputStream.write(buffer, 0, length)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
        }.start()
    }

    private fun handlePacket(packet: ByteArray, length: Int) {
        // 解析数据包
        // 示例：输出数据包大小
        Log.e("TAG", "Received packet of size: $length bytes")
        /**
         * 后面进入核心逻辑部分(过滤数据包)
         */
    }
    private fun stopVpnInterface() {
        // 停止虚拟网络接口并清理资源
        try {
            isRunning = false
            trafficProcessingThread?.interrupt()  // 中断数据处理线程
            trafficProcessingThread = null

            inputStream?.close()  // 关闭输入流
            outputStream?.close() // 关闭输出流
            vpnInterface?.close() // 关闭虚拟接口

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopVpnInterface()  // 假设你有一个方法来停止虚拟接口
        isRunning = false
        vpnInterface?.close()
        vpnInterface = null
    }
}
