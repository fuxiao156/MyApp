package com.example.sunnycandy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sunnycandy.R
import com.example.sunnycandy.databinding.ActivityTimerBinding
import com.example.sunnycandy.service.ReminderWorker
import com.example.sunnycandy.utils.notificationUtils
import java.util.concurrent.TimeUnit
import androidx.work.Data

class Timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val button = binding.assure
        button.setOnClickListener{
            val day = binding.day.text.toString().toInt()
            val hour = binding.hour.text.toString().toInt()
            val minite = binding.minite.text.toString().toInt()
            val second = binding.second.text.toString().toInt()
        }
        val notification = notificationUtils(this)
        // 创建通知渠道
        notification.createNotificationChannel("timer", "timer")
        // 发送通知
        val title = "通知标题"
        val contentText = "通知内容"
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val granted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
        if (granted) {
            // 已授权，执行需要权限的操作
            val inputData = Data.Builder()
                .putString("title",title)
                .putString("content",contentText)
                .build()
//            notification.sendNotification("timer", 2,  title, contentText, R.drawable.clock)
            // 创建 OneTimeWorkequest 来调度 ReminderWorker
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(5, TimeUnit.SECONDS) // 设置初始延迟，例如5秒
                .setInputData(inputData)
                .build()

            // 将任务请求加入 WorkManager 队列
            WorkManager.getInstance().enqueue(workRequest)
        } else {
            // 未授权，需要请求权限或执行适当的处理
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        }

    }
}