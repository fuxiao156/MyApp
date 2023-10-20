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

class Timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 在Activity或Fragment中请求后台权限
        val permission1 = Manifest.permission.FOREGROUND_SERVICE
        val requestCode = 123 // 替换为您自己的请求码

        if (ContextCompat.checkSelfPermission(this, permission1) != PackageManager.PERMISSION_GRANTED) {
            // 如果应用没有权限，请求权限
            ActivityCompat.requestPermissions(this, arrayOf(permission1), requestCode)
        }


        //获取notificationUtils类对象
        val notification = notificationUtils(this)

        // 创建通知渠道
        notification.createNotificationChannel("timer", "timer")
        val button = binding.assure

        //给按钮绑定点击事件
        button.setOnClickListener{
            val day = binding.day.text.toString()
            val hour = binding.hour.text.toString()
            val minite = binding.minite.text.toString()
            //绑定通知内容
            val title = "通知标题"
            val contentText = "通知内容"
            val tag = "test"
            //每一个通知的id
            val notificationId = 1
            val permission = Manifest.permission.POST_NOTIFICATIONS
            //延迟时间-分钟
            val delayTime = 5
            //循环间隔-分钟
            val interval = 10
            val inputData = Data.Builder()
                .putString("title",title)
                .putString("content",contentText)
                .putString("tag",tag)
                .putInt("notificationId",notificationId)
                .putInt("delayTime",delayTime)
                .putInt("Interval",interval)
                .build()
            //检测是否授权
            val granted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
            if (granted) {
                //  已授权，创建 OneTimeWorkequest 来调度 ReminderWorker
                val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(
                        delayTime.toLong(),
                        TimeUnit.SECONDS
                    ).setInputData(inputData).addTag(tag)
                    .build()
                // 将任务请求加入 WorkManager 队列
                WorkManager.getInstance(this).enqueue(workRequest)
            } else {
                // 未授权，需要请求权限或执行适当的处理
                ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
            }
        }

        val cancelButton = binding.cancel
        cancelButton.setOnClickListener {
            WorkManager.getInstance(this).cancelAllWorkByTag("test")
        }
    }
}