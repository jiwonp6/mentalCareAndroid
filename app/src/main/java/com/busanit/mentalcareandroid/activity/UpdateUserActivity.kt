package com.busanit.mentalcareandroid.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.databinding.ActivityUpdateUserBinding
import com.busanit.mentalcareandroid.model.McUser
import com.busanit.mentalcareandroid.model.McUserLogin
import com.busanit.mentalcareandroid.model.McUserLoginSuccess
import com.busanit.mentalcareandroid.model.McUserUpdate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateUserBinding


//    override fun startActivity(intent: Intent?, options: Bundle?) {
//        super.startActivity(intent, options)
//        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        
//        RetrofitClient.api.getByUserId(token, userId).enqueue(object : Callback<McUser> {
//            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
//                if (response.isSuccessful) {
//                    println("!!!!" + response.body())
//                } else {
//                    Toast.makeText(this@UpdateUserActivity, "회원 정보 로딩 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<McUser>, t: Throwable) {
//                // 네트워크 오류
//                Toast.makeText(this@UpdateUserActivity, "회원 정보 수정 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "").orEmpty()
        val userId = sharedPreferences?.getString("userId", "").orEmpty()

        Log.d("mylog", "userId->onCreate: ${sharedPreferences.getString("userId", userId)}")

        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원정보수정 버튼 클릭 -> API 요청
        binding.buttonUpdateUser.setOnClickListener {
            val userPw = binding.EditTextPassword.text.toString()
            val user_check: McUserLogin = McUserLogin(userId, userPw)

            // 회원 정보 변경시 비밀번호 일치 확인(로그인) API 요청
            val user =  checkPasswordRequest(user_check, userPw)


            Log.d("mylog", "일단 비번인증은 성공 -> onCreate: ${user}")

            // 회원 정보 변경 API 요청
            if (user != null) {
                updateUser(token, userId, user)
            }

        }
    }

    private fun updateUser(token: String, userId: String, user: McUserUpdate) {
        RetrofitClient.api.updateUser(token, userId, user).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                Log.d("mylog", "회원정보변경까지는 들어옴->onResponse: ${response.body()}")
                Log.d("mylog", "회원정보변경까지는 들어옴->onResponse: ${response.code()}")
                Log.d("mylog", "회원정보변경까지는 들어옴->onResponse: ${response.message()}")
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateUserActivity, "회원정보수정에 성공하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    val sharedPreferences =
                        getSharedPreferences("app_pref", MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("userNickname", response.body()?.userNickname) // 사용자 닉네임 바꾸기
                        .apply()

                    Log.d("mylog", "회원 정보 변경->onResponse: ${sharedPreferences}")

                    startActivity(Intent(this@UpdateUserActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@UpdateUserActivity, "회원정보수정에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                Log.d("mylog", "onFailure: ${t.message}")
                // 네트워크 오류
                Toast.makeText(this@UpdateUserActivity, "회원정보수정 네트워크 요청 실패", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun checkPasswordRequest(user_check: McUserLogin, userPw: String): McUserUpdate? {
        var updateUser: McUserUpdate? = null
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
                    if (binding.CheckGenderM.isChecked) {
                        userGender = 'M'
                    } else if (binding.CheckGenderF.isChecked) {
                        userGender = 'F'
                    } else if (binding.CheckGenderF.isChecked) {
                        userGender = 'N'
                    }
                    var userBirth = binding.EditTextBirth.text.toString()
                    var userEmail = binding.EditTextEmail.text.toString()
                    var userPhonenumber = binding.EditTextPhonenumber.text.toString()

                    if (userPwNew.equals(null) || userPwNew.equals("")) {
                        userPwNew = userPw
                    }
                    if (userNickname.equals(null) || userNickname.equals("")) {
                        userNickname = response.body()?.userNickname!!
                    }

                    if (userGender.equals(null) || userGender.equals("")) {
                        userGender = response.body()?.userGender!!
                    }
                    if (userBirth.equals(null) || userBirth.equals("")) {
                        userBirth = response.body()?.userBirth!!
                    }
                    if (userEmail.equals(null) || userEmail.equals("")) {
                        userEmail = response.body()?.userEmail!!
                    }
                    if (userPhonenumber.equals(null) || userPhonenumber.equals("")) {
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
                    Log.d("mylog", "changeUser->onResponse: ${user}")

                    updateUser = user

                    Toast.makeText(this@UpdateUserActivity, "비밀번호 인증에 성공했습니다.", Toast.LENGTH_SHORT)
                        .show()

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
    return updateUser
    }
}