package com.example.sunnycandy.utils
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class notificationUtils (private val context: Context){
    // 创建通知渠道
    fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            val notificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        contentText: String,
        smallIcon: Int
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(smallIcon)

        // 发送通知
        val notificationManager = NotificationManagerCompat.from(context)
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val granted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        if (granted) {
            // 已授权，执行需要权限的操作
            notificationManager.notify(notificationId, builder.build())
        }
        else{
        }
            Log.d("Timer","未获得授权")
        }
}