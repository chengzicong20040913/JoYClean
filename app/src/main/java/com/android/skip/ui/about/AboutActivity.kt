package com.android.skip.ui.about

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

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
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("欢迎使用 JoYClean \uD83E\uDD73")
                }
            },
            fontSize = 28.sp,
            // style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // 添加底部间距
        )
//        // 插入图片
//        Image(
//            painter = painterResource(id = R.drawable.imagelogo), // 替换为实际图片资源ID
//            contentDescription = "主界面图片",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp) // 控制图片高度
//        )
        Text(
            text = """
                JoYClean 是由 JYC Studio 团队研发的基于流量分析的强力广告拦截软件，只要点击屏幕，即刻告别广告烦恼！现在就由我来告诉你这个软件要怎么使用喵！
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
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.mainpage1), // 替换为实际图片资源ID
                contentDescription = "开启模拟点击3",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mainpage2), // 替换为实际图片资源ID
                contentDescription = "开启模拟点击4",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        Text(
            text = """
                
                主界面如上图所示，界面上有下面这些按钮：
                
                （1）流量屏蔽开关：点击此按钮可开启或关闭基于流量分析的广告拦截功能，开启后按钮下会显示已运行时间。
                
                （2）模拟点击开关：点击此按钮可开启或关闭基于模拟点击的广告拦截功能，其中开启功能时需要在设置中开启该应用的无障碍服务权限。
                
                （3）应用白名单：点击进入应用白名单页面，可以自主设置对哪些应用不开启模拟点击广告屏蔽功能。
                
                （4）设置：点击进入设置界面，您可以对软件的一些功能进行自定义。
                
                （5）DNS IP配置：点击后可以在文本框内输入您自主选择的DNS服务器，提升软件基于流量分析的广告拦截效果。
                
                （6）帮助：进入当前您所在的帮助页面
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        // 设置界面标题
        Text(
            text = "2. 开启模拟点击屏蔽",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 整体布局的边距
        ) {
            // 第一行
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.autoclick1), // 替换为实际图片资源ID
                    contentDescription = "开启模拟点击1",
                    modifier = Modifier
                        .weight(1f) // 让图片在行内平分宽度
                        .padding(end = 8.dp, bottom = 16.dp) // 两张图片之间的间距
                )
                Image(
                    painter = painterResource(id = R.drawable.autoclick2), // 替换为实际图片资源ID
                    contentDescription = "开启模拟点击2",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, bottom = 16.dp)
                )
            }
            // 第二行
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.autoclick3), // 替换为实际图片资源ID
                    contentDescription = "开启模拟点击3",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.autoclick4), // 替换为实际图片资源ID
                    contentDescription = "开启模拟点击4",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        }

        Text(
            text = """
                在主页面点击模拟点击开关后，您的设备会自动跳转到系统设置的无障碍页面，您只需要按照上面图片的示意，依次点击页面底部的“已安装的服务”、JoYClean、开启按钮，勾选“我已知风险”后开启，然后您就可以享受 JoYClean 基于模拟点击快速跳过软件开屏广告的便捷功能。
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        // 数据分析界面标题
        Text(
            text = "3. 设置界面",
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
                
                在主页面点击设置进入设置页面后，可以看到如上图这些选项：
                
                「自动更新」应用会在后台每隔 12 小时左右检查是否有新版本并自动更新
                
                「后台任务隐藏」开启后，在后台任务窗口中，JoYClean 应用窗口会被隐藏。
                
                「是否允许提示」开启此功能以及系统设置中本软件的“允许通知”，当成功执行模拟点击操作时，会显示提示“已为您跳过广告”。
                
                「严格模式」如果开启， JoYClean 会完全遵循配置文件中定义的规则，因此，如果配置文件中没有定义规则的应用，就不会执行模拟点击操作；如果关闭，JoYClean 会默认搜索界面中的“跳过”关键字，如果找到，就会执行模拟点击操作。因此，即使没有在配置文件中定义规则的应用，也可以实现跳过广告的功能。但同时，也会存在误触发的情况出现。
                
                「自定义配置」点击可以输入自定义配置规则，支持输入纯文本的 YAML 格式或 JSON 格式，或可以返回 JSON 或 YAML 文本的 URL 链接。
                
                「切换主题」点击可以选择浅色主题、深色主题、跟随系统。
            """.trimIndent(),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        val uriHandler = LocalUriHandler.current // 用于处理超链接

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("如果您需要更多帮助，请联系我们的支持团队 ")
                }
                pushStringAnnotation(tag = "URL", annotation = "https://github.com/chengzicong20040913/JoYClean")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                    append("JYC Studio")
                }
                pop()
            },
            fontSize = 12.sp, // 设置字号
            textAlign = TextAlign.Center, // 文本居中
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    uriHandler.openUri("https://github.com/chengzicong20040913/JoYClean") // 点击打开超链接
                },
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}