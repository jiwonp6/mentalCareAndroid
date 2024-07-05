package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.databinding.ActivityWithdrawUserBinding
import com.busanit.mentalcareandroid.model.McUser
import com.busanit.mentalcareandroid.model.McUserLogin
import com.busanit.mentalcareandroid.model.McUserLoginSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityWithdrawUserBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityWithdrawUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences: SharedPreferences? = getSharedPreferences("app_pref", MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", "").orEmpty()

        binding.buttonWithdrawUser.setOnClickListener {
            val userPw = binding.EditTextPassword.text.toString()
            val user_check = McUserLogin(userId, userPw)

            // 회원 탈퇴시 비밀번호 일치 확인(로그인) API
            checkPasswordRequest(user_check, userPw)
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPasswordRequest(user_check: McUserLogin, userPw: String) {
        RetrofitClient.api.authLogin(user_check).enqueue(object : Callback<McUserLoginSuccess> {
            override fun onResponse(
                call: Call<McUserLoginSuccess>,
                response: Response<McUserLoginSuccess>
            ) {
                val sharedPreferences: SharedPreferences? = getSharedPreferences("app_pref", MODE_PRIVATE)
                val userId = sharedPreferences?.getString("userId", "").orEmpty()

                if (response.isSuccessful) {
                    withdrawUser(userId)
                } else {
                    Toast.makeText(this@WithdrawUserActivity, "비밀번호 인증에 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<McUserLoginSuccess>, t: Throwable) {
                Toast.makeText(this@WithdrawUserActivity, "회원 탈퇴 네트워크 요청 실패", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun withdrawUser(userId: String) {
        RetrofitClient.api.withdrawUser(userId).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {

                    Toast.makeText(this@WithdrawUserActivity, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    
                    // 토큰 제거
                    val sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
                    sharedPreferences?.edit()
                        ?.remove("access_token")
                        ?.remove("useId")
                        ?.remove("userNickname")
                        ?.apply()

                    // 2. TitleActivity 로 돌려 보내고 현재 화면 종료
                    startActivity(Intent(this@WithdrawUserActivity, TitleActivity::class.java))
                    this@WithdrawUserActivity.finish()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                Toast.makeText(this@WithdrawUserActivity, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}