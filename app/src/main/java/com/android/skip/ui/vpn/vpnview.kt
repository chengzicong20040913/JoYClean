package com.android.skip.ui.vpn

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _isOn = MutableStateFlow(false)
    private val _elapsedSeconds = MutableStateFlow(0)
    private val _vpnPermissionRequired = MutableStateFlow(false)  // 用于表示是否需要VPN权限

    val isOn: StateFlow<Boolean> = _isOn
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds
    val vpnPermissionRequired: StateFlow<Boolean> = _vpnPermissionRequired  // 公开给外部使用

    // 切换状态的逻辑
    fun toggleState(context: Context) {

        // 反转当前状态
        val newState = !_isOn.value
        _isOn.value = newState

        if (newState) {
            //请求VPN服务
            _vpnPermissionRequired.value = true
            startTimer()
        }
        else {
            // 停止 VPN 服务
            _vpnPermissionRequired.value = false
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