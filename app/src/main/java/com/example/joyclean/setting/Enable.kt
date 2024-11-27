package com.example.joyclean.setting

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavController
import com.example.joyclean.database.AppManager

@Composable
fun ShowEnable(
    context: Context,
    appList: List<String>,
    iconList: List<ByteArray?>,
    refreshKey: MutableState<Int>
) {
    // 管理选中状态
    val selectedItems = remember { mutableStateOf<Set<String>>(emptySet()) }
    Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f) // 限制高度为页面的 40%
            ){
        // 顶部蓝色条状结构
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标题
            Text(
                text = "已开启保护的应用",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )

            // 右侧按钮
            Button(
                onClick = {
                    // 移除选中的逻辑
                    alter(context = context, Origin = true, selectedItems = selectedItems)
                    refreshKey.value++
                    selectedItems.value = emptySet() // 清空选中状态
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(text = "确认移除")
            }
        }

        // 下方的 AppList
        AppList(appList = appList, iconList = iconList, selectedItems = selectedItems,refreshKey=refreshKey)
    }
}
