package com.example.joyclean


import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.joyclean.ui.theme.JoYCleanTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.joyclean.vpnservice.MyVpnService
import android.widget.Toast
import androidx.activity.result.*
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlin.getValue
import android.os.Handler
import android.os.Looper

class MainActivity : ComponentActivity() {

    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>  // 用于请求 VPN 权限
    // 使用 lazy 委托实现记忆化
    private val vpnPrepareIntent: Intent? by lazy {
        VpnService.prepare(this)  // 获取请求权限的 Intent
    }
    private val viewModel: MainViewModel by viewModels() // 使用 ViewModelProvider 来获取 ViewModel
    private var count_ = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vpnIntent = Intent(this, MyVpnService::class.java)
        // 初始化 vpnPermissionLauncher
        vpnPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 用户授权了 VPN 权限，启动 VPN 服务
                startVpnService(vpnIntent)
                Toast.makeText(this, "VPN 已开启", Toast.LENGTH_SHORT).show()
            } else {
                // 用户拒绝授权
                Toast.makeText(this, "VPN 权限请求被拒绝", Toast.LENGTH_SHORT).show()
            }
        }


        setContent {
            // 直接访问 State 的值
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

            JoYCleanTheme {
                Surface {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
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
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview() {
    // 这里我们创建一个模拟的 viewModel
    val viewModel = MainViewModel() // 创建一个新的 ViewModel 实例

    JoYCleanTheme {
        Surface {
            val navController = rememberNavController()
            AppNavGraph(navController = navController, viewModel = viewModel) // 使用模拟的 viewModel
        }
    }
}


