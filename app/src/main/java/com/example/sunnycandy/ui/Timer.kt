package com.example.sunnycandy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sunnycandy.databinding.ActivityTimerBinding

class Timer : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}