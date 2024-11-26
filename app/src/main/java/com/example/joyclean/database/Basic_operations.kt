package com.example.joyclean.database

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import androidx.room.Room
import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.coroutines.*

//注意所有有关App的基础操作
class AppManager(private val context: Context) {
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .build()
    }

    private val appDao: AppDao by lazy { appDatabase.appDao() }
    /**
     * 获取所有已安装应用的名称和图标
     * @return 返回一个包含所有应用名称和图标的列表
     */
    fun getAllInstalledAppsAsync(callback: (List<AppItem>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val packageManager = context.packageManager
            val appList = mutableListOf<AppItem>()

            try {
                val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                for (packageInfo in installedPackages) {
                    val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
                    val appIcon: Drawable = packageManager.getApplicationIcon(packageInfo.applicationInfo)
                    appList.add(AppItem(appName, appIcon))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                callback(appList)
            }
        }
    }
    /**获取所有isProxyEnabled为true的表项
    */
    fun getAppsWithProxyEnabledAsync(callback: (List<AppInfo>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enabledApps = appDao.getAppsWithProxyEnabled()
                withContext(Dispatchers.Main) {
                    callback(enabledApps)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(emptyList()) // 返回空列表以防止崩溃
            }
        }
    }


    /**获取所有isProxyEnabled为false的表项
     */
    fun getAppsWithProxyDisabledAsync(callback:(List<AppInfo>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val disabledApps = appDao.getAppsWithProxyDisabled()
                withContext(Dispatchers.Main) {
                    callback(disabledApps)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(emptyList()) // 返回空列表以防止崩溃
            }
        }
    }

    /**
     * 初始化，检查手机上还有没有软件名称
     * 没有在数据库中
     */

    fun checkDatabase() {
        getAllInstalledAppsAsync { installedApps ->
            CoroutineScope(Dispatchers.IO).launch {
                for (app in installedApps) {
                    val appName = app.name
                    val icon = app.icon

                    try {
                        val exists = appDao.isAppExists(appName)
                        if (!exists) {
                            val iconByteArray = try {
                                Converters().fromDrawable(icon)
                            } catch (e: Exception) {
                                null
                            }

                            val appInfo = AppInfo(appName = appName, icon = iconByteArray, isProxyEnabled = false)
                            appDao.insertAppInfo(appInfo)
                        }
                    } catch (e: Exception) {
                        Log.e("DatabaseError", "Failed to insert app: $appName", e)
                    }
                }
            }
        }
    }

    // Companion object to provide global access to the single instance
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AppManager? = null

        // Global access method for the single instance
        fun getInstance(context: Context): AppManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

// 数据类，存储应用的名称和图标
data class AppItem(val name: String, val icon: Drawable)

