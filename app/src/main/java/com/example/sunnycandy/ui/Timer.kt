package com.example.sunnycandy.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.example.sunnycandy.utils.dataGson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.Date

class Timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    //定义历史元素的结构
    data class HistoryItem(val title:String, val content:String, val interval: Int, var startTime: Date)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        // 获取LayoutInflater
        val inflater = LayoutInflater.from(this)
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

        // 获取 SharedPreferences 中的 JSON 字符串
        val historySharedPred = this.getSharedPreferences("historyItems", Context.MODE_PRIVATE)
        val editor = historySharedPred.edit()
        val historyJson = historySharedPred.getString("historyData","[]")
        var notificationnum = historySharedPred.getInt("notificationnum",0)
        Log.d("从本地获取historyItems",historyJson.toString())
        Log.d("从本地获取notigicationnum",notificationnum.toString())
        // 将 JSON 字符串转换为对象列表
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java,dataGson)
            .create()
        var historyItems: MutableList<HistoryItem> = gson.fromJson(historyJson, object : TypeToken<MutableList<HistoryItem>>(){}.type)
        //获取历史闹钟列表元素
        val history = binding.history

        //将对象列表转化为视图
        for (item in historyItems){
            item.startTime = getStartTime(item.startTime,item.interval)
            println("检测data值")
            println(item.startTime.toString())
            Log.d("检测date数据类型",(item.startTime is Date).toString())
            Log.d("检测date值",item.startTime.toString())
            val clockItem = inflater.inflate(R.layout.clock_item,null)
            clockItem.findViewById<TextView>(R.id.itemtitle).text = item.title
            clockItem.findViewById<TextView>(R.id.itemcontent).text = item.content
            clockItem.findViewById<TextView>(R.id.itemInterval).text = getInterval(item.startTime)
            clockItem.findViewById<ImageView>(R.id.cancelClock).setOnClickListener{
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确认删除"+item.title + "吗？")
                    .setPositiveButton("确定"){dialog,which->
                        history.removeView(clockItem)
                        WorkManager.getInstance(this).cancelUniqueWork(item.title)
                        historyItems.remove(item)
                        val tempJson = gson.toJson(historyItems)
                        editor.putString("historyData",tempJson).apply()
                        Log.d("警告框","删除了"+item.title)
                        dialog.dismiss()}
                    .setNegativeButton("取消"){dialog,which->
                        Log.d("警告框","点击了取消")
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                alertDialog.show()
            }
            history.addView(clockItem)
        }
        val tempJson = gson.toJson(historyItems)
        editor.putString("historyData",tempJson).apply()

        //给按钮绑定点击事件
        button.setOnClickListener{
            val day = binding.day.text.toString()
            val hour = binding.hour.text.toString()
            val minite = binding.minite.text.toString()
            //循环间隔-分钟
            val interval = getNumInterval(day,hour,minite)
            //绑定通知内容
            val title = binding.title.text.toString()
            val content = binding.content.text.toString()
            val tag = "test"
            //每一个通知的id
            val permission = Manifest.permission.POST_NOTIFICATIONS
            //延迟时间-分钟
            val delayTime = 5
            //检测是否授权
            val granted = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
            if (granted &&interval!=0&&title!=""&&content!="") {
                notificationnum++
                val inputData = Data.Builder()
                    .putString("title",title)
                    .putString("content",content)
                    .putString("tag",tag)
                    .putInt("notificationId",notificationnum)
                    .putInt("Interval",interval)
                    .build()
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

                //创建新的 HistoryItem对象
                val newItem = HistoryItem(title,content,interval,Date())
                historyItems.add(newItem)
                //保存对象到SharedPreference
                val tempJson = gson.toJson(historyItems)
                Log.d("newItem",newItem.toString())
                Log.d("保存的tempJson",tempJson)
                editor.putString("historyData",tempJson).apply()
                editor.putInt("notificationnum",notificationnum).apply()
                //创建对应的视图
                val clockItem = inflater.inflate(R.layout.clock_item,null)
                clockItem.findViewById<TextView>(R.id.itemtitle).text = title
                clockItem.findViewById<TextView>(R.id.itemcontent).text = content
//                clockItem.findViewById<TextView>(R.id.itemInterval).text = interval
                clockItem.findViewById<ImageView>(R.id.cancelClock).setOnClickListener{
                    val alertDialog = AlertDialog.Builder(this)
                        .setTitle("确认删除")
                        .setMessage("确认删除"+title + "吗？")
                        .setPositiveButton("确定"){dialog,which->
                            history.removeView(clockItem)
                            WorkManager.getInstance(this).cancelUniqueWork(title)
                            historyItems.remove(newItem)
                            val tempJson = gson.toJson(historyItems)
                            editor.putString("historyData",tempJson).apply()
                            Log.d("警告框","删除了"+ title)
                            dialog.dismiss()}
                        .setNegativeButton("取消"){dialog,which->
                            Log.d("警告框","点击了取消")
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .create()
                    alertDialog.show()
                }
                history.addView(clockItem)
            } else {
                if(granted){
                    val alertDialog = AlertDialog.Builder(this)
                        .setTitle("错误捏")
                        .setMessage("有必要项没有输入。")
                        .setPositiveButton("ok") { dialog, which ->
                        }
                        .create()
                    alertDialog.show()

                }
                else{
                    // 未授权，需要请求权限或执行适当的处理
                    ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
                }
            }
        }

        val testbutton = binding.test
        testbutton.setOnClickListener{
            history.removeAllViews()
            WorkManager.getInstance(this).cancelAllWorkByTag("test")
            editor.putString("historyData","[]").apply()

        }
}

    fun getInterval(startTime: Date):String {
        val currentDate = Date()
        val diffInMillis = startTime.time - currentDate.time
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis).toInt()%24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis).toInt()%60
        var str = ""
        if(days != 0)
        {
           str += days.toString() + "天"
        }
        if(hours != 0)
        {
            str += hours.toString() + "时"
        }
        if(minutes != 0)
        {
            str += minutes.toString() + "分"
        }
        if (days==0||hours==0||minutes==0)
        {
            str = "通知倒计时:$str"
        }
        if(days==0&&hours==0&&minutes==0)
        {
            str = "还剩不到一分钟响铃"
        }
        return str
    }
    fun getNumInterval(day:String,hour:String,minite:String):Int {
        var interval = 0
        if(day!="")
        {
            interval+= day.toInt()*24*60
        }
        if(hour!="")
        {
            interval+= hour.toInt()*60
        }
        if(minite!="")
        {
            interval+= minite.toInt()
        }
        return interval
    }

    fun getStartTime(startdate:Date,interval:Int):Date{
        val now = Date()
        var result = startdate
        while (result.before(now))
        {
            val timeInmil = result.time + interval*60*1000
            result = Date(timeInmil)
        }
        return result
    }
}