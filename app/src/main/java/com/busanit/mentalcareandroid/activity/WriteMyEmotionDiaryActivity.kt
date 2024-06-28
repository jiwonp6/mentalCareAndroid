package com.busanit.mentalcareandroid.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.databinding.ActivityWriteMyEmotionDiaryBinding

class WriteMyEmotionDiaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteMyEmotionDiaryBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteMyEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}