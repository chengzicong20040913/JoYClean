package com.example.joyclean

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
@Composable
fun SettingsPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "设置页面", modifier = Modifier.padding(16.dp))

        Button(
            onClick = { navController.navigate("main") }, // 使用 NavController 返回主页面
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "返回主页面")
        }
    }
}

