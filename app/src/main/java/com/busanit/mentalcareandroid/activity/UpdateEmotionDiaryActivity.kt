package com.busanit.mentalcareandroid.activity

import RecyclerViewAdapter
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.busanit.mentalcareandroid.databinding.ActivityUpdateEmotionDiaryBinding
import com.busanit.mentalcareandroid.model.Emotion
import com.busanit.mentalcareandroid.model.EmotionDiary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateEmotionDiaryActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {
    lateinit var binding: ActivityUpdateEmotionDiaryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedEmotion: Emotion? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityUpdateEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val date = intent.getStringExtra("date")

        // 감정 태그 list 출력 + 태그 클릭시 emotionId 전달 + 감정 일지 수정
        if (userId != null && date != null) {
            setOriginInfo(userId, date)
        } else {
            Toast.makeText(this, "Invalid user ID or date", Toast.LENGTH_SHORT).show()
        }

        binding.run {
            // 선택된 아이템 값 전달 -> 감정 일지 작성
            buttonUpdate.setOnClickListener {
                val edReason = EditTextReason.text.toString()
                if (selectedEmotion != null && userId != null && date != null) {
                    val emotionDiary = EmotionDiary(

                        userId,
                        selectedEmotion!!.emotionId,
                        selectedEmotion!!.emotionType,
                        selectedEmotion!!.emotionWord,
                        edReason,
                        date
                    )
                    updateEmotionDiary(userId, date, emotionDiary)
                } else {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "Please select an emotion and enter a reason.", Toast.LENGTH_SHORT).show()
                }
            }

            // 감정 일지 삭제
            buttonDelete.setOnClickListener {
                deleteEmotionDiary(userId!!, date!!)
            }

        }
    }

    // 원 정보 불러오기
    private fun setOriginInfo(userId: String, date: String) {
        RetrofitClient.api.getSelectedDateEmotionDiary(userId, date).enqueue(object : Callback<EmotionDiary> {
            override fun onResponse(call: Call<EmotionDiary>, response: Response<EmotionDiary>) {
                Log.d("mylog", "!!!onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let { originEmotionDiary ->
                        binding.EditTextReason.setText(originEmotionDiary.edReason)
                        selectedEmotion = Emotion(originEmotionDiary.emotionId, originEmotionDiary.emotionType, originEmotionDiary.emotionWord)
                        emotionList(originEmotionDiary.emotionId)
                    }
                } else {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "Failed to load original diary.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                Toast.makeText(this@UpdateEmotionDiaryActivity, "Network error occurred.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 감정 태그 list
    private fun emotionList(selectedEmotionId: Long) {
        RetrofitClient.api.getEmotionList().enqueue(object : Callback<List<Emotion>> {
            override fun onResponse(call: Call<List<Emotion>>, response: Response<List<Emotion>>) {
                Log.d("mylog", "onResponse: ${selectedEmotionId}")
                if (response.isSuccessful) {
                    response.body()?.let { emotionList ->
                        val adapter = RecyclerViewAdapter(emotionList, this@UpdateEmotionDiaryActivity)

                        adapter.setSelectedEmotionId(selectedEmotionId)
                        binding.emotionList.adapter = adapter
                        binding.emotionList.layoutManager = GridLayoutManager(this@UpdateEmotionDiaryActivity, 4, GridLayoutManager.HORIZONTAL, false)
                    }
                } else {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "Failed to load emotion list.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Emotion>>, t: Throwable) {
                Log.e("UpdateEmotionDiary", "Failed to fetch emotion list", t)
            }
        })
    }

    // 감정 태그 선택시
    override fun onItemClick(emotion: Emotion) { selectedEmotion = emotion }

    // 감정 일지 수정
    private fun updateEmotionDiary(userId: String, date: String, emotionDiary: EmotionDiary) {
        RetrofitClient.api.updateEmotionDiary(userId, date, emotionDiary).enqueue(object : Callback<EmotionDiary> {
            override fun onResponse(call: Call<EmotionDiary>, response: Response<EmotionDiary>) {
                Log.d("mylog", "수정->onResponse: ${response.body()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "감정 일지를 수정하였습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent().apply { putExtra("date", emotionDiary.edDate) }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "감정 일지 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                Log.d("mylog", "onFailure: ${t.cause}")
                Log.d("mylog", "onFailure: ${t.message}")
            }
        })
    }
    
    // 감정 일지 삭제
    private fun deleteEmotionDiary(userId: String, date: String) {
        RetrofitClient.api.deleteEmotionDiary(userId, date).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    response.body()?.let { num -> num > 0
                        Toast.makeText(this@UpdateEmotionDiaryActivity, "감정 일지를 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent().apply { putExtra("date", date) }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@UpdateEmotionDiaryActivity, "감정 일지 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(this@UpdateEmotionDiaryActivity, "Network error occurred.", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
