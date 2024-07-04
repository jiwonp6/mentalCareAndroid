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
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.MyReservationBinding
import com.busanit.mentalcareandroid.databinding.MyReservationItemBinding
import com.busanit.mentalcareandroid.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MyReservationActivity"
class MyReservationActivity : AppCompatActivity() {
    lateinit var binding: MyReservationBinding
    lateinit var binding1: MyReservationItemBinding
    lateinit var adapter: MyReservationAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= MyReservationBinding.inflate(layoutInflater)
        binding1= MyReservationItemBinding.inflate(layoutInflater, binding.root, false)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        adapter = MyReservationAdapter(listOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MyReservationActivity)
        binding.recyclerView.adapter = adapter



        val userId = "a"  // 하드코딩 유저 아이디
        val reservationId = intent.getLongExtra("reservationId", 0)
        val api = RetrofitClient.api
        Log.d(TAG, "onCreate: reservationId : ${reservationId}")

        getReservationByUserIdApi(api, userId)

    }



    fun getReservationByUserIdApi(api: ApiService, userId: String) {
        api.getReservationByUserId(userId).enqueue(object : Callback<List<Reservation>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Reservation>>,
                response: Response<List<Reservation>>
            ) {
                if (response.isSuccessful) {
                    // 네트워킹에 성공할 경우 데이터를 가져옴 (엘비스 연산자로 null 처리)
                    val posts = response.body()
                    Log.d(TAG, "onResponse: ${response.body()}")
                    val reservations = response.body() ?: emptyList<Reservation>()
                    adapter.updateList(reservations)
                } else {
                    handleServerError(response)  // 오류 처리 함수 호출
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                handleNetworkError(t)
                // 실패 처리
            }
        })
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
        Toast.makeText(this, "네트워크 요청실패", Toast.LENGTH_SHORT).show()
    }

}

