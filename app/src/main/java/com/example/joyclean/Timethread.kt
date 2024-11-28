package com.example.joyclean

import android.content.Context
import android.content.Intent
import com.example.joyclean.database.AppManager
import androidx.lifecycle.ViewModel
import com.example.joyclean.vpnservice.MyVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _isOn = MutableStateFlow(false)
    private val _elapsedSeconds = MutableStateFlow(0)

    val isOn: StateFlow<Boolean> = _isOn
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    // 切换状态的逻辑
    fun toggleState(context: Context) {
        //val appManager = AppManager.getInstance(context)

        // 反转当前状态
        val newState = !_isOn.value
        _isOn.value = newState

        // 根据新的状态启动或停止 VPN 服务
        val vpnIntent = Intent(context, MyVpnService::class.java)
        if (newState) {
            // 打开 VPN 连接：启动 VPN 服务
            context.startService(vpnIntent)
            startTimer()
        } else {
            // 关闭 VPN 连接：停止 VPN 服务
            context.stopService(vpnIntent)
            resetTimer()
        }
    }

    // 启动定时器
    fun startTimer() {
        viewModelScope.launch {
            while (_isOn.value) {
                delay(1000)  // 每秒延迟
                _elapsedSeconds.value += 1  // 增加计时
            }
        }
    }

    // 重置定时器
    fun resetTimer() {
        _elapsedSeconds.value = 0
    }
}

