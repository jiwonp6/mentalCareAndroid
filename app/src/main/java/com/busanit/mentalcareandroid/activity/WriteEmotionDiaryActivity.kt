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
import com.busanit.mentalcareandroid.databinding.ActivityWriteEmotionDiaryBinding
import com.busanit.mentalcareandroid.model.Emotion
import com.busanit.mentalcareandroid.model.EmotionDiary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteEmotionDiaryActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemClickListener {
    lateinit var binding: ActivityWriteEmotionDiaryBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedEmotion: Emotion? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(this)
        binding = ActivityWriteEmotionDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 감정 태그 list 출력 + 태그 클릭시 emotionId 전달
        emotionList()

        binding.run {
            buttonWrite.setOnClickListener {
                if (selectedEmotion == null) {
                    Toast.makeText(this@WriteEmotionDiaryActivity, "감정 태그를 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /* 메소드 */
    // 감정 태그 list
    private fun emotionList() {
        RetrofitClient.api.getEmotionList().enqueue(object : Callback<List<Emotion>> {
            override fun onResponse(call: Call<List<Emotion>>, response: Response<List<Emotion>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        binding.emotionList.adapter =
                            RecyclerViewAdapter(it, this@WriteEmotionDiaryActivity)
                        val layoutManager = GridLayoutManager(
                            this@WriteEmotionDiaryActivity,
                            4,
                            GridLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.emotionList.layoutManager = layoutManager
                    }
                } else {
                    binding.emotionList.adapter = null
                }
            }

            override fun onFailure(call: Call<List<Emotion>>, t: Throwable) {
                // 네트워크 오류 처리
            }
        })
    }

    // 감정 태그 클릭시
    override fun onItemClick(emotion: Emotion) {
        selectedEmotion = emotion

        // 선택된 아이템 값 전달 -> 감정 일지 작성
        binding.buttonWrite.setOnClickListener {
            sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)

            val userId = sharedPreferences.getString("userId", null)
            val edReason = binding.EditTextReason.text.toString()
            var edDate = intent.getStringExtra("date")!!
            val emotionId = emotion.emotionId
            val emotionType = emotion.emotionType
            val emotionWord = emotion.emotionWord

            userId?.let { it1 ->
                edDate.let { it2 ->
                    val emotionDiary =
                        EmotionDiary(userId, emotionId, emotionType, emotionWord, edReason, edDate)
                    writeEmotionDiary(it1, emotionDiary)

                }
            }

        }
    }

    // 감정 일지 작성
    fun writeEmotionDiary(userId: String, emotionDiary: EmotionDiary) {
        RetrofitClient.api.writeEmotionDiary(userId, emotionDiary)
            .enqueue(object : Callback<EmotionDiary> {
                override fun onResponse(
                    call: Call<EmotionDiary>,
                    response: Response<EmotionDiary>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(
                                this@WriteEmotionDiaryActivity,
                                "감정일지를 작성하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(this@WriteEmotionDiaryActivity, MonthlyActivity::class.java)
                            intent.putExtra("date", emotionDiary.edDate) // date 값을 Intent에 추가
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@WriteEmotionDiaryActivity,
                            "감정일지를 작성에 실패하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<EmotionDiary>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.d("mylog", "onFailure: ${t.cause}")
                    Log.d("mylog", "onFailure: ${t.message}")
                }
            })
    }

}
