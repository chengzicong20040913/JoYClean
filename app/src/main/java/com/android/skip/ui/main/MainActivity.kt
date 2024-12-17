package com.android.skip.ui.main

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.skip.MyApp
import com.android.skip.R
import com.android.skip.data.config.ConfigViewModel
import com.android.skip.data.version.ApkVersionViewModel
import com.android.skip.ui.about.AboutActivity
import com.android.skip.ui.components.FlatButton
import com.android.skip.ui.components.ResourceIcon
import com.android.skip.ui.components.RowContent
import com.android.skip.ui.main.disclaimer.DisclaimerDialog
import com.android.skip.ui.main.disclaimer.DisclaimerViewModel
import com.android.skip.ui.main.start.StartAccessibilityViewModel
import com.android.skip.ui.main.start.StartButton
import com.android.skip.ui.main.button.*
import com.android.skip.ui.main.tutorial.TutorialDialog
import com.android.skip.ui.main.tutorial.TutorialViewModel
import com.android.skip.ui.settings.SettingsActivity
import com.android.skip.ui.settings.theme.SwitchThemeViewModel
import com.android.skip.ui.theme.AppTheme
import com.android.skip.ui.webview.WebViewActivity
import com.android.skip.ui.whitelist.WhiteListActivity
import com.android.skip.util.DataStoreUtils
import com.android.skip.vpnservice.*
import dagger.hilt.android.AndroidEntryPoint
import androidx. compose. material3.*
import com.android.skip.ui.vpn.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val startAccessibilityViewModel by viewModels<StartAccessibilityViewModel>()

    private val switchThemeViewModel by viewModels<SwitchThemeViewModel>()

    private val configViewModel by viewModels<ConfigViewModel>()

    private val apkVersionViewModel by viewModels<ApkVersionViewModel>()

    private val tutorialViewModel by viewModels<TutorialViewModel>()

    private val disclaimerViewModel by viewModels<DisclaimerViewModel>()
    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>  // 用于请求 VPN 权限
    // 使用 lazy 委托实现记忆化
    private val vpnPrepareIntent: Intent? by lazy {
        VpnService.prepare(this)  // 获取请求权限的 Intent
    }
    private val viewModel: MainViewModel by viewModels() // 使用 ViewModelProvider 来获取 ViewModel
    private var count_ = 0
    private var DNSIP by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vpnIntent = Intent(this, MyVpnService::class.java)
        vpnPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 用户授权了 VPN 权限，启动 VPN 服务
                vpnIntent.putExtra("DNS", DNSIP)
                startVpnService(vpnIntent)
                Toast.makeText(this, "VPN 已开启", Toast.LENGTH_SHORT).show()
            } else {
                // 用户拒绝授权
                Toast.makeText(this, "VPN 权限请求被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
        setContent {
            val vpnPermissionRequired = viewModel.vpnPermissionRequired.collectAsState().value

            // 使用 LaunchedEffect 监听 vpnPermissionRequired 的变化
            LaunchedEffect(vpnPermissionRequired) {
                if (vpnPermissionRequired) {
                    // 请求 VPN 权限并启动服务
                    requestVpnPermissionAndStart(vpnIntent)
                    count_+=1
                } else if(count_!=0) {
                    // 停止 VPN 服务
                    requestVpnEnd(vpnIntent)
                }
            }
            AppTheme(switchThemeViewModel) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 64.dp, horizontal = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppTitle()
                    VPNButton(viewModel=viewModel)
                    StartButton(startAccessibilityViewModel = startAccessibilityViewModel) {
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                    WhiteListButton {
                        startActivity(Intent(MyApp.context, WhiteListActivity::class.java))
                    }
                    SettingsButton {
                        startActivity(Intent(MyApp.context, SettingsActivity::class.java))
                    }
                    IPsetting(onClick = {}, onIpReceived = { ip ->
                        DNSIP = ip
                    })
                    AboutButton {
                        startActivity(Intent(MyApp.context, AboutActivity::class.java))
                    }
                }
                TutorialDialog(tutorialViewModel, {
                    tutorialViewModel.changeDialogState(false)
                    DataStoreUtils.putSyncData(getString(R.string.store_show_tutorial), false)
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }, {
                    tutorialViewModel.changeDialogState(false)
                    val intent = Intent(MyApp.context, WebViewActivity::class.java).apply {
                        putExtra("url", R.string.tutorial_url)
                    }
                    startActivity(intent)
                })
                DisclaimerDialog(disclaimerViewModel, {
                    disclaimerViewModel.changeDialogState(false)
                    DataStoreUtils.putSyncData(getString(R.string.store_show_disclaimer), false)
                }, {
                    disclaimerViewModel.changeDialogState(false)
                    finish()
                })
            }
        }

        configViewModel.readConfig()
        configViewModel.configPostState.observe(this) {
            configViewModel.loadConfig(it)
        }

        apkVersionViewModel.checkVersion()
    }
    private fun startVpnService(vpnIntent: Intent) {
        startService(vpnIntent)
    }

    private fun endVpnService(vpnIntent: Intent) {
        stopService(vpnIntent)
    }

    // 请求 VPN 权限
    private fun requestVpnPermissionAndStart(vpnIntent: Intent) {
        // 检查 vpnPrepareIntent 是否为 null
        vpnPrepareIntent?.let {
            // 如果 vpnPrepareIntent 不为空，则请求权限
            vpnPermissionLauncher.launch(it)
        } ?: run {
            // 如果 vpnPrepareIntent 为 null，直接启动 VPN 服务
            startVpnService(vpnIntent)
            Toast.makeText(this, "VPN 已开启", Toast.LENGTH_SHORT).show()
        }

    }

    private fun requestVpnEnd(vpnIntent: Intent) {
        // 如果当前有 VPN 连接，结束连接
        endVpnService(vpnIntent)
        // 在这里，你可以执行一些清理工作或显示相关提示
        // 延时停止自己
        Toast.makeText(this, "VPN 已关闭", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
    }
}

@Composable
fun AppTitle() {
    Text(
        text = stringResource(id = R.string.app_name),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun WhiteListButton(onClick: () -> Unit) {
    FlatButton(content = {
        RowContent(R.string.whitelist,
            null,
            { ResourceIcon(iconResource = R.drawable.app_registration) })
    }, onClick = onClick)
}

@Composable
fun SettingsButton(onClick: () -> Unit) {
    FlatButton(content = {
        RowContent(R.string.settings, null, { ResourceIcon(iconResource = R.drawable.settings) })
    }, onClick = onClick)
}

@Composable
fun AboutButton(onClick: () -> Unit = {}) {
    FlatButton(
        content = {
            RowContent(R.string.about, null, { ResourceIcon(iconResource = R.drawable.info) })
        }, onClick = onClick
    )
}
fun formatElapsedTime(elapsedSeconds: Int): String {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60

    // 格式化为 "小时:分钟:秒钟"
    // 格式化为 "小时:分钟:秒钟"，分钟和秒钟保持两位
    return String.format("%d:%02d:%02d", hours, minutes, seconds)
}
@Composable
fun VPNButton(viewModel: MainViewModel){
    val isOn by viewModel.isOn.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        ToggleCircle(
            isOn = isOn,
            onToggle = {
                viewModel.toggleState(context = context)
            }
        )
        if (isOn) {
            val formattedTime = formatElapsedTime(elapsedSeconds)
            Text(
                text = "已运行时间: ${formattedTime }",
                color=MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
@Composable
fun IPsetting(onClick: () -> Unit, onIpReceived: (String) -> Unit) {

    // 当点击按钮时，显示对话框
    var showDialog by remember { mutableStateOf(false) }
    FlatButton(
        content = {
            RowContent(R.string.Ipset, null, { ResourceIcon(iconResource = R.drawable.ip_identifier) })
        }, onClick = {
            showDialog = true
        }
    )

    // 监听 showDialog 变量来弹出对话框
    if (showDialog) {
        // 传递外部的 onIpReceived 回调给对话框
        IPDialog(onDismiss = {
            showDialog = false  // 点击关闭或提交后关闭对话框
        }, onIpReceived = onIpReceived)
    }
}

@Composable
fun IPDialog(onDismiss: () -> Unit, onIpReceived: (String) -> Unit) {
    var ipText by remember { mutableStateOf(TextFieldValue("")) }  // 用于存储用户输入的 DNS IP
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("请输入DNS服务器IP")
        },
        text = {
            TextField(
                value = ipText,
                onValueChange = { ipText = it },
                label = { Text("DNS IP") },
                placeholder = { Text("例如: 8.8.8.8") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // 在确认时通过回调将输入的 IP 返回给父组件
                    onIpReceived(ipText.text)
                    onDismiss()  // 关闭对话框
                }
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )


}


