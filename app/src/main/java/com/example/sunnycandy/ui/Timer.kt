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
        val button = binding.assure
        button.setOnClickListener{
            val week = binding.week.text.toString().toInt()
            val day = binding.day.text.toString().toInt()
            val hour = binding.hour.text.toString().toInt()
            val minite = binding.minite.text.toString().toInt()
            val second = binding.second.text.toString().toInt()
        }
    }
}