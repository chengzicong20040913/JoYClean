package com.example.joyclean.database

import androidx.room.*
import android.content.Context
@Entity(tableName = "App_info") // 建议修改表名为更直观的名字
data class AppInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自动生成的主键
    val appName: String,                              // 应用名称
    val appLogo: String,                              // logo 的路径或 URL（可以是文件路径或网络地址）
    val isProxyEnabled: Boolean                       // 是否启用代理
)
@Dao
interface UserDao {

    @Insert  // 增
    fun insertAppInfo(user: AppInfo)

    @Delete  // 删
    fun deleteAppInfo(user: AppInfo)

    @Query("select * from App_info")  // 查
    fun getAllUser(): List<AppInfo>

    @Update  // 改
    fun updateUser(user: AppInfo)

}
@Database(version = 1, entities = [AppInfo::class], exportSchema = false)
abstract class MyDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: MyDataBase? = null
        @Synchronized
        fun getDataBase(context: Context): MyDataBase {
            return instance
                ?: Room.databaseBuilder(context.applicationContext, MyDataBase::class.java, "any_name_is_ok")
                    .build()
                    .apply { instance = this }
        }
    }
}