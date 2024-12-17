package com.android.skip.ui.main.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

val stopcolor =Color(0xFF808080)
val startcolor=Color(0xFF1E4377)
@Composable
fun ToggleCircle(isOn: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.8f) // 宽度占页面宽度的 80%
            .aspectRatio(1.0f)   // 高度与宽度保持 1:1，形成正方形
            .clickable { onToggle() }, // 点击事件触发外部提供的切换函数
        shape = CircleShape, // 使用圆形形状
        color = if (isOn) startcolor else stopcolor // 默认文字颜色为白色
    ) {
        Box(
            contentAlignment = Alignment.Center, // 确保内容居中
            modifier = Modifier.fillMaxSize() // 让 Box 填满 Surface
        ) {
            // 显示按钮状态文字
            Text(
                text = if (isOn) "ON" else "OFF",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold, // 设置为加粗
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif // 使用艺术字体的 FontFamily
            )
        }
    }
}