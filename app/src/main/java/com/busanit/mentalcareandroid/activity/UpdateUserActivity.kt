package com.busanit.mentalcareandroid.activity

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.set
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.databinding.ActivityUpdateUserBinding
import com.busanit.mentalcareandroid.model.McUser
import com.busanit.mentalcareandroid.model.McUserLogin
import com.busanit.mentalcareandroid.model.McUserLoginSuccess
import com.busanit.mentalcareandroid.model.McUserUpdate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors.toSet

class UpdateUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateUserBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences: SharedPreferences? = getSharedPreferences("app_pref", MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", "").orEmpty()

        RetrofitClient.api.getByUserId(userId).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {
                    binding.EditTextNickname.setText(response.body()?.userNickname)
                    when(response.body()?.userGender) {
                        'M' -> binding.male.setChecked(true)
                        'F' -> binding.female.setChecked(true)
                        else -> binding.none.setChecked(true)
                    }
                    binding.EditTextBirth.setText(response.body()?.userBirth)
                    binding.EditTextEmail.setText(response.body()?.userEmail)
                    binding.EditTextPhonenumber.setText(response.body()?.userPhonenumber)

                } else {
                    Toast.makeText(this@UpdateUserActivity, "회원 정보 로딩 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@UpdateUserActivity, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })

        // 회원 정보 수정 버튼 클릭 -> API 요청
        binding.buttonUpdateUser.setOnClickListener {
            val userPw = binding.EditTextPassword.text.toString()
            val user_check = McUserLogin(userId, userPw)

            // 회원 정보 변경시 비밀번호 일치 확인(로그인) API + (내부) 회원 정보 변경 API
            checkPasswordRequest(user_check, userPw)
        }
    }

    private fun checkPasswordRequest(user_check: McUserLogin, userPw: String) {
        RetrofitClient.api.authLogin(user_check).enqueue(object : Callback<McUserLoginSuccess> {
            override fun onResponse(
                call: Call<McUserLoginSuccess>,
                response: Response<McUserLoginSuccess>
            ) {
                if (response.isSuccessful) {
                    // 변경 정보
                    var userPwNew = binding.EditTextNewPassword.text.toString()
                    var userNickname = binding.EditTextNickname.text.toString()

                    var userGender = 'N'

                    if (binding.male.isChecked) {
                        userGender = 'M'
                    } else if (binding.female.isChecked) {
                        userGender = 'F'
                    }

                    var userBirth = binding.EditTextBirth.text.toString()
                    var userEmail = binding.EditTextEmail.text.toString()
                    var userPhonenumber = binding.EditTextPhonenumber.text.toString()

                    if (userPwNew.equals(null) || userPwNew.trim().equals("")) {
                        userPwNew = userPw
                    }
                    if (userNickname.equals(null) || userNickname.trim().equals("")) {
                        userNickname = response.body()?.userNickname!!
                    }

                    if (userGender.equals(null) || userGender.equals("")) {
                        userGender = response.body()?.userGender!!
                    }
                    if (userBirth.equals(null) || userBirth.trim().equals("")) {
                        userBirth = response.body()?.userBirth!!
                    }
                    if (userEmail.equals(null) || userEmail.trim().equals("")) {
                        userEmail = response.body()?.userEmail!!
                    }
                    if (userPhonenumber.equals(null) || userPhonenumber.trim().equals("")) {
                        userPhonenumber = response.body()?.userPhonenumber!!
                    }

                    val user = McUserUpdate(
                        userPw,
                        userPwNew,
                        userNickname,
                        userGender,
                        userBirth,
                        userEmail,
                        userPhonenumber
                    )

                    // 회원 정보 수정 메소드
                    val sharedPreferences: SharedPreferences? = getSharedPreferences("app_pref", MODE_PRIVATE)
                    val token = sharedPreferences?.getString("token", "").orEmpty()
                    val userId = sharedPreferences?.getString("userId", "").orEmpty()
                    updateUser(token, userId, user)

                } else {
                    Toast.makeText(this@UpdateUserActivity, "비밀번호 인증에 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<McUserLoginSuccess>, t: Throwable) {
                Toast.makeText(this@UpdateUserActivity, "회원정보수정 네트워크 요청 실패", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun updateUser(token: String, userId: String, user: McUserUpdate) {
        RetrofitClient.api.updateUser(token, userId, user).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateUserActivity, "회원정보수정에 성공하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    val sharedPreferences =
                        getSharedPreferences("app_pref", MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("userNickname", response.body()?.userNickname) // 사용자 닉네임 바꾸기
                        .apply()

                    startActivity(Intent(this@UpdateUserActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@UpdateUserActivity, "회원정보수정에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@UpdateUserActivity, "회원정보수정 네트워크 요청 실패", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}