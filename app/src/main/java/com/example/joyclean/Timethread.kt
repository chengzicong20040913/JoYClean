package com.example.joyclean

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
fun tickerFlow(interval: Long): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        kotlinx.coroutines.delay(interval) // Flow 内部使用 delay 不阻塞外部
    }
}
    .onEach { /* 可以添加额外逻辑 */ }
    .flowOn(Dispatchers.Default) // 在后台线程运行

@Composable
fun TimerFlowEffect(
    isRunning: Boolean,
    onTick: () -> Unit // 每秒调用的函数
) {
    LaunchedEffect(isRunning) {
        if (isRunning) {
            tickerFlow(1000L) // 每 1 秒触发一次
                .collect { onTick() }
        }
    }
}