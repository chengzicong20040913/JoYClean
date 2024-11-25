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
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoYCleanTheme {
                Surface {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController) // 加载导航图
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview(){
    JoYCleanTheme {
        Surface {
            val navController = rememberNavController()
            AppNavGraph(navController = navController) // 加载导航图
        }
    }
}