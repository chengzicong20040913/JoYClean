package com.example.joyclean
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout

@Composable
fun ThreeButtonsLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .padding(16.dp), // 外边距
        verticalArrangement = Layout.top, // 子元素间距均匀
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中对齐
    ) {
        //Title("欢迎使用JoYClean")
        Button(
            onClick = { /* TODO: 跳转到设置页面 */ },
            modifier = Modifier
                .fillMaxWidth() // 按钮占满宽度
                .padding(vertical = 4.dp) // 按钮上下间距
        ) {
            Text(text = "设置")
        }
        Button(
            onClick = { /* TODO: 显示帮助内容 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "帮助")
        }
        Button(
            onClick = { /* TODO: 打开数据分析功能 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "数据分析")
        }
    }
}