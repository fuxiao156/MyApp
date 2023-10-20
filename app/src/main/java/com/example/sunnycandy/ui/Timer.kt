package com.example.sunnycandy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sunnycandy.databinding.ActivityTimerBinding
import com.example.sunnycandy.service.ReminderWorker
import com.example.sunnycandy.utils.notificationUtils
import java.util.concurrent.TimeUnit
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder

class Timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val button = binding.assure

        //给按钮绑定点击事件
        button.setOnClickListener{
            val day = binding.day.text.toString().toInt()
            val hour = binding.hour.text.toString().toInt()
            val minite = binding.minite.text.toString().toInt()
            val second = binding.second.text.toString().toInt()
        }

        //获取notificationUtils类对象
        val notification = notificationUtils(this)

        // 创建通知渠道
        notification.createNotificationChannel("timer", "timer")

        //绑定通知内容
        val title = "通知标题"
        val contentText = "通知内容"
        val permission = Manifest.permission.POST_NOTIFICATIONS
        //检测是否授权
        val granted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
        if (granted) {
            // 已授权，执行需要权限的操作
            val inputData = Data.Builder()
                .putString("title",title)
                .putString("content",contentText)
                .build()
            // 创建 OneTimeWorkequest 来调度 ReminderWorker
            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                5,
                TimeUnit.SECONDS
            ).setInitialDelay(
                2,
                TimeUnit.SECONDS
            ).setInputData(inputData).addTag("test")
                .build()

            // 将任务请求加入 WorkManager 队列
            WorkManager.getInstance(this).enqueue(workRequest)
        } else {
            // 未授权，需要请求权限或执行适当的处理
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        }

    }
}