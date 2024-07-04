package com.busanit.mentalcareandroid.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.activity.MonthlyActivity
import com.busanit.mentalcareandroid.activity.TitleActivity
import com.busanit.mentalcareandroid.activity.UpdateUserActivity
import com.busanit.mentalcareandroid.activity.WithdrawUserActivity
import com.busanit.mentalcareandroid.databinding.FragmentHomeBinding
import com.busanit.mentalcareandroid.model.EmotionDiary
import com.busanit.mentalcareandroid.model.SleepData
import com.busanit.mentalcareandroid.model.StressData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { RetrofitClient.initialize(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.run {
            sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE)

            // 닉네임
            TextViewNickname.text = sharedPreferences.getString("userNickname", null)

            // 오늘 날짜
            val today: LocalDate = LocalDate.now()
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM dd")
            date.text = today.format(formatter)
            val dayOfWeek: DayOfWeek = today.dayOfWeek
            dayOfTheWeek.text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            
            // popUpMenu -> 로그아웃, 회원 정보 수정, 회원 탈퇴
            popUpMenu.setOnClickListener { view ->
                showPopup(view)
            }

            // home contents 클릭 -> monthly 뷰로 이동
            homeContents.setOnClickListener{ monthlyView() }
            
            // 데이터 정보
            val userId = sharedPreferences.getString("userId", null)
            Log.d("mylog", "onCreateView: ${userId}")
            userId?.let { setUserStressData(it) }
            userId?.let { setUserSleepData(it) }

            // 감정 일지
            userId?.let { setUserEmotionDiary(it) }

        }

        return binding.root
    }

    /* 메뉴 */
    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
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
                Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                // Navigate to TitleActivity
                startActivity(Intent(activity, TitleActivity::class.java))
                activity?.finish()
                true
            }
            R.id.buttonUpdateUser -> {
                // Handle register action
                startActivity(Intent(activity, UpdateUserActivity::class.java))
                true
            }
            R.id.buttonWithdrawUser -> {
                // Handle withdraw user action
                startActivity(Intent(activity, WithdrawUserActivity::class.java))
                true
            }
            else -> false
        }
    }

    // 내용 클릭 -> monthly 뷰로 이동
    private fun monthlyView() {
        startActivity(Intent(activity, MonthlyActivity::class.java))
    }

    // 데이터(스트레스, 수면)
    private fun setUserStressData(userId: String) {
        RetrofitClient.api.getTodayStressData(userId).enqueue(object : Callback<StressData> {
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
    private fun setUserSleepData(userId: String) {
        RetrofitClient.api.getTodaySleepData(userId).enqueue(object : Callback<SleepData> {
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

    // 감정 일지
    private fun setUserEmotionDiary(userId: String) {
        RetrofitClient.api.getTodayEmotionDiary(userId).enqueue(object : Callback<EmotionDiary> {
            override fun onResponse(call: Call<EmotionDiary>, response: Response<EmotionDiary>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.emotionDiaryTag.setText(it.emotionType + "\t" + it.emotionWord)
                        binding.emotionDiaryContent.setText(it.edReason)
                    }
                } else {
                    binding.emotionDiaryTag.text = ""
                    binding.emotionDiaryContent.setTextColor("#DADADA".toColorInt())
                    binding.emotionDiaryContent.text = "아직 추가된 오늘의 감정일지가 없습니다."
                }
            }
            override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

}



