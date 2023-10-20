package com.example.sunnycandy

import android.app.Application
import androidx.work.WorkManager

class MyApplication  : Application(){
    override fun onCreate() {
        super.onCreate()
        initializeWorkManager()
    }

    private fun initializeWorkManager() {
        // 创建 WorkManager 实例
        val workManager = WorkManager.getInstance(this)
    }
}