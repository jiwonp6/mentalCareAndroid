package com.busanit.mentalcareandroid.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.adapter.ViewPagerAdapter
import com.busanit.mentalcareandroid.databinding.ActivityMainBinding
import com.busanit.mentalcareandroid.databinding.ActivityMyEmotionDiaryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MyEmotionDiaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyEmotionDiaryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 저장된 토큰을 보호된 리소스 요청 시 사용
//         val token = sharedPreferences.getString("token", "") ?: ""

        // 인증 요청시 HTTP 헤더에 "Bearer {jwt_token}" 요청
//         callProtect("Bearer $token")

        setupViewPager()    // 뷰페이저 설정
    }

    // 뷰 페이저 설정 함수
    private fun setupViewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(this)

        val tabTitles = listOf("홈", "게시판", "설정")

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()
    }
}