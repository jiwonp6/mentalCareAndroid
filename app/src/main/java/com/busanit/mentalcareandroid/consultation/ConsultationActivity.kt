package com.busanit.mentalcareandroid.consultation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityConsultationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ConsultationActivity"
class ConsultationActivity : AppCompatActivity() {
    lateinit var binding: ActivityConsultationBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityConsultationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val reservationId = intent.getLongExtra("reservationId", 0)
        val strHospitalName = intent.getStringExtra("hospitalName")
        val strReservationYear = intent.getStringExtra("reservationYear")
        val strReservationMonthDay = intent.getStringExtra("reservationMonthDay")
        val strReservationTime = intent.getStringExtra("reservationTime")

        binding.run {

            hospitalName.text = strHospitalName
            textViewYear.text = "${strReservationYear}년"
            textViewMonthDay.text = "${strReservationMonthDay}"
            textViewTime.text = strReservationTime

            button.setOnClickListener {
                // 사용자로부터 데이터를 입력받아 데이터 객체 생성
                val contentConsul = contentConsul.text.toString()
                val changeConsul = editText2.text.toString()
                // val picture = picture.

                val consultation = Consultation(reservationId, null, contentConsul, changeConsul, null)
                RetrofitClient.api.createConsultation(consultation).enqueue(object :
                    Callback<Consultation> {
                    override fun onResponse(call: Call<Consultation>, response: Response<Consultation>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ConsultationActivity, "새 글 쓰기 성공했습니다.", Toast.LENGTH_SHORT).show()
                            val id = response.body()?.consultationId
                            val intent = Intent(this@ConsultationActivity, MyConsultationActivity::class.java)
                            intent.putExtra("consultationId", id)
                            intent.putExtra("reservationId", reservationId)
                            intent.putExtra("hospitalName", strHospitalName)
                            intent.putExtra("reservationYear", strReservationYear)
                            intent.putExtra("reservationMonthDay", strReservationMonthDay)
                            intent.putExtra("reservationTime",strReservationTime)
                            startActivity(intent)

                            finish()  // 새 글 작성 성공시, Activity 종료, 이전으로 돌아감
                        } else {
                            Toast.makeText(this@ConsultationActivity, "응답 실패 ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Consultation>, t: Throwable) {
                        // 실패 처리 (네트워크 요청 자체가 안되어 예외(t)를 던짐.
                        Toast.makeText(this@ConsultationActivity, "네트워크 요청 실패 ${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })



            }
        }

    }
}