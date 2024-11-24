package com.example.joyclean
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable

@Composable
fun MainPage() {
    // 状态变量，用于存储用户输入
    var isOn by remember { mutableStateOf(false) }
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
            heightFraction = 0.1f
        ) {
            Title("JoYClean")
        }
        PageLayout_Column(
            backgroundColor= Background_color.tranparent,
            heightFraction = 0.5f
        ) {
            ToggleCircle(
                isOn = isOn,
                onToggle = {
                    toggleState(isOn) { newState ->
                        isOn = newState // 更新状态
                    }
                }
            )
        }
        PageLayout_Column(
            backgroundColor= Background_color.tranparent,
            heightFraction = 0.4f,
            verticalArrangement = Layout.bottom
        ) {
            ThreeButtonsLayout()
        }
    }
}

