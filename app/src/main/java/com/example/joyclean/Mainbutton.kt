package com.example.joyclean
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext
import com.example.joyclean.database.AppManager
import android.app.Activity.* // 如果代码在 Activity 中运行需要导入
import android.net.VpnService // 用于检查和启动 VPN 服务
import com.example.joyclean.vpnservice.MyVpnService


@Composable
fun ToggleCircle(isOn: Boolean, onToggle: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(0.8f) // 宽度占页面宽度的 80%
            .aspectRatio(1.0f)    // 高度与宽度保持 1:1，形成正方形
            .background(
                color = if (isOn) Background_color.light else Background_color.dim, // 切换颜色
                shape = CircleShape // 圆形形状
            )
            .clickable { onToggle() } // 点击事件触发外部提供的切换函数
    ) {
        // 显示按钮状态文字
        Text(
            text = if (isOn) "ON" else "OFF",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold, // 设置为加粗
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif // 替换为艺术字体的 FontFamily
        )
    }
}


fun toggleState(isOn: Boolean, context: Context, updateState: (Boolean) -> Unit) {
    val appManager = AppManager.getInstance(context)

    if (!isOn) {
        // 打开 VPN 连接：启动 VPN 服务
        val vpnIntent = Intent(context, MyVpnService::class.java)
        // 启动 VPN 服务
        context.startService(vpnIntent)
    } else {
        // 关闭 VPN 连接：停止 VPN 服务
        val vpnIntent = Intent(context, MyVpnService::class.java)
        // 停止 VPN 服务
        context.stopService(vpnIntent)
    }

    // 切换状态：反转当前状态
    val newState = !isOn
    // 通过回调更新新的状态
    updateState(newState)
}
