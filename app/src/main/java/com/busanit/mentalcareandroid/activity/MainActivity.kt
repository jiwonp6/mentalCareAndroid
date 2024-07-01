package com.busanit.mentalcareandroid.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.adapter.ViewPagerAdapter
import com.busanit.mentalcareandroid.databinding.ActivityMainBinding
import com.busanit.mentalcareandroid.model.Test
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        val tabTitles = listOf("홈", "병원", "게시판")

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
                tab, position -> tab.text = tabTitles[position]
        }.attach()
    }

    // 보호된 자원 네트워크 요청 함수 : 403 번 (금지된 응답 Forbidden, 자원 확인 불가), 토큰과 함께 요청 => 200번 Ok
    private fun callProtect(token: String) {
        val api = RetrofitClient.api

        api.protect(token).enqueue(object : Callback<Test> {
            // 응답이 있는 경우
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                // 응답 코드가 200번대
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "응답성공", Toast.LENGTH_SHORT).show()
                } else {
                    // 응답하였지만, 400~500 번대인 경우
                    Toast.makeText(
                        this@MainActivity,
                        "응답실패, ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // 네트워크 요청 실패
            override fun onFailure(call: Call<Test>, t: Throwable) {
                Toast.makeText(this@MainActivity, "요청 실패, ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 네트워크 요청 테스트 함수 : 200 번 응답과 함께 자원 응답
    private fun callTest() {
        val api = RetrofitClient.api

        api.test().enqueue(object : Callback<Test> {
            // 응답이 있는 경우
            override fun onResponse(call: Call<Test>, response: Response<Test>) {
                // 응답 코드가 200번대
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "응답성공", Toast.LENGTH_SHORT).show()
                } else {
                    // 응답하였지만, 400~500 번대인 경우
                    Toast.makeText(
                        this@MainActivity,
                        "응답실패, ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // 네트워크 요청 실패
            override fun onFailure(call: Call<Test>, t: Throwable) {
                Toast.makeText(this@MainActivity, "요청 실패, ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}