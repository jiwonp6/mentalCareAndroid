package com.busanit.mentalcareandroid.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.toColorInt
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.databinding.ActivityMonthlyBinding
import com.busanit.mentalcareandroid.model.EmotionDiary
import com.busanit.mentalcareandroid.model.SleepData
import com.busanit.mentalcareandroid.model.StressData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MonthlyActivity : AppCompatActivity() {

    lateinit var binding: ActivityMonthlyBinding
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", null)

            // 오늘 날짜
            val today: LocalDate = LocalDate.now()
            val year = today.year
            val month = today.month
            TextViewMonth.text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
            TextViewYear.text = year.toString()

            // HomeBar 설정
            popUpMenu.setOnClickListener { view ->
                showPopup(view)
            }

            // 처음 구동시 데이터 오늘 날짜로 세팅
            userId?.let { setTodayStressData(it) }
            userId?.let { setTodaySleepData(it) }
            userId?.let { setTodayEmotionDiary(it) }


            // CalendarView 설정
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val formattedDate: String = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                Log.d("mylog", "onCreate: ${formattedDate}")

                /* 날짜가 선택되었을 때의 동작 정의 */
                // 데이터 & 감정일지
                userId?.let { setUserStressData(it, formattedDate) }
                userId?.let { setUserSleepData(it, formattedDate) }
                userId?.let { setUserEmotionDiary(it, formattedDate) }
                
            }
        }
    }

    /* 오늘 날짜 */
    // 데이터(스트레스, 수면) & 감정일지
    private fun setTodayStressData(userId: String) {
        RetrofitClient.api.getTodayStressData(userId).enqueue(object : Callback<StressData> {
            override fun onResponse(call: Call<StressData>, response: Response<StressData>) {
                if (response.isSuccessful) {
                    response.body()?.let { binding.stressData.setText(it.stdAvg.toString()) }
                } else {
                    binding.stressData.text = "측정된 데이터가 없습니다."
                }
            }
            override fun onFailure(call: Call<StressData>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setTodaySleepData(userId: String) {
        RetrofitClient.api.getTodaySleepData(userId).enqueue(object : Callback<SleepData> {
            override fun onResponse(call: Call<SleepData>, response: Response<SleepData>) {
                if (response.isSuccessful) {
                    response.body()?.let { binding.sleepData.setText(it.sldAvg.toString()) }
                } else {
                    binding.sleepData.text = "측정된 데이터가 없습니다."
                }
            }
            override fun onFailure(call: Call<SleepData>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setTodayEmotionDiary(userId: String) {
        RetrofitClient.api.getTodayEmotionDiary(userId).enqueue(object : Callback<EmotionDiary> {
            override fun onResponse(call: Call<EmotionDiary>, response: Response<EmotionDiary>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.emotionDiaryTag.setText(it.emotionType + "\t" + it.emotionWord)
                        binding.emotionDiaryContent.setText(it.edReason)

                        // 감정일지 수정
                        binding.emotionDiary.setOnClickListener{
                            startActivity(Intent(this@MonthlyActivity, UpdateEmotionDiaryActivity::class.java))
                        }
                    }
                } else {
                    binding.emotionDiaryTag.text = ""
                    binding.emotionDiaryContent.setTextColor("#DADADA".toColorInt())
                    binding.emotionDiaryContent.text = "아직 추가된 오늘의 감정일지가 없습니다."

                    // 감정일지 작성
                    binding.emotionDiary.setOnClickListener{
                        startActivity(Intent(this@MonthlyActivity, WriteEmotionDiaryActivity::class.java))
                    }
                }
            }
            override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /* 선택 날짜 */
    // 데이터(스트레스, 수면) & 감정일지
    private fun setUserStressData(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateStressData(userId, date).enqueue(object : Callback<StressData> {
            override fun onResponse(call: Call<StressData>, response: Response<StressData>) {
                    Log.d("mylog", "1. onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let { binding.stressData.setText(it.stdAvg.toString()) }
                } else {
                    binding.stressData.text = "측정된 데이터가 없습니다."
                }
            }
            override fun onFailure(call: Call<StressData>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUserSleepData(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateSleepData(userId, date).enqueue(object : Callback<SleepData> {
            override fun onResponse(call: Call<SleepData>, response: Response<SleepData>) {
                if (response.isSuccessful) {
                    response.body()?.let { binding.sleepData.setText(it.sldAvg.toString()) }
                } else {
                    binding.sleepData.text = "측정된 데이터가 없습니다."
                }
            }
            override fun onFailure(call: Call<SleepData>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUserEmotionDiary(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateEmotionDiary(userId, date).enqueue(object : Callback<EmotionDiary> {
            override fun onResponse(call: Call<EmotionDiary>, response: Response<EmotionDiary>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.emotionDiaryTag.setText(it.emotionType + "\t" + it.emotionWord)
                        binding.emotionDiaryContent.setText(it.edReason)

                        // 감정일지 수정
                        binding.emotionDiary.setOnClickListener{
                            startActivity(Intent(this@MonthlyActivity, UpdateEmotionDiaryActivity::class.java))
                        }
                    }
                } else {
                    binding.emotionDiaryTag.text = ""
                    binding.emotionDiaryContent.setTextColor("#DADADA".toColorInt())
                    binding.emotionDiaryContent.text = "아직 추가된 오늘의 감정일지가 없습니다."

                    // 감정일지 작성
                    binding.emotionDiary.setOnClickListener{
                        startActivity(Intent(this@MonthlyActivity, WriteEmotionDiaryActivity::class.java))
                    }
                }
            }
            override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /* 메뉴 */
    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.home_fragment_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleMenuItemClick(menuItem)
        }
        popup.show()
    }
    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.buttonLogout -> {
                // Handle logout action
                sharedPreferences.edit()
                    .remove("token")
                    .remove("userId")
                    .remove("userNickname")
                    .apply()

                // Show toast
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                // Navigate to TitleActivity
                startActivity(Intent(this, TitleActivity::class.java))
                finish()
                true
            }

            R.id.buttonUpdateUser -> {
                // Handle register action
                startActivity(Intent(this@MonthlyActivity, UpdateUserActivity::class.java))
                true
            }

            R.id.buttonWithdrawUser -> {
                // Handle withdraw user action
                startActivity(Intent(this@MonthlyActivity, WithdrawUserActivity::class.java))
                true
            }

            else -> false
        }
    }
}