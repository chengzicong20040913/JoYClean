package com.android.skip.ui.main.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx. compose. runtime. remember


val stopcolor =Color(0xFF808080)
val startcolor=Color(0xFF1E4377)
@Composable
fun ToggleCircle(isOn: Boolean, onToggle: () -> Unit) {
    // 外部渐变方向（外层由深色到浅色）
    val transitionState = remember { Animatable(0f) }

    // 动画过渡，控制渐变变化
    LaunchedEffect(isOn) {
        // 开始动画
        transitionState.animateTo(
            targetValue = if (isOn) 1f else 0f,
            animationSpec = tween(durationMillis = 2000) // 1秒动画
        )
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.8f) // 宽度占页面宽度的 80%
            .aspectRatio(1.0f)   // 高度与宽度保持 1:1，形成正方形
            .clip(CircleShape)
            .clickable { onToggle() },
        shape = CircleShape,
        color = Color.Transparent // 设置透明的填充色
    ) {
        Box(
            contentAlignment = Alignment.Center, // 确保内容居中
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // 使用 `transitionState` 来控制渐变的透明度或方向过渡
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0.5f, transitionState.value.coerceAtLeast(0.5f), transitionState.value.coerceAtLeast(0.5f)), // 柔和的灰色渐变
                            Color(0.5f, transitionState.value.coerceAtLeast(0.5f), transitionState.value.coerceAtLeast(0.5f)) // 更柔和的灰色
                        )
                    )
                ) // 内部背景渐变
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


