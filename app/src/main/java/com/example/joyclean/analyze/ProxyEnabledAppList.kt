package com.example.joyclean.analyze

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.joyclean.setting.alter
import androidx.compose.foundation.layout.Spacer

@Composable
fun ProxyEnabledAppList(
    appList: List<String>,
    iconList: List<ByteArray?>,
    blockCountList: List<Int>,
    refreshKey: MutableState<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧标题
        Text(
            text = "已开启保护的应用及拦截次数",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "应用程序",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        // 使用 Spacer 占据剩余空间使得被拦截次数右对齐
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "被拦截次数",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    key(refreshKey.value) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
        ) {
            itemsIndexed(appList) { index, appName ->
                ProxyEnabledAppItem(
                    appName = appName,
                    iconData = iconList.getOrNull(index), // 避免越界
                    blockCount = blockCountList.get(index)
                )
            }
        }
    }
}


@Composable
fun ProxyEnabledAppItem(
    appName: String,
    iconData: ByteArray?,
    blockCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 显示图标
        if (iconData != null) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(
                    iconData,
                    0,
                    iconData.size
                ).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        } else {
            // 提供默认图标或占位
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }

        // 显示应用名称
        Text(
            text = appName,
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        // 使用 Spacer 占据剩余空间使得被拦截次数右对齐
        Spacer(modifier = Modifier.weight(1f))

        // 被拦截次数
        Text(
            text = blockCount.toString(),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}