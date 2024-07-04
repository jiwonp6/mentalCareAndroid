package com.busanit.mentalcareandroid.activity

import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.toColorInt
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityMonthlyBinding
import com.busanit.mentalcareandroid.model.EmotionDiary
import com.busanit.mentalcareandroid.model.SleepData
import com.busanit.mentalcareandroid.model.StressData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class MonthlyActivity : AppCompatActivity() {

    lateinit var binding: ActivityMonthlyBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var launcher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityMonthlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        // Result API 등록
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // 성공시 emotionDiary 업데이트
                    val updatedDate = result.data?.getStringExtra("date")
                    updatedDate?.let {setUserEmotionDiary(userId!!, it)}
                }
            }

        // 처음 구동시 데이터 오늘 날짜로 세팅
        var date = intent.getStringExtra("date")
        if (date == null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            date = dateFormat.format(currentDate)
        }
        userId?.let { setUserStressData(it, date!!) }
        userId?.let { setUserSleepData(it, date!!) }
        userId?.let { setUserEmotionDiary(it, date!!) }

        // 오늘 날짜 설정, 달력 날짜 선택 -> 감정일지 작성
        binding.run {
            // 오늘 날짜
            val today: LocalDate = LocalDate.now()
            val year = today.year
            val month = today.month
            TextViewMonth.text =
                month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
            TextViewYear.text = year.toString()

            // HomeBar 설정
            popUpMenu.setOnClickListener { view ->
                showPopup(view)
            }

            // CalendarView 설정
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

                /* 날짜가 선택되었을 때의 동작 정의 */
                // 데이터 & 감정일지
                userId?.let { setUserStressData(it, date!!) }
                userId?.let { setUserSleepData(it, date!!) }
                userId?.let { setUserEmotionDiary(it, date!!) }

            }
        }
    }

    /* 선택 날짜 */
    // 데이터(스트레스, 수면) & 감정일지
    private fun setUserStressData(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateStressData(userId, date)
            .enqueue(object : Callback<StressData> {
                override fun onResponse(call: Call<StressData>, response: Response<StressData>) {
                    if (response.isSuccessful) {
                        response.body()?.let { binding.stressData.setText(it.stdAvg.toString()) }
                    } else {
                        binding.stressData.text = "None"
                    }
                }

                override fun onFailure(call: Call<StressData>, t: Throwable) {
                    // 네트워크 오류
                    //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setUserSleepData(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateSleepData(userId, date)
            .enqueue(object : Callback<SleepData> {
                override fun onResponse(call: Call<SleepData>, response: Response<SleepData>) {
                    if (response.isSuccessful) {
                        response.body()?.let { binding.sleepData.setText(it.sldAvg.toString()) }
                    } else {
                        binding.sleepData.text = "None"
                    }
                }

                override fun onFailure(call: Call<SleepData>, t: Throwable) {
                    // 네트워크 오류
                    //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setUserEmotionDiary(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateEmotionDiary(userId, date)
            .enqueue(object : Callback<EmotionDiary> {
                override fun onResponse(
                    call: Call<EmotionDiary>,
                    response: Response<EmotionDiary>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            binding.emotionDiaryTag.setText(it.emotionType + "\t" + it.emotionWord)
                            binding.emotionDiaryContent.setText(it.edReason)

                            // 감정일지 수정
                            binding.emotionDiary.setOnClickListener {
                                val intent =
                                    Intent(this@MonthlyActivity, UpdateEmotionDiaryActivity::class.java)
                                intent.putExtra("date", date)
                                launcher.launch(intent)
                            }
                        }
                    } else {
                        binding.emotionDiaryTag.text = ""
                        binding.emotionDiaryContent.setTextColor("#DADADA".toColorInt())
                        binding.emotionDiaryContent.text = "아직 추가된 오늘의 감정일지가 없습니다."

                        // 감정일지 작성
                        binding.emotionDiary.setOnClickListener {
                            val intent =
                                Intent(this@MonthlyActivity, WriteEmotionDiaryActivity::class.java)
                            intent.putExtra("date", date)
                            launcher.launch(intent)
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