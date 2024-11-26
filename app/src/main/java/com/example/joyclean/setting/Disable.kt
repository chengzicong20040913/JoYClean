package com.example.joyclean.setting

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.joyclean.database.AppManager

@Composable
fun ShowDisable(
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
            .fillMaxHeight(1f) // 限制高度为页面的 60%
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
                text = "未开启保护的应用",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )

            // 右侧按钮
            Button(
                onClick = {
                    alter(context = context, Origin = false, selectedItems = selectedItems)
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
        AppList(appList = appList, iconList = iconList, selectedItems = selectedItems)
    }
}