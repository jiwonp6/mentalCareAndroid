package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.adapter.ViewPagerAdapter
import com.busanit.mentalcareandroid.databinding.ActivityMainBinding
import com.busanit.mentalcareandroid.fragment.HomeFragment
import com.busanit.mentalcareandroid.hospital.HospitalActivity
import com.busanit.mentalcareandroid.model.Test
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()    // 뷰페이저 설정
    }


    // 뷰 페이저 설정 함수
    private fun setupViewPager() {
        binding.viewPager.adapter = ViewPagerAdapter(this)
        // home
        binding.home.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
        }
        binding.hospital.setOnClickListener{
            startActivity(Intent(this@MainActivity, HospitalActivity::class.java))
        }

        //board
        binding.board.setOnClickListener {
            startActivity(Intent(this@MainActivity, CommunityMainActivity::class.java))
        }

    }

    // 보호된 자원 네트워크 요청 함수 : 403 번 (금지된 응답 Forbidden, 자원 확인 불가), 토큰과 함께 요청 => 200번 Ok
    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
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