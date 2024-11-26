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
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.joyclean.database.*
import android.graphics.*
import androidx.compose.runtime.mutableStateOf
import com.example.joyclean.setting.*
import kotlinx.coroutines.*
import kotlin.collections.plus
import kotlin.collections.toMap

@Composable
fun AppList(
    appList: List<String>,
    iconList: List<ByteArray?>,
    selectedItems: MutableState<Set<String>>
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        itemsIndexed(appList) { index, appName ->
            val isChecked = selectedItems.value.contains(appName)

            AppItem(
                appName = appName,
                iconData = iconList.getOrNull(index), // 避免越界
                isChecked = isChecked,
                onCheckedChange = { checked ->
                    selectedItems.value = if (checked) {
                        selectedItems.value + appName // 添加到选中集合
                    } else {
                        selectedItems.value - appName // 从选中集合中移除
                    }
                }
            )
        }
    }
}


@Composable
fun AppItem(
    appName: String,
    iconData: ByteArray?,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 复选框
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 8.dp)
        )

        // 显示图标
        if (iconData != null) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(
                    iconData,
                    0,
                    iconData.size
                ).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        } else {
            // 提供默认图标或占位
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        // 显示应用名称
        Text(
            text = appName,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


fun alter(
    context: Context,
    Origin: Boolean,
    selectedItems: MutableState<Set<String>>
) {
    val appManager = AppManager.getInstance(context)

    // 按需修改应用的 isProxyEnabled 值

    val appNames = selectedItems.value.toList() // 获取选中应用的名称列表

    if (appNames.isNotEmpty()) {
            // 根据 Origin 执行不同的逻辑
        appNames.forEach { appName ->
            appManager.getAppByNameAsync(appName) { appInfo ->
                if (appInfo != null) {
                    val newProxyStatus = !Origin // Origin=false -> set to true; Origin=true -> set to false
                    updateProxyStatus(context, appInfo, newProxyStatus)
                }
            }
        }
    }

}

/**
 * 更新应用的代理状态
 * @param context 上下文
 * @param appInfo 需要更新的应用信息
 * @param isProxyEnabled 是否启用代理
 */
fun updateProxyStatus(context: Context, appInfo: AppInfo, isProxyEnabled: Boolean) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 假设 appDao 提供了更新方法
            val appManager = AppManager.getInstance(context)
            appManager.updateAppProxyStatus(appInfo.appName, isProxyEnabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



