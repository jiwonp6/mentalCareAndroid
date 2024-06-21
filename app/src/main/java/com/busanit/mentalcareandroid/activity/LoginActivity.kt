package com.busanit.mentalcareandroid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.databinding.ActivityLoginBinding
import com.busanit.mentalcareandroid.model.McUserLogin
import com.busanit.mentalcareandroid.model.McUserLoginSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 로그인 버튼 클릭했을 때 이벤트
        binding.buttonLogin.setOnClickListener {
            // 사용자로부터 정보를 입력받아 데이터 객체 생성
            val userId = binding.EditTextUsername.text.toString()
            val userPw = binding.EditTextPassword.text.toString()
            val user = McUserLogin(userId, userPw)

            // 로그인 API 요청
            RetrofitClient.api.authLogin(user).enqueue(object : Callback<McUserLoginSuccess> {
                override fun onResponse(
                    call: Call<McUserLoginSuccess>,
                    response: Response<McUserLoginSuccess>
                ) {
                    // 성공한 경우 JWT 토큰을 확인
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "로그인 성공했습니다.", Toast.LENGTH_SHORT).show()

                        // 반환된 JWT 토큰을 sharedPreferences 에 저장
                        val token = response.body()?.jwt ?: ""  // 토큰

                        val sharedPreferences =
                            getSharedPreferences("app_pref", Context.MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putString("token", token)                                // 토큰
                            .putString("userId", userId)                              // 사용자 이름
                            .putString("userNickname", response.body()?.userNickname) // 사용자 닉네임
                            .apply()

                        // 로그인 성공하면 메인 화면 진입
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()    // 로그인 액티비티 종료

                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("mylog", "onResponse: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<McUserLoginSuccess>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "로그인 네트워크 요청 실패했습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("mylog", "onFailure: ${t.message}")
                    Log.d("mylog", "onFailure: ${t.printStackTrace()}")
                }
            })
        }

    }
}