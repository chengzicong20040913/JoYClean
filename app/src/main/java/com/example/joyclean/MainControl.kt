package com.example.joyclean
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll // 用于垂直滚动
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState // 用于记住滚动状态
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme.colorScheme
@Composable
fun ThreeButtonsLayout(navController:NavController) {

    Column(
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .padding(16.dp), // 外边距
        verticalArrangement = Layout.top, // 子元素间距均匀
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中对齐
    ) {
        //Title("欢迎使用JoYClean")
        Image(
            painter = painterResource(id = R.drawable.logo), // 替换为你的图片资源ID
            contentDescription = "顶部图片",
            modifier = Modifier
                .fillMaxWidth() // 图片占满宽度
                .height(100.dp) // 设置图片高度
                .padding(bottom = 16.dp) // 图片底部外边距
        )
        Button(
            onClick = { navController.navigate("settings") },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary, // 按钮背景颜色
            ),
            modifier = Modifier
                .fillMaxWidth() // 按钮占满宽度
                .padding(vertical = 4.dp) // 按钮上下间距
        ) {
            Text(text = "设置")
        }
        Button(
            onClick = { navController.navigate("helper") },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary, // 按钮背景颜色
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "帮助")
        }
        Button(
            onClick = { navController.navigate("data_analyze")},
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary, // 按钮背景颜色
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "数据分析")
        }
    }
}