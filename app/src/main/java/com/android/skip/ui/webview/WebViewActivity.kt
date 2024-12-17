package com.android.skip.ui.webview

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.skip.ui.settings.theme.SwitchThemeViewModel
import com.android.skip.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.android.skip.R
@AndroidEntryPoint
class WebViewActivity : AppCompatActivity() {

    private val switchThemeViewModel by viewModels<SwitchThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(switchThemeViewModel) {
                WebViewPage("")
                /*
                val url = intent.getIntExtra("url", -1)
                if (url != -1) {
                    WebViewPage(url = getString(url))
                } else {
                    val address = intent.getStringExtra("address")
                    if (address != null) {
                        WebViewPage(url = address)
                    }
                }*/
            }
        }
    }

    @Composable
    fun WebViewPage(url: String) {
        /*
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )*/
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
}