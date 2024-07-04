package com.busanit.mentalcareandroid.hospital

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityHospitalBinding
import com.busanit.mentalcareandroid.reservation.MyReservationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HospitalActivity : AppCompatActivity() {
    lateinit var binding: ActivityHospitalBinding
    lateinit var hospitalList: List<Hospital>
    lateinit var hospitalAdapter: HospitalAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityHospitalBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // 병원명, 주소로 검색 및 필터
        hospitalList = emptyList()
        hospitalAdapter = HospitalAdapter(hospitalList)


        // 액티비티에서 어댑터 및 레이아웃 매니저 설정

        binding.recyclerView.adapter = hospitalAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        binding.editTextHospitalSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("text Changed");
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 버튼을 누르면 해당 페이지로 이동
        binding.run {

            myReservationIcon.setOnClickListener {
                val myReservation0 = Intent(this@HospitalActivity, MyReservationActivity::class.java)
                startActivity(myReservation0)
            }

            questionIcon.setOnClickListener {
                val question0 = Intent(this@HospitalActivity, QuestionActivity::class.java)
                startActivity(question0)
            }
        }

        // 시도별 Button 클릭 시 Bottom Sheet 표시 (LocalCode 설정)
        binding.localIcon.setOnClickListener {
            val bottomSheet = BottomSheetFragment()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        // 병원 정보 들고오기

        val api = RetrofitClient.api
        api.getHospitals().enqueue(object : Callback<List<Hospital>> {
            override fun onResponse(call: Call<List<Hospital>>, response: Response<List<Hospital>>) {
                if (response.isSuccessful) {
                    // 네트워킹에 성공할 경우 데이터를 가져옴 (엘비스 연산자로 null 처리)
                    val posts = response.body() ?: emptyList()
                    // 리사이클러뷰 어댑터 매개변수를 통해 데이터 전달 + 어댑터 연결
                    hospitalList = posts;
                    hospitalAdapter = HospitalAdapter(posts)
                    binding.recyclerView.adapter = hospitalAdapter
                } else {
                    handleServerError(response)  // 오류 처리 함수 호출
                }
            }

            override fun onFailure(call: Call<List<Hospital>>, t: Throwable) {
                handleNetworkError(t)
                // 실패 처리
            }
        })

        /*// 병원 검색 기능
        binding.run {
            searchButton.setOnClickListener {
                println("search btn");
                var searchText =  editTextHospitalSearch.text;
                filter(searchText.toString());
            }
        }*/
    }

    private fun filter(text: String) {
        Log.d("list size", hospitalList.size.toString());
        val filteredNameList = hospitalList.filter {
            it.hospitalName.contains(text, ignoreCase = true)
        }

        val filteredList = hospitalList.filter {
            it.hospitalLocation.contains(text, ignoreCase = true)
        }

        filteredNameList.containsAll(filteredList);

        if(filteredNameList.size == 0){
            hospitalAdapter.updateList(hospitalList)
            return;
        }

        Log.d("hospitalList", text);
        Log.d("hospitalList", filteredList.toString());
        hospitalAdapter.updateList(filteredNameList)
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
