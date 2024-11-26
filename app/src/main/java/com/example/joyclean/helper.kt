package com.example.joyclean

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*


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
        Text(
            text = "这是帮助内容。你可以在这里放置更多信息。" +
                    "\n\n" +
                    "这段文本非常长，因此我们可以看到滚动效果。如果你添加更多内容，文本就会变得非常长，用户可以通过滚动来查看剩余的部分。\n\n" +
                    "继续滚动并添加更多的示例文本。\n\n" +
                    "更多信息：\n\n" +
                    "1. 设置\n" +
                    "2. 帮助\n" +
                    "3. 数据分析\n\n" +
                    "继续滚动...\n\n" +
                    "结束部分\n" +
                    "这是帮助内容。你可以在这里放置更多信息。" +
                    "\n\n" +
                    "这段文本非常长，因此我们可以看到滚动效果。如果你添加更多内容，文本就会变得非常长，用户可以通过滚动来查看剩余的部分。\n\n" +
                    "继续滚动并添加更多的示例文本。\n\n" +
                    "更多信息：\n\n" +
                    "1. 设置\n" +
                    "2. 帮助\n" +
                    "3. 数据分析\n\n" +
                    "继续滚动...\n\n" +
                    "结束部分\n" +
                    "这是帮助内容。你可以在这里放置更多信息。" +
                    "\n\n" +
                    "这段文本非常长，因此我们可以看到滚动效果。如果你添加更多内容，文本就会变得非常长，用户可以通过滚动来查看剩余的部分。\n\n" +
                    "继续滚动并添加更多的示例文本。\n\n" +
                    "更多信息：\n\n" +
                    "1. 设置\n" +
                    "2. 帮助\n" +
                    "3. 数据分析\n\n" +
                    "继续滚动...\n\n" +
                    "结束部分",
            modifier = Modifier.fillMaxWidth()
        )
    }
}