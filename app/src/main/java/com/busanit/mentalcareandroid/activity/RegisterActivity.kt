package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityRegisterBinding
import com.busanit.mentalcareandroid.model.McUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 아이디 확인
        binding.buttonCheckedIdExist.setOnClickListener {
            val userId = binding.EditTextUserId.text.toString()
            checkUserIdExist(userId)
        }

        // 성별 설정
        var userGender = 'N'
        binding.UserGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.male -> {
                    userGender = 'M'
                }
                R.id.female -> {
                    userGender = 'F'
                }
                R.id.none -> {
                    userGender = 'N'
                }
            }
        }

        // 회원가입 버튼 클릭 -> API 요청
        binding.buttonRegister.setOnClickListener {
            // 입력된 정보를 가져와 요청할 객체 생성
            val userId = binding.EditTextUserId.text.toString()
            val userPw = binding.EditTextPassword.text.toString()
            val userNickname = binding.EditTextNickname.text.toString()
            // 성별은 따로 setOnCheckedChangeListener 로 받음
            val userBirth = binding.EditTextBirth.text.toString()
            var userEmail = binding.EditTextEmail.text.toString()
            val userPhonenumber = binding.EditTextPhonenumber.text.toString()

            // 유효성 검사
            // 아이디
            checkUserIdExist(userId)
            if (binding.buttonCheckedIdExist.isEnabled) {
                binding.EditTextUserId.requestFocus()
                Toast.makeText(this@RegisterActivity, "아이디 확인을 진행해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 비밀번호
            // 영문 포함 + 숫자 포함 + 특수문자 + 길이 8~20자리 사이 문자열(반드시 모두 포함)
            val regexPw = Regex(pattern = "[a-z0-9#?!@\$%^&*-](?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$ %^&*-])[a-z0-9#?!@\$%^&*-]{8,20}", options = setOf(RegexOption.IGNORE_CASE))
            if (!regexPw.matches(userPw)) {
                binding.EditTextPassword.requestFocus()
                Toast.makeText(this@RegisterActivity, "비밀번호 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 생년월일
            val regexBirth = Regex(pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}", options = setOf(RegexOption.IGNORE_CASE))
            if (!regexBirth.matches(userBirth)) {
                binding.EditTextBirth.requestFocus()
                Toast.makeText(this@RegisterActivity, "생년월일 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 이메일
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                binding.EditTextEmail.requestFocus()
                Toast.makeText(this@RegisterActivity, "이메일 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 전화번호
            val regexTel = Regex(pattern = "[0-9]{3}-[0-9]{3,4}-[0-9]{4}", options = setOf(RegexOption.IGNORE_CASE))
            if (!regexTel.matches(userPhonenumber)) {
                binding.EditTextPhonenumber.requestFocus()
                Toast.makeText(this@RegisterActivity, "전화번호 형식을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 받은 정보로 객체 생성
            val user = McUser(userId, userPw, userNickname, userGender, userBirth, userEmail, userPhonenumber)

            // 레트로핏으로 API 비동기 요청
            createUser(user)
        }
    }

    private fun createUser(user: McUser) {
        RetrofitClient.api.createUser(user).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(this@RegisterActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<McUser>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@RegisterActivity, "회원가입 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkUserIdExist(userId: String) {
        RetrofitClient.api.getByUserId(userId).enqueue(object : Callback<McUser> {
            override fun onResponse(call: Call<McUser>, response: Response<McUser>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "사용 불가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                    binding.buttonCheckedIdExist.isEnabled = true
                } else {
                    binding.buttonCheckedIdExist.isEnabled = false
                }
            }
            override fun onFailure(call: Call<McUser>, t: Throwable) {
                // 네트워크 오류
                Toast.makeText(this@RegisterActivity, "아이디 확인 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

}