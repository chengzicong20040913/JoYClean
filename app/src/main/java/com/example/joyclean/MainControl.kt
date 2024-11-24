package com.example.joyclean
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
@Composable
fun ThreeButtonsLayout() {
    Row(
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .padding(16.dp), // 外边距
        horizontalArrangement = Arrangement.SpaceEvenly, // 子元素间距均匀
        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
    ) {
        Button(
            onClick = { /* TODO: 跳转到设置页面 */ },
            modifier = Modifier.weight(1f), // 设置按钮等宽
        ) {
            Text(text = "设置")
        }
        Spacer(modifier = Modifier.width(8.dp)) // 按钮间距
        Button(
            onClick = { /* TODO: 显示帮助内容 */ },
            modifier = Modifier.weight(1f),
        ) {
            Text(text = "帮助")
        }
        Spacer(modifier = Modifier.width(8.dp)) // 按钮间距
        Button(
            onClick = { /* TODO: 打开数据分析功能 */ },
            modifier = Modifier.weight(1f),
        ) {
            Text(text = "数据分析")
        }
    }
}