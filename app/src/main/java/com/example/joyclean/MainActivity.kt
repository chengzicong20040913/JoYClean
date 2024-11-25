package com.example.joyclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.joyclean.ui.theme.JoYCleanTheme
import androidx.compose.foundation.layout.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoYCleanTheme {
                Box(modifier = Modifier.fillMaxSize()) { // 确保父容器占满屏幕
                    MainPage()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview(){
    JoYCleanTheme {
        Box(modifier = Modifier.fillMaxSize()) { // 确保父容器占满屏幕
            MainPage()
        }
    }
}