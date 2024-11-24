package com.example.joyclean
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
@Composable
fun ToggleCircle(isOn: Boolean, onToggle: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(0.8f) // 宽度占页面宽度的 80%
            .aspectRatio(1f)    // 高度与宽度保持 1:1，形成正方形
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

fun toggleState(isOn: Boolean, updateState: (Boolean) -> Unit) {
    if (isOn) {
        /*TODO:填充关闭连接的逻辑*/
    }
    else {
        /*TODO:填充打开连接的逻辑*/
    }
    val newState = !isOn // 取反状态
    updateState(newState) // 通过回调传递新状态
}