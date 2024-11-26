package com.example.joyclean.database

import androidx.room.*
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

@Entity(tableName = "App_info") // 建议修改表名为更直观的名字
data class AppInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自动生成的主键
    val appName: String,                              // 应用名称
    @TypeConverters(Converters::class)
    val icon: ByteArray?,                             //图标的存储
    val count : Int=0,                                // 此应用内已经拦截的次数
    val isProxyEnabled: Boolean                       // 是否启用代理
)

@Dao
interface AppDao {
    @Insert  // 增
    fun insertAppInfo(app: AppInfo)

    @Delete  // 删
    fun deleteAppInfo(app: AppInfo)

    @Query("select * from App_info")  // 查
    fun getAllApp(): List<AppInfo>

    @Update  // 改
    fun updateApp(user: AppInfo)

    // 查询某个应用名称是否存在
    @Query("SELECT EXISTS(SELECT 1 FROM App_info WHERE appName = :appName LIMIT 1)")
    fun isAppExists(appName: String): Boolean

    @Query("SELECT * FROM App_info WHERE isProxyEnabled = 1")
    fun getAppsWithProxyEnabled(): List<AppInfo>

    // 查询 isProxyEnabled 为 false 的所有记录
    @Query("SELECT * FROM App_info WHERE isProxyEnabled = 0")
    fun getAppsWithProxyDisabled(): List<AppInfo>

}

@Database(version = 1, entities = [AppInfo::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getDataBase(context: Context): AppDatabase{
            return instance
                ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "any_name_is_ok")
                    .build()
                    .apply { instance = this }
        }
    }
}
class Converters {

    @TypeConverter
    fun fromDrawable(drawable: Drawable?): ByteArray? {
        // 如果 drawable 为 null，直接返回 null
        if (drawable == null) return null

        // 尝试将 Drawable 转换为 Bitmap
        return try {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null // 如果发生异常，返回 null
        }
    }

    @TypeConverter
    fun toDrawable(byteArray: ByteArray?): Drawable? {
        if (byteArray == null) return null

        return try {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            BitmapDrawable(Resources.getSystem(), bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null // 如果发生异常，返回 null
        }
    }
}
