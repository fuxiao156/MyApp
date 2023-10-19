package com.example.sunnycandy.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sunnycandy.R
import com.example.sunnycandy.utils.notificationUtils

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val inputData = inputData
        val title = inputData.getString("title")
        val contentText = inputData.getString("content")
        // 在这里执行发送通知的逻辑，比如使用 NotificationManager 发送通知
        val notification = notificationUtils(applicationContext)
        // 创建通知渠道
        notification.createNotificationChannel("timer", "timer")

        if (contentText != null&& title!=null) {
            notification.sendNotification("timer", 2,  title, contentText, R.drawable.clock)
        }
        // 返回 Result.success() 表示任务执行成功
        return Result.success()
    }
}
