package com.example.sunnycandy.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sunnycandy.R
import com.example.sunnycandy.utils.notificationUtils
import androidx.work.WorkManager

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // 返回 Result.success() 表示任务执行成功
        sendNotification()
        Log.d("是否执行循环","执行了一次循环")
        return Result.success()
    }
    private fun sendNotification(){
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
        else{
            Log.d("循环相关","循环停止")
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelUniqueWork("test")
        }
    }
}

