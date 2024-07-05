package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.databinding.ActivityTitleBinding

class TitleActivity : AppCompatActivity() {
    lateinit var binding: ActivityTitleBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = getToken()

        // 토큰이 없는 경우 타이틀 화면 진행
        if (token.isNullOrEmpty()) {
            // 로그인 버튼을 클릭하면 LoginActivity 로 이동
            binding.buttonLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            // 회원가입 버튼을 클릭하면 RegisterActivity 로 이동
            binding.buttonRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } else {
            // 토큰이 존재하면 MainActivity로 이동 (로그인 정보가 저장되어 있는 경우)
            startActivity(Intent(this, MainActivity::class.java))
            finish()    // TitleActivity 종료
        }
    }

    // sharedPreferences 에서 토큰 가져오는 함수
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null) ?: ""
    }
}