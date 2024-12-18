package com.android.skip.ui.about

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.android.skip.MyApp
import com.android.skip.R
import com.android.skip.data.config.ConfigViewModel
import com.android.skip.data.download.ApkDownloadViewModel
import com.android.skip.data.version.ApkVersionViewModel
import com.android.skip.dataclass.VersionState
import com.android.skip.ui.about.config.ConfigVersionButton
import com.android.skip.ui.about.download.ApkDownloadDialog
import com.android.skip.ui.about.version.ApkVersionButton
import com.android.skip.ui.components.FlatButton
import com.android.skip.ui.components.ResourceIcon
import com.android.skip.ui.components.RowContent
import com.android.skip.ui.components.ScaffoldPage
import com.android.skip.ui.components.expandMenuItems
import com.android.skip.ui.settings.recent.RecentButton
import com.android.skip.ui.settings.recent.RecentViewModel
import com.android.skip.ui.settings.theme.SwitchThemeViewModel
import com.android.skip.ui.theme.AppTheme
import com.android.skip.ui.webview.WebViewActivity
import com.android.skip.util.DataStoreUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {
    private val configViewModel by viewModels<ConfigViewModel>()

    private val switchThemeViewModel by viewModels<SwitchThemeViewModel>()

    private val apkVersionViewModel by viewModels<ApkVersionViewModel>()

    private val apkDownloadViewModel by viewModels<ApkDownloadViewModel>()
    //private val recentViewModel by viewModels<RecentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(switchThemeViewModel) {
                ScaffoldPage(R.string.about,{finish()},{
                    HelpDetail()
                })
            }
        }

        var latestVersion = ""
        apkVersionViewModel.versionPostState.observe(this) {
            if (it.status == VersionState.DISCOVER_LATEST) {
                val isNotUpdate =
                    DataStoreUtils.getSyncData(getString(R.string.store_not_update), false)
                if (!isNotUpdate) {
                    apkDownloadViewModel.changeDialogState(true)
                    latestVersion = it.latestVersion
                }
            }
        }

        apkDownloadViewModel.apkDownloadProcess.observe(this) {
            if (it == 100) {
                apkDownloadViewModel.changeDialogState(false)

                val filename = "SKIP-v$latestVersion.apk"
                val path = "${MyApp.context.filesDir}/apk"
                val apkFile = File(path, filename)
                val apkUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    apkFile
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
        }

        configViewModel.configPostState.observe(this) {
            configViewModel.loadConfig(it)
        }
    }
}

@Composable
fun AboutGithub(onClick: () -> Unit) {
    FlatButton(content = {
        RowContent(
            title = R.string.about_github_title,
            subTitle = R.string.about_github_subtitle
        )
    }, onClick = onClick)
}

@Composable
fun AboutDocs(onClick: () -> Unit) {
    FlatButton(content = {
        RowContent(
            title = R.string.about_docs_title,
            subTitle = R.string.about_docs_subtitle
        )
    }, onClick = onClick)
}
@Composable
fun HelpDetail() {
    val scrollState = rememberScrollState() // 创建滚动状态
    Column(
        modifier = Modifier
            .fillMaxSize() // 填满整个屏幕
            .padding(16.dp) // 添加内边距
            .verticalScroll(scrollState) // 允许垂直滚动
    ) {
        // 显示标题文本
        Text(
            text = "欢迎使用JoYClean！",
            style = MaterialTheme.typography.headlineLarge, // 使用大标题样式
            color=MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // 添加底部间距
        )
        // 插入图片
        Image(
            painter = painterResource(id = R.drawable.imagelogo), // 替换为实际图片资源ID
            contentDescription = "主界面图片",
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // 控制图片高度
        )
        Text(
            text = """
                JoYClean是由JYC团队研发的基于流量分析的强力广告拦
                截软件，只要点击屏幕，即刻告别广告烦恼！现在就由我来告诉你这个软件要怎么使用喵！
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )



        // 主界面使用指南标题
        Text(
            text = "1. 主界面使用指南",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距

        )

        Text(
            text = """
                主界面如下图所示：
                - 最大的按钮是开关按钮：点击此按钮可开启或关闭广告拦截防护功能。
                - 设置按钮：点击进入设置界面，您可以自定义广告拦截的力度并选择开启防护的软件。
                - 数据分析按钮：点击进入数据分析界面，查看在各个软件中拦截的广告流量数据。
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.xpage), // 替换为实际图片资源ID
            contentDescription = "主界面图片",
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp) // 控制图片高度
        )

        // 设置界面标题
        Text(
            text = "2. 设置界面",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )
        Image(
            painter = painterResource(id = R.drawable.setting), // 替换为实际图片资源ID
            contentDescription = "主界面图片",
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp) // 控制图片高度
        )

        Text(
            text = """
                在设置界面中，您可以控制拦截防护的范围。按照图上的指示，对希望调整的应用进行勾选，就能选择是否对他开启拦截广告服务啦~
                不过很遗憾的是，由于Room数据库缓存设定有点奇怪，每次对应用进行修改之后，尽管实际上的防护已经开启或者关闭，需要退出页面重新进入，图形页面才会正常显示~
            """,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        // 数据分析界面标题
        Text(
            text = "3. 数据分析界面",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )

        Text(
            text = """
                数据分析界面会为您提供详细的统计信息，包括：
                - 当前广告拦截数量。
                - 各个应用中拦截的广告流量比例。
                - 过去一段时间内的广告拦截趋势。
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        // 结束部分
        Text(
            text = "如果您需要更多帮助，请联系我们的支持团队。",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}