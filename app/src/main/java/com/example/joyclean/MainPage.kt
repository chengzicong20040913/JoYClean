package com.example.joyclean
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text // Material Design 3
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment


fun formatElapsedTime(elapsedSeconds: Int): String {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60

    // 格式化为 "小时:分钟:秒钟"
    // 格式化为 "小时:分钟:秒钟"，分钟和秒钟保持两位
    return String.format("%d:%02d:%02d", hours, minutes, seconds)
}
@Composable
fun MainPage() {
    // 状态变量，用于存储用户输入
    var isOn by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    // 启动定时器
    TimerFlowEffect(isRunning = isOn) {
        elapsedSeconds++ // 每秒增加计时
    }
    // 当 isOn 为 false，清零计时
    if (!isOn) {
        elapsedSeconds = 0
    }

    PageLayout_Column(
        backgroundColor= Background_color.base_color,
        padding = ExtractPaddingValues(),
        verticalArrangement = Layout.top,
    )
    {
        PageLayout_Column(
            backgroundColor= Background_color.tranparent,
            verticalArrangement = Layout.top,
            horizontalAlignment = Layout.left,
            heightFraction = 0.05f
        ) {
            Title("JoYClean")
        }
        PageLayout_Column(
            backgroundColor= Background_color.tranparent,
            verticalArrangement = Layout.depart,
            heightFraction = 0.95f
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Title("欢迎使用JoYClean")
                ToggleCircle(
                    isOn = isOn,
                    onToggle = {
                        toggleState(isOn) { newState ->
                            isOn = newState // 更新状态
                        }
                    }
                )
            }
            if (isOn) {
                val formattedTime = formatElapsedTime(elapsedSeconds)
                Text(
                    text = "已运行时间: ${formattedTime }",
                    style = MaterialTheme.typography.headlineSmall
                )
                Title("广告屏蔽已开启")
            }
            else{
                Title("广告屏蔽已关闭")
            }
            ThreeButtonsLayout()
        }
    }
}

