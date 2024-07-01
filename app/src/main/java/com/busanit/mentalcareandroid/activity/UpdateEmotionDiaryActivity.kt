package com.busanit.mentalcareandroid.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityMonthlyBinding
import com.busanit.mentalcareandroid.databinding.ActivityUpdateEmotionDiaryBinding

class UpdateEmotionDiaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateEmotionDiaryBinding
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}