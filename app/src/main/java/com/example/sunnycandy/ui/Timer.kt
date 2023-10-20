package com.example.sunnycandy.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
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
import androidx.work.ExistingWorkPolicy
import com.example.sunnycandy.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

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

        //定义历史元素的结构
        data class HistoryItem(val title:String, val content:String,val numInterval: Int, val interval: String,  val startTime: String)
        data class User(val name1:String,val name2:String)
        // 获取 SharedPreferences 中的 JSON 字符串
        val historySharedPred = this.getSharedPreferences("historyItems", Context.MODE_PRIVATE)
        val historyJson = historySharedPred.getString("historyData","[]")
        // 将 JSON 字符串转换为对象列表
        val gson = GsonBuilder().create()
        val historyItems: MutableList<HistoryItem> = gson.fromJson(historyJson, object : TypeToken<MutableList<HistoryItem>>(){}.type)
        Log.d("获取Items，为",historyJson.toString())
        Log.d("获取Items非Json格式为",historyItems.toString())

        //获取历史闹钟列表元素
        val history = binding.history

        // 获取LayoutInflater
        val inflater = LayoutInflater.from(this)
        //给按钮绑定点击事件
        button.setOnClickListener{
//            val day = binding.day.text.toString()
//            val hour = binding.hour.text.toString()
//            val minite = binding.minite.text.toString()
            //绑定通知内容
            val title = binding.title.text.toString()
            val content = binding.content.text.toString()
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
                .putString("content",content)
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
                WorkManager.getInstance(this).enqueueUniqueWork(title,
                    ExistingWorkPolicy.REPLACE,workRequest)

                val clockItem = inflater.inflate(R.layout.clock_item,null)
                clockItem.findViewById<TextView>(R.id.itemtitle).text = title
                clockItem.findViewById<TextView>(R.id.itemcontent).text = content
                history.addView(clockItem)

                //创建新的 HistoryItem对象
                val newItem = HistoryItem(title,content,10,"10秒","now")
                historyItems.add(newItem)
                //保存对象到SharedPreference
                val tempJson = gson.toJson(historyItems)
                val editor = historySharedPred.edit()
                editor.putString("historyData",tempJson).apply()
                Log.d("newItem的值是",newItem.toString())
                Log.d("nweItem的json是",gson.toJson(newItem))
                Log.d("historyItem的值是",historyItems.toString())
                Log.d("保存data,items的值为",tempJson.toString())
            } else {
                // 未授权，需要请求权限或执行适当的处理
                ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
            }
        }

        val cancelButton = binding.cancel
        cancelButton.setOnClickListener {
            WorkManager.getInstance(this).cancelAllWorkByTag("test")
        }
        val cancelButton2 = binding.cancel2
        cancelButton2.setOnClickListener {
            WorkManager.getInstance(this).cancelUniqueWork(binding.title.text.toString())
        }
        val testButton = binding.test
        val editor = historySharedPred.edit()
        editor.putString("historyData","[]").apply()
}
}