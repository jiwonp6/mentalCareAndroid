package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.databinding.ActivityRegisterBinding
import com.busanit.mentalcareandroid.model.McUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 -> API 요청
        binding.buttonRegister.setOnClickListener {
            // 입력된 정보를 가져와 요청할 객체 생성
            val userId = binding.EditTextUsername.text.toString()
            val userPw = binding.EditTextPassword.text.toString()
            val userNickname = binding.EditTextNickname.text.toString()

            var userGender = 'N'
            if (binding.CheckGenderM.isChecked) {
                userGender = 'M'
            } else if (binding.CheckGenderF.isChecked){
                userGender = 'F'
            } else if (binding.CheckGenderF.isChecked){
                userGender = 'N'
            } else {
                Toast.makeText(this@RegisterActivity, "성별 항목을 반드시 선택해야 합니다.", Toast.LENGTH_SHORT).show()
                binding.CheckGenderN.isFocusable
            }

            val userBirth = binding.EditTextBirth.text.toString()
            val userEmail = binding.EditTextEmail.text.toString()
            val userPhonenumber = binding.EditTextPhonenumber.text.toString()

            val user = McUser(userId, userPw, userNickname, userGender, userBirth, userEmail, userPhonenumber)

            // 레트로핏으로 API 비동기 요청
            RetrofitClient.api.createUser(user).enqueue(object : Callback<McUser> {
                override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(this@RegisterActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<McUser>, t: Throwable) {
                    Log.d("mylog", "onResponse: ${user}")
                    Log.d("mylog", "onFailure: ${t.message}")
                    Log.d("mylog", "onFailure: ${t.cause}")
                    // 네트워크 오류
                    Toast.makeText(this@RegisterActivity, "회원가입 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}