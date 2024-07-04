package com.busanit.mentalcareandroid.reservation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.consultation.ConsultationActivity
import com.busanit.mentalcareandroid.consultation.MyConsultationActivity
import com.busanit.mentalcareandroid.databinding.MyReservationItemBinding
import com.busanit.mentalcareandroid.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MyReservationAdapter"

@RequiresApi(Build.VERSION_CODES.O)
class MyReservationAdapter(var reservations: List<Reservation>) :
    RecyclerView.Adapter<MyReservationAdapter.MyReservationViewHolder>() {
    inner class MyReservationViewHolder(val binding: MyReservationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 항목 뷰에 데이터를 바인딩
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(reservation: Reservation) {
            binding.hospitalName.text = reservation.hospitalName
            binding.textViewYear.text = "${reservation.reservationDate.year}년"
            binding.textViewMonthDay.text =
                "${reservation.reservationDate.month} ${reservation.reservationDate.dayOfMonth}일 "
            binding.textViewTime.text = "${reservation.reservationTime.toString()}"

            // 항목을 클릭했을 때 Consultation 액티비티를 시작하고 데이터 전달
            binding.root.setOnClickListener {

                val context = it.context
                getReservationByIdApi(RetrofitClient.api, reservation.reservationId!!, context)

            }

            binding.deleteReservationButton.setOnClickListener {

                val reservationId = reservation.reservationId!!
                Log.d(TAG, "bind: ${reservationId}")

                RetrofitClient.api.deleteReservation(reservationId).enqueue(object :
                    Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "onResponse: ${response.body()}")
                            Toast.makeText(it.context, "예약이 정상적으로 삭제되었습니다", Toast.LENGTH_SHORT)
                                .show()


                            // 데이터 가져오고 화면 갱신
                            val myReservationActivity =
                                binding.root.context as MyReservationActivity
                            myReservationActivity.getReservationByUserIdApi(
                                RetrofitClient.api,
                                "a"       // 하드코딩 유저 아이디
                            )

                        } else {
                            Log.d(TAG, "onResponse: 실패 ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReservationViewHolder {
        // XML 레이아웃을 인플레이트하여 뷰홀더 객체의 매개변수로 넣어 뷰홀더를 생성 반환
        val binding = MyReservationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyReservationViewHolder(binding)
    }

    override fun getItemCount(): Int = reservations.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyReservationViewHolder, position: Int) {
        // 주어진 위치의 데이터 객체를 뷰홀더에 바인딩
        holder.bind(reservations[position])

    }

    fun updateList(newList: List<Reservation>) {
        reservations = newList
        notifyDataSetChanged()
    }

    fun getReservationByIdApi(api: ApiService, reservationId: Long, context: Context) {
        api.getReservation(reservationId).enqueue(object : Callback<Reservation> {
            override fun onResponse(
                call: Call<Reservation>,
                response: Response<Reservation>
            ) {
                if (response.isSuccessful) {
                    // 네트워킹에 성공할 경우 데이터를 가져옴 (엘비스 연산자로 null 처리)
                    val posts = response.body()
                    Log.d(TAG, "onResponse: ${response.body()}")
                    val reservation = response.body()!!
                    val consultationId = reservation.consultationId


                    // 상담내역이 존재하면
                    if(consultationId != null) {
                        // 입력된 상담내역으로 이동
                        val intent = Intent(context, MyConsultationActivity::class.java)
                        intent.putExtra("consultationId", consultationId)
                        intent.putExtra("reservationId", reservation.reservationId)
                        intent.putExtra("hospitalName", reservation.hospitalName)
                        intent.putExtra("reservationYear", reservation.reservationDate.year.toString())
                        intent.putExtra("reservationMonthDay", "${reservation.reservationDate.month} ${reservation.reservationDate.dayOfMonth}일 ")
                        intent.putExtra("reservationTime", reservation.reservationTime.toString())
                        context.startActivity(intent)
                    } else {
                        // 존재하지 않으면 상담내역 입력으로 이동
                        val intent = Intent(context, ConsultationActivity::class.java)
                        intent.putExtra("reservationId", reservation.reservationId)
                        intent.putExtra("hospitalName", reservation.hospitalName)
                        intent.putExtra("reservationYear", reservation.reservationDate.year.toString())
                        intent.putExtra("reservationMonthDay", "${reservation.reservationDate.month} ${reservation.reservationDate.dayOfMonth}일 ")
                        intent.putExtra("reservationTime", reservation.reservationTime.toString())
                        context.startActivity(intent)
                    }



                } else {
                    handleServerError(response)  // 오류 처리 함수 호출
                }
            }

            override fun onFailure(call: Call<Reservation>, t: Throwable) {
                handleNetworkError(t)
                // 실패 처리
            }
        })


    }
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
    }

}
