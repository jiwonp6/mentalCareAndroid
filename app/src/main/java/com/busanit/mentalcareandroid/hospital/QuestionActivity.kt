package com.busanit.mentalcareandroid.hospital

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.QuestionBinding

class QuestionActivity : AppCompatActivity() {
    lateinit var binding: QuestionBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = QuestionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

    }
}