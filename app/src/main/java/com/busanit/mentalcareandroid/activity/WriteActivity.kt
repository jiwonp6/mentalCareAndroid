package com.busanit.mentalcareandroid.activity

import android.content.SharedPreferences
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
import com.busanit.mentalcareandroid.adapter.BoardAdapter
import com.busanit.mentalcareandroid.databinding.ActivityWriteBinding
import com.busanit.mentalcareandroid.model.Board
import com.busanit.mentalcareandroid.model.NewBoard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteBinding
    lateinit var boards: List<Board>
    lateinit var boardAdapter: BoardAdapter
    lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)


        binding.run {
            completeButton.setOnClickListener {
                val title = editTitle.text.toString()
                val content = editContent.text.toString()
                var tag = "COMMON"

                when(tagGroup.checkedRadioButtonId) {
                    R.id.commonButton -> tag = "COMMON"
                    R.id.mentalButton -> tag = "MENTAL"
                    R.id.cheeringButton -> tag = "CHEERING"
                }

                val userNickname = sharedPreferences.getString("userNickname", null).toString()

                val newBoard = NewBoard(tag, title, content, userNickname)
                boards = mutableListOf()

                RetrofitClient.api.createBoard(newBoard).enqueue(object : Callback<Board> {
                    override fun onResponse(call: Call<Board>, response: Response<Board>) {
                        if (response.isSuccessful) {0
                            Toast.makeText(this@WriteActivity, "새로운 게시글 작성이 완료되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("mylog", "onResponse: ${response.body()}")
                            val board = response.body()!!
                            val boardMutableList = boards.toMutableList()
                            boardMutableList.add(board)
                            boardAdapter.updateBoards(boardMutableList.toList())
                            finish()
                        } else {
                            Toast.makeText(this@WriteActivity, "응답 실패 ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Board>, t: Throwable) {
                        Log.d("mylog", "onFailure: ${t.message}")
                    }
                })
            }
        }
    }
}