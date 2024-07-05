package com.busanit.mentalcareandroid.reservation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.busanit.mentalcareandroid.databinding.ReservationAvailableBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime

private const val TAG = "BottomSheetFragmentRese"
// 2. 프래그먼트 작성 : 캘린더에 날짜를 클릭하면 예약 시간 프래그먼트가 뜨도록
class BottomSheetFragmentReservationAvail : BottomSheetDialogFragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: ReservationAvailableBinding
    var time: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReservationAvailableBinding.inflate(inflater, container, false)
        // XML 레이아웃 연결
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reservationActivity = activity as ReservationActivity

        Log.d(TAG, "onViewCreated: ${reservationActivity.selectedYear}")
        Log.d(TAG, "onViewCreated: ${reservationActivity.selectedMonth}")
        Log.d(TAG, "onViewCreated: ${reservationActivity.selectedDayOfMonth}")



        binding.run {
            val textViewList: List<TextView>
            textViewList = listOf(oclock9, oclock10, oclock11, oclock1, oclock2, oclock3, oclock4, oclock5, oclock6)

            for (textView in textViewList) {
                clockHandler(textView)
            }

            finishReservation.setOnClickListener {
                val finishReser = Intent(context, MyReservationActivity::class.java)
                val year = (activity as ReservationActivity).selectedYear
                val month = (activity as ReservationActivity).selectedMonth
                val dayOfMonth = (activity as ReservationActivity).selectedDayOfMonth
                val hospitalId = (activity as ReservationActivity).hospitalId

                val time = time.split(":").get(0).toInt() // "9:00" -> 9

                val reservationDate = LocalDate.of(year, month, dayOfMonth )
                val reservationTime = LocalTime.of(time,0)

                sharedPreferences =
                    context?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)!!
                val userId = sharedPreferences.getString("userId", null)  // 하드코딩 유저 아이디
                val reservation = Reservation(null, userId, reservationDate, reservationTime,  hospitalId, null, null)
                Log.d(TAG, "onViewCreated: ${reservation}")
                // 1. 레트로핏 네트워크 요청
                RetrofitClient.api.createReservation(reservation).enqueue(object :
                    Callback<Reservation> {
                    override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "예약이 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            val reservationIntent = Intent(context, MyReservationActivity::class.java)
                            Log.d(TAG, "onResponse: response.body():${response.body()}")
                            val reservationId = response.body()!!.reservationId
                            Log.d(TAG, "onResponse: reservationId ${reservationId}")
                            reservationIntent.putExtra("reservationId", reservationId)
                            startActivity(reservationIntent)
                        } else {
                            Log.d(TAG, "onResponse: ${response.code()}")
                            Toast.makeText(context, "응답 실패 ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Reservation>, t: Throwable) {
                        // 실패 처리 (네트워크 요청 자체가 안되어 예외(t)를 던짐.
                        Log.d(TAG, "onResponse: ${t.printStackTrace()}")
                        Toast.makeText(context, "네트워크 요청 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })

                // 1-1. Reservation 객체 필요
                Log.d(TAG, "onViewCreated: ${time}")
                startActivity(finishReser)

            }
        }

    }

    private fun clockHandler(textView: TextView) {
        textView.setOnClickListener {
            textView.isSelected = !textView.isSelected  // 선택
            time = textView.text.toString()             // 시간설정
            if (it.isSelected) {
                textView.setBackgroundColor(Color.LTGRAY)
            } else {
                textView.setBackgroundColor(Color.parseColor("@drawable/edit_text_border"))
            }
        }
    }

}


