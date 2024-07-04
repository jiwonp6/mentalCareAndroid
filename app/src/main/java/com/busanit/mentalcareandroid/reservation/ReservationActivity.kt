package com.busanit.mentalcareandroid.reservation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityReservationBinding
import com.busanit.mentalcareandroid.hospital.Hospital
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

private const val TAG = "ReservationActivity"
class ReservationActivity : AppCompatActivity() {
    lateinit var binding: ActivityReservationBinding
    lateinit var hospital: Hospital
    var hospitalId: String = ""
    var selectedYear: Int = -1
    var selectedMonth: Int = -1
    var selectedDayOfMonth: Int = -1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityReservationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        hospitalId = intent.getStringExtra("hospitalId")!!

        Log.d(TAG, "onCreate: ${hospitalId}")
        val api = RetrofitClient.api
        api.getHospital(hospitalId).enqueue(object : Callback<Hospital> {
            override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                if (response.isSuccessful) {
                    // 네트워킹에 성공할 경우 데이터를 가져옴 (엘비스 연산자로 null 처리)
                    hospital = response.body()!!
                    // 각각의 칸 안에 병원 정보들 넣기
                    binding.hospitalName.text = hospital.hospitalName
                    binding.address.text = hospital.hospitalLocation
                    binding.callNumber.text = hospital.hospitalCall
                    binding.website.text = hospital.hospitalWebsite

                    // mon_start_time(빈칸 id) 가 자동으로 monStartTime으로 바뀜
                    binding.monStartTime.text = hospital.monStartTime
                    binding.monEndTime.text = hospital.monEndTime
                    binding.tueStartTime.text = hospital.tueStartTime
                    binding.tueEndTime.text = hospital.tueEndTime
                    binding.wedStartTime.text = hospital.wedStartTime
                    binding.wedEndTime.text = hospital.wedEndTime
                    binding.thuStartTime.text = hospital.thuStartTime
                    binding.thuEndTime.text = hospital.thuEndTime
                    binding.friStartTime.text = hospital.friStartTime
                    binding.friEndTime.text = hospital.friEndTime
                    binding.satStartTime.text = hospital.satStartTime
                    binding.satEndTime.text = hospital.satEndTime
                    binding.sunStartTime.text = hospital.sunStartTime
                    binding.sunEndTime.text = hospital.sunEndTime
                    binding.sunHoliday.text = hospital.sunHoliday
                    binding.lunchtime.text = hospital.lunchtime
                    binding.holiday.text = hospital.holiday


                    // 캘린더 선택 이벤트 처리
                    binding.ReserCalender.setOnDateChangeListener { view, year, month, dayOfMonth ->

                        // Calendar 객체를 사용하여 선택된 날짜를 설정합니다.
                        val calendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        selectedYear = year
                        selectedMonth = month
                        selectedDayOfMonth = dayOfMonth

                        // 선택된 날짜의 요일을 가져옵니다.
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                        // 요일을 문자열로 변환합니다.
                        val dayOfWeekString = getDayOfWeekString(dayOfWeek)

                        Log.d(TAG, "onCreate: ${dayOfWeekString}")

                        val date = "$dayOfMonth/${month + 1}/$year"
                        Toast.makeText(this@ReservationActivity, "Selected Date: $date", Toast.LENGTH_SHORT).show()

                        when (dayOfWeekString) {
                            "Sunday" -> { if(hospital.sunStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}
                            "Monday" -> {if(hospital.monStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 월요일
                            "Tuesday" -> {if(hospital.tueStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 화요일
                            "Wednesday" -> {if(hospital.wedStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 수요일
                            "Thursday" -> {if(hospital.thuStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 목요일
                            "Friday" -> {if(hospital.friStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 금요일
                            "Saturday" -> {if(hospital.satStartTime == "00:00") {
                                val bottomSheet = BottomSheetFragmentReservationNot()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            } else {
                                val bottomSheet = BottomSheetFragmentReservationAvail()
                                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                            }}// 토요일
                        }

                    }
                } else {
                    handleServerError(response)  // 오류 처리 함수 호출
                }
            }

            override fun onFailure(call: Call<Hospital>, t: Throwable) {
                handleNetworkError(t)
                // 실패 처리
            }
        })





    }

    // 숫자 형태의 요일을 문자열로 변환하는 메서드입니다.
    private fun getDayOfWeekString(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday" // 일요일
            Calendar.MONDAY -> "Monday" // 월요일
            Calendar.TUESDAY -> "Tuesday" // 화요일
            Calendar.WEDNESDAY -> "Wednesday" // 수요일
            Calendar.THURSDAY -> "Thursday" // 목요일
            Calendar.FRIDAY -> "Friday" // 금요일
            Calendar.SATURDAY -> "Saturday" // 토요일
            else -> ""
        }
    }
    // 레트로핏 오류 처리
    // 응답은 하였으나 성공(200번대)이 아닌 경우 핸들러
    private fun handleServerError(response: Response<*>) {
        when (response.code()) {
            400 -> Log.d("mylog", "400 Bad Request ${response.message()}")
            401 -> Log.d("mylog", "401 Unauthorized ${response.message()}")
            403 -> Log.d("mylog", "403 Forbidden ${response.message()}")
            404 -> Log.d("mylog", "404 Not Found ${response.message()}")
            500 -> Log.d("mylog", "500 Server Error ${response.message()}")
            else -> Log.d("mylog", "Response Error ${response.message()}")
        }
    }
    // 네트워크 요청 자체가 실패한 경우 핸들러
    private fun handleNetworkError(t: Throwable) {
        Log.d("mylog", "Network Error ${t.message}")
        Log.d("mylog", "Network Error ${t.printStackTrace()}")
        Toast.makeText(this, "네트워크 요청실패", Toast.LENGTH_SHORT).show()
    }


}