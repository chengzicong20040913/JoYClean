package com.example.joyclean

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController,viewModel: MainViewModel) {
    //val viewModel: MainViewModel = MainViewModel()  // 获取 ViewModel 实例
    NavHost(
        navController = navController,
        startDestination = "main" // 默认起始页面
    ) {
        // 主页面
        composable("main") { MainPage(navController,viewModel=viewModel) }
        // 设置页面
        composable("settings") { SettingsPage(navController)}
        //数据分析页面
        composable("data_analyze"){AnalyzePage(navController)}
        //帮助页面
        composable("helper"){Helper(navController)}
    }
}
