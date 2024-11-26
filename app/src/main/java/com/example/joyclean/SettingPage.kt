package com.example.joyclean

import android.graphics.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.NavController
import com.example.joyclean.database.*

import androidx.compose.runtime.*
import com.example.joyclean.setting.*


@Composable
fun SettingsPage(navController: NavController) {
    // 调用方法
    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)
    var appListDisable by remember { mutableStateOf<List<String>>(emptyList()) }
    var appListEnable by remember { mutableStateOf<List<String>>(emptyList()) }
    var iconListDisable: List<ByteArray?> by remember { mutableStateOf<List<ByteArray?>>(emptyList()) }
    var iconListEnable: List<ByteArray?> by remember { mutableStateOf<List<ByteArray?>>(emptyList()) }
    val seenAppNames = mutableSetOf<String>()
    val refreshKey = remember { mutableIntStateOf(0) }
    appManager.checkDatabase()
    // 加载数据
    LaunchedEffect(refreshKey.value) {

        appManager.getAppsWithProxyDisabledAsync { enabledApps ->
            // 筛选出尚未处理的应用（通过 seenAppNames 去重）
            val newApps = enabledApps.filter { it.appName !in seenAppNames }

            // 筛选出图标不为 null 的应用
            val filteredApps = newApps.filter { it.icon != null }

            // 更新已见集合（确保应用唯一）
            filteredApps.forEach { app -> seenAppNames.add(app.appName) }

            // 构建一个 Map，以 appName 作为键，icon 作为值，去重
            val appIconMap = (appListDisable.zip(iconListDisable) + filteredApps.map { it.appName to it.icon!! })
                .toMap()

            // 更新状态，确保去重
            appListDisable = appIconMap.keys.toList()
            iconListDisable = appIconMap.values.toList()
        }
        appManager.getAppsWithProxyEnabledAsync { enabledApps ->
            // 筛选出尚未处理的应用（通过 seenAppNames 去重）
            val newApps = enabledApps.filter { it.appName !in seenAppNames }

            // 筛选出图标不为 null 的应用
            val filteredApps = newApps.filter { it.icon != null }

            // 更新已见集合（确保应用唯一）
            filteredApps.forEach { app -> seenAppNames.add(app.appName) }

            // 构建一个 Map，以 appName 作为键，icon 作为值，去重
            val appIconMap = (appListEnable.zip(iconListDisable) + filteredApps.map { it.appName to it.icon!! })
                .toMap()

            // 更新状态，确保去重
            appListEnable = appIconMap.keys.toList()
            iconListEnable = appIconMap.values.toList()
        }
    }

    PageLayout_Column(
        backgroundColor= Background_color.base_color,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    ){
        TopBarWithBackButton(title="设置",navController=navController)

        // 主体内容
        //通过数据库查询图标并显示
        PageLayout_Column(
            backgroundColor= Background_color.tranparent,
            verticalArrangement = Layout.top,
            horizontalAlignment = Layout.left,
            heightFraction = 1.0f
        ) {
            ShowEnable(
                context = context,
                appList = appListEnable,
                iconList = iconListEnable,
                refreshKey = refreshKey
            )
            ShowDisable(
                context = context,
                appList = appListDisable,
                iconList = iconListDisable,
                refreshKey = refreshKey
            )
        }
    }
}




