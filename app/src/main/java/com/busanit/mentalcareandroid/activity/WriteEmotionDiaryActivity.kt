package com.busanit.mentalcareandroid.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.RetrofitClient
import com.busanit.mentalcareandroid.adapter.RecyclerViewAdapter
import com.busanit.mentalcareandroid.databinding.ActivityWriteEmotionDiaryBinding
import com.busanit.mentalcareandroid.model.Emotion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteEmotionDiaryActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteEmotionDiaryBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emotionList()
    }
    private fun emotionList() {
        RetrofitClient.api.getEmotionList().enqueue(object : Callback<List<Emotion>> {
            override fun onResponse(call: Call<List<Emotion>>, response: Response<List<Emotion>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.emotionList.adapter = RecyclerViewAdapter(response.body()!!)
                        binding.emotionList.layoutManager = LinearLayoutManager(this@WriteEmotionDiaryActivity)
                    }
                } else {
                    binding.emotionList.adapter = null
                }
            }
            override fun onFailure(call: Call<List<Emotion>>, t: Throwable) {
                // 네트워크 오류
                //Toast.makeText(this@HomeFragment, "회원 정보 로딩 네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}