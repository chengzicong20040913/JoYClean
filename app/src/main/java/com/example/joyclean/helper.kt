package com.example.joyclean

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
// Material Design 3 的主题和排版
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography

@Composable
fun Helper(navController: NavController) {
    PageLayout_Column(
        backgroundColor= Background_color.base_color,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    ){
        TopBarWithBackButton(title="帮助",navController=navController)
        PageLayout_Column(
            padding = Padding(16.dp,16.dp,16.dp,16.dp)
        ) {
            ScrollableTextContent()
        }
    }
}
@Composable
fun ScrollableTextContent() {
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
            modifier = Modifier.fillMaxWidth()
        )



        // 主界面使用指南标题
        Text(
            text = "1. 主界面使用指南",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )

        Text(
            text = """
                主界面如下图所示：
                - 最大的按钮是开关按钮：点击此按钮可开启或关闭广告拦截防护功能。
                - 设置按钮：点击进入设置界面，您可以自定义广告拦截的力度并选择开启防护的软件。
                - 数据分析按钮：点击进入数据分析界面，查看在各个软件中拦截的广告流量数据。
            """.trimIndent(),
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
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )

        Text(
            text = """
                在设置界面中，您可以控制拦截防护的范围和强度，包括：
                - 选择需要开启防护的软件。
                - 调整广告拦截的灵敏度。
            """.trimIndent(),
            modifier = Modifier.fillMaxWidth()
        )

        // 数据分析界面标题
        Text(
            text = "3. 数据分析界面",
            style = MaterialTheme.typography.headlineSmall, // 小标题样式
            modifier = Modifier.padding(vertical = 8.dp) // 上下间距
        )

        Text(
            text = """
                数据分析界面会为您提供详细的统计信息，包括：
                - 当前广告拦截数量。
                - 各个应用中拦截的广告流量比例。
                - 过去一段时间内的广告拦截趋势。
            """.trimIndent(),
            modifier = Modifier.fillMaxWidth()
        )

        // 结束部分
        Text(
            text = "如果您需要更多帮助，请联系我们的支持团队。",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}