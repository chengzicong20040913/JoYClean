package com.example.joyclean
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll // 用于垂直滚动
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.foundation.rememberScrollState // 用于记住滚动状态

@Composable
fun ThreeButtonsLayout() {
    var showDialog by remember { mutableStateOf(false) } // 控制对话框显示/隐藏

    // 用来显示帮助对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // 点击空白区域关闭对话框
            title = { Text("帮助") },
            text = {
                // 使用 Column 来包含可滚动的内容
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // 给内容一个高度
                        .verticalScroll(rememberScrollState()) // 使内容区域可滚动
                ) {
                    // 模拟长文本
                    Text("这是帮助内容。你可以在这里放置更多信息。" +
                            "\n\n" +
                            "这段文本非常长，因此我们可以看到滚动效果。如果你添加更多内容，文本就会变得非常长，用户可以通过滚动来查看剩余的部分。\n\n" +
                            "继续滚动并添加更多的示例文本。\n\n" +
                            "更多信息：\n\n" +
                            "1. 设置\n" +
                            "2. 帮助\n" +
                            "3. 数据分析\n\n" +
                            "继续滚动...\n\n" +
                            "结束部分"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false } // 关闭对话框
                ) {
                    Text("确认")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .padding(16.dp), // 外边距
        verticalArrangement = Layout.top, // 子元素间距均匀
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中对齐
    ) {
        //Title("欢迎使用JoYClean")
        Button(
            onClick = { /* TODO: 跳转到设置页面 */ },
            modifier = Modifier
                .fillMaxWidth() // 按钮占满宽度
                .padding(vertical = 4.dp) // 按钮上下间距
        ) {
            Text(text = "设置")
        }
        Button(
            onClick = { showDialog = true/* TODO: 显示帮助内容 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "帮助")
        }
        Button(
            onClick = { /* TODO: 打开数据分析功能 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(text = "数据分析")
        }
    }
}