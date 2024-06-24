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

        // 회원 정보 세팅
        val sharedPreferences: SharedPreferences? = getSharedPreferences("app_pref", MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", "").orEmpty()
        setOriginUserInfo(userId)

        // 비밀번호 확인
        binding.buttonCheckedPw.setOnClickListener {
            val userPw = binding.EditTextPassword.text.toString()
            val user_check = McUserLogin(userId, userPw)
            checkUserPw(user_check)
        }

        // 회원 정보 수정
        binding.buttonUpdateUser.setOnClickListener {
            // 변경 정보
            val userPw = binding.EditTextPassword.text.toString()
            var userPwNew = binding.EditTextNewPassword.text.toString()
            var userNickname = binding.EditTextNickname.text.toString()
            var userGender = 'N'
            if (binding.male.isChecked) {
                userGender = 'M'
            } else if (binding.female.isChecked) {
                userGender = 'F'
            } else {
                userGender = 'N'
            }
            var userBirth = binding.EditTextBirth.text.toString()
            var userEmail = binding.EditTextEmail.text.toString()
            var userPhonenumber = binding.EditTextPhonenumber.text.toString()

            // 변경 정보 유효성 검사
            // 비밀번호
            // 영문 포함 + 숫자 포함 + 특수문자 + 길이 8~20자리 사이 문자열(반드시 모두 포함)
            val regexPw = Regex(
                pattern = "[a-z0-9#?!@\$%^&*-](?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$ %^&*-])[a-z0-9#?!@\$%^&*-]{8,20}",
                options = setOf(RegexOption.IGNORE_CASE)
            )
            if (!regexPw.matches(userPw)) {
                binding.EditTextPassword.requestFocus()
                Toast.makeText(this@UpdateUserActivity, "비밀번호 형식을 확인해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // 생년월일
            val regexBirth = Regex(
                pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
                options = setOf(RegexOption.IGNORE_CASE)
            )
            if (!regexBirth.matches(userBirth)) {
                binding.EditTextBirth.requestFocus()
                Toast.makeText(this@UpdateUserActivity, "생년월일 형식을 확인해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // 이메일
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                binding.EditTextEmail.requestFocus()
                Toast.makeText(this@UpdateUserActivity, "이메일 형식을 확인해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            // 전화번호
            val regexTel = Regex(
                pattern = "[0-9]{3}-[0-9]{3,4}-[0-9]{4}",
                options = setOf(RegexOption.IGNORE_CASE)
            )
            if (!regexTel.matches(userPhonenumber)) {
                binding.EditTextPhonenumber.requestFocus()
                Toast.makeText(this@UpdateUserActivity, "전화번호 형식을 확인해 주세요.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

//            // 변경하지 않는 경우 원래 정보로 세팅
//            if (userPwNew.equals(null) || userPwNew.trim().equals("")) {
//                userPwNew = userPw
//            }
//            if (userNickname.equals(null) || userNickname.trim().equals("")) {
//                userNickname = response.body()?.userNickname!!
//            }
//            if (userBirth.equals(null) || userBirth.trim().equals("")) {
//                userBirth = response.body()?.userBirth!!
//            }
//            if (userEmail.equals(null) || userEmail.trim().equals("")) {
//                userEmail = response.body()?.userEmail!!
//            }
//            if (userPhonenumber.equals(null) || userPhonenumber.trim().equals("")) {
//                userPhonenumber = response.body()?.userPhonenumber!!
//            }

            // 받은 정보로 객체 생성
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
            val sharedPreferences: SharedPreferences? =
                getSharedPreferences("app_pref", MODE_PRIVATE)
            val token = sharedPreferences?.getString("token", "").orEmpty()
            val userId = sharedPreferences?.getString("userId", "").orEmpty()
            updateUser(token, userId, user)

        }
    }

    private fun setOriginUserInfo(userId: String) {
        RetrofitClient.api.getByUserId(userId).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {
                    binding.EditTextNickname.setText(response.body()?.userNickname)
                    when (response.body()?.userGender) {
                        'M' -> binding.male.setChecked(true)
                        'F' -> binding.female.setChecked(true)
                        else -> binding.none.setChecked(true)
                    }
                    binding.EditTextBirth.setText(response.body()?.userBirth)
                    binding.EditTextEmail.setText(response.body()?.userEmail)
                    binding.EditTextPhonenumber.setText(response.body()?.userPhonenumber)

                } else {
                    Toast.makeText(this@UpdateUserActivity, "회원 정보 로딩 실패", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@UpdateUserActivity, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun checkUserPw(user_check: McUserLogin) {
        RetrofitClient.api.authLogin(user_check).enqueue(object : Callback<McUserLoginSuccess> {
            override fun onResponse(
                call: Call<McUserLoginSuccess>,
                response: Response<McUserLoginSuccess>
            ) {
                if (response.isSuccessful) {
                    binding.buttonCheckedPw.isEnabled = false
                } else {
                    Toast.makeText(this@UpdateUserActivity, "비밀번호 확인을 진행해 주세요.", Toast.LENGTH_SHORT)
                        .show()
                    binding.buttonCheckedPw.isEnabled = true
                }
            }

            override fun onFailure(call: Call<McUserLoginSuccess>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@UpdateUserActivity, "비밀번호 확인 네트워크 요청 실패", Toast.LENGTH_SHORT)
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