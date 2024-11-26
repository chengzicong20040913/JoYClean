package com.example.joyclean
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import android.util.Log // 需要导入
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavController
// 全局定义的布局工具类
object Layout {
    val centerV = Arrangement.Center
    val top = Arrangement.Top
    val bottom = Arrangement.Bottom
    val centerW =Alignment.CenterHorizontally
    val left = Alignment.Start
    val right = Alignment.End
    val depart = Arrangement.SpaceBetween
}
object Background_color{
    val base_color = Color(0xFFDFE5DC)
    val tranparent = Color(0x00000000)
    val dim = Color.Gray
    val light = Color(0xFFB3E5FC)
    val red =Color.Red
}
data class Padding(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp
)
@Composable
fun ExtractPaddingValues(): Padding {
    val paddingValues = WindowInsets.statusBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current // 获取布局方向（LTR/RTL）

    val startPadding = paddingValues.calculateStartPadding(layoutDirection)
    val topPadding = paddingValues.calculateTopPadding()
    val endPadding = paddingValues.calculateEndPadding(layoutDirection)
    val bottomPadding = paddingValues.calculateBottomPadding()
    return Padding(
        start = startPadding,
        top = topPadding,
        end = endPadding,
        bottom = bottomPadding
    )
}
@Composable
fun PageLayout_Column(
    verticalArrangement: Arrangement.Vertical = Arrangement.Center, // 默认居中
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally, // 默认居中
    padding: Padding = Padding(0.dp, 0.dp, 0.dp, 0.dp), // 默认无内边距
    widthFraction: Float = 1.0f,
    heightFraction: Float = 1.0f,
    backgroundColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction) // 动态设置宽度占比
            .fillMaxHeight(heightFraction) // 动态设置高度占比
            .background(backgroundColor) // 动态设置背景颜色
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.start,
                    top = padding.top,
                    end = padding.end,
                    bottom = padding.bottom
                ),
            verticalArrangement = verticalArrangement, // 动态控制垂直对齐
            horizontalAlignment = horizontalAlignment // 动态控制水平对齐
        ) {
            content() // 动态内容
        }
    }
}
@Composable
fun PageLayout_Row(
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically, // 垂直对齐方式
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween, // 水平排列方式
    padding: PaddingValues = PaddingValues(16.dp), // 使用 Compose 内置的 PaddingValues
    widthFraction: Float = 1f, // 控制宽度占比，默认填满父容器
    heightFraction: Float = 1f, // 控制高度占比，默认填满父容器
    backgroundColor: Color = Color.Transparent, // 背景颜色
    content: @Composable RowScope.() -> Unit // 使用 RowScope 提供内容作用域
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction) // 动态设置宽度占比
            .fillMaxHeight(heightFraction) // 动态设置高度占比
            .background(backgroundColor) // 动态设置背景颜色
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding), // 使用 PaddingValues 简化设置
            horizontalArrangement = horizontalArrangement, // 动态水平排列
            verticalAlignment = verticalAlignment // 动态垂直对齐
        ) {
            content() // 动态内容
        }
    }
}
@Composable
fun TopBarWithBackButton(
    title: String,
    navController: NavController,
    onBackClick: () -> Unit = { navController.navigate("main") } // 默认点击行为
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick, // 返回按钮的点击行为
            modifier = Modifier.size(48.dp) // 调整按钮区域大小
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black // 设置箭头颜色
            )
        }

        Spacer(modifier = Modifier.width(8.dp)) // 图标和标题之间的间距

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = Color.Black // 设置标题颜色
        )
    }
}
