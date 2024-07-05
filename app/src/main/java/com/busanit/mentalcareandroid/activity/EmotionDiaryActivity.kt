package com.busanit.mentalcareandroid.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.databinding.ActivityEmotionDiaryBinding

class EmotionDiaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmotionDiaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}