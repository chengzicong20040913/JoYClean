package com.example.joyclean

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsPage(navController: NavController) {
    PageLayout_Column(
        backgroundColor= Background_color.base_color,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    ){
        TopBarWithBackButton(title="设置",navController=navController)

        // 主体内容
        Text(
            text = "设置页面内容",
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}

