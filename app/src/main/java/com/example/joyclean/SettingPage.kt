package com.example.joyclean

import android.graphics.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.joyclean.database.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign


@Composable
fun SettingsPage(navController: NavController) {
    // 调用方法
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)
    var appList by remember { mutableStateOf("") }
    var lastAppList by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    appManager.checkDatabase()
    PageLayout_Column(
        backgroundColor= Background_color.base_color,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    ){
        TopBarWithBackButton(title="设置",navController=navController)

        // 主体内容
        //通过数据库查询图标并显示
        appManager.getAppsWithProxyDisabledAsync { enabledApps ->
            val newAppList = enabledApps.joinToString(separator = "\n") { app -> app.appName }
            if (newAppList != lastAppList) { // 检查是否发生了实际更新
                lastAppList = newAppList
                appList = newAppList
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize() // 填满整个屏幕
                .padding(16.dp) // 添加内边距
                .verticalScroll(scrollState) // 允许垂直滚动
        ) {
            Text(
                text = appList,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
fun ProxyDisabledAppsScreen(appManager: AppManager) {
    // 定义一个 State 保存应用数据
    val appsState = remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    // 调用异步方法
    LaunchedEffect(Unit) {
        appManager.getAppsWithProxyDisabledAsync { disabledApps ->
            appsState.value = disabledApps // 更新 State
        }
    }

    // 显示应用列表
    LazyColumn {
        items(appsState.value) { app ->
            AppItemView(app)
        }
    }
}
@Composable
fun AppItemView(app: AppInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 显示图标
        app.icon?.let { iconByteArray ->
            val bitmap = BitmapFactory.decodeByteArray(iconByteArray, 0, iconByteArray.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "${app.appName} Icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 显示应用名称
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}




