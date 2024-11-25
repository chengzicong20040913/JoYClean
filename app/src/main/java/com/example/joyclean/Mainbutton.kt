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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.material.icons.filled.ArrowDropDown
fun calculateDuration(startTime: Long): String {
    val currentTime = System.currentTimeMillis()
    val elapsedMillis = currentTime - startTime

    val hours = (elapsedMillis / (1000 * 60 * 60)) % 24
    val minutes = (elapsedMillis / (1000 * 60)) % 60
    val seconds = (elapsedMillis / 1000) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun ArrowButtonComponent(
    modifier: Modifier = Modifier,
    text: String = "请点击下面的按钮",
    size: Dp = 32.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加外边距
        horizontalAlignment = Alignment.CenterHorizontally // 内容水平居中
    ) {
        // 显示文字
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall, // 使用主题中的样式
            color = MaterialTheme.colorScheme.onBackground, // 文字颜色
            modifier = Modifier.padding(bottom = 8.dp) // 与箭头的间距
        )
        // 显示向下箭头
        Icon(
            imageVector = Icons.Default.ArrowDropDown, // 向下箭头图标
            contentDescription = "向下箭头",
            tint = MaterialTheme.colorScheme.primary, // 箭头颜色
            modifier = Modifier.size(size) // 箭头大小
        )
    }
}
@Composable
fun ToggleCircleWithTime(
    isOn: Boolean,
    onToggle: () -> Unit,
    duration: String // 显示已经开启的持续时间
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // 圆形按钮
        ToggleCircle(isOn = isOn, onToggle = onToggle)

        // 显示持续时间
        if (isOn) {
            Text(
                text = "已开启时间: $duration",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

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