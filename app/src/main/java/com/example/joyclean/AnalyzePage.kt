package com.example.joyclean

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.joyclean.analyze.ProxyEnabledAppList
import com.example.joyclean.database.AppManager
import com.example.joyclean.setting.AppList

@Composable
fun AnalyzePage(navController: NavController) {

    val context = LocalContext.current
    val appManager = AppManager.getInstance(context)
    var appListEnable by remember { mutableStateOf<List<String>>(emptyList()) }
    var iconListEnable: List<ByteArray?> by remember { mutableStateOf<List<ByteArray?>>(emptyList()) }
    var blockCountList: List<Int> by remember { mutableStateOf<List<Int>>(emptyList()) }
    val refreshKey = remember { mutableIntStateOf(0) }
    val seenAppNames = mutableSetOf<String>()

    LaunchedEffect(refreshKey.value) {
        appManager.getAppsWithProxyEnabledAsync { enabledApps ->
            // 筛选出尚未处理的应用（通过 seenAppNames 去重）
            val newApps = enabledApps.filter { it.appName !in seenAppNames }

            // 筛选出图标不为 null 的应用
            val filteredApps = newApps.filter { it.icon != null }

            // 更新已见集合（确保应用唯一）
            filteredApps.forEach { app -> seenAppNames.add(app.appName) }

            // 构建一个 Map，以 appName 作为键，icon 作为值，去重
            val appIconMap = (appListEnable.zip(iconListEnable) + filteredApps.map { it.appName to it.icon!! })
                .toMap()

            // 更新状态，确保去重
            appListEnable = appIconMap.keys.toList()
            iconListEnable = appIconMap.values.toList()
            blockCountList = List(appListEnable.size) {0}
        }
    }

    PageLayout_Column(
        backgroundColor= colorScheme.background,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    ){
        TopBarWithBackButton(title="数据分析",navController=navController)

        // 主体内容
        ProxyEnabledAppList(appList = appListEnable, iconList = iconListEnable, blockCountList = blockCountList, refreshKey=refreshKey)
    }
}