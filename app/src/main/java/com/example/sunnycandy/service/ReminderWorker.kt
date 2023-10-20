package com.example.sunnycandy.service

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sunnycandy.R
import com.example.sunnycandy.utils.notificationUtils
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // 返回 Result.success() 表示任务执行成功
        sendNotification()
        val title = inputData.getString("title").toString()
        val interval = inputData.getInt("Interval",10)
        val tag = inputData.getString("tag")
        Log.d("是否执行循环","执行了一次循环")
        val daliyWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(
                interval.toLong(),TimeUnit.SECONDS
            ).setInputData(inputData)
            .addTag(tag.toString())
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(title, ExistingWorkPolicy.REPLACE,daliyWorkRequest)
        return Result.success()
    }
    private fun sendNotification(){
        //从inputData获取数据
        val inputData = inputData
        val title = inputData.getString("title")
        val contentText = inputData.getString("content")
        val notifacationId = inputData.getInt("notificationId",10)
        // 在这里执行发送通知的逻辑，比如使用 NotificationManager 发送通知
        val notification = notificationUtils(applicationContext)
        // 创建通知渠道
        notification.createNotificationChannel("timer", "timer")

        if (contentText != null&& title!=null) {
            notification.sendNotification("timer", notifacationId,  title, contentText, R.drawable.clock)
        }
        else{
            Log.d("循环相关","循环停止")
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelUniqueWork("test")
        }
    }
}

