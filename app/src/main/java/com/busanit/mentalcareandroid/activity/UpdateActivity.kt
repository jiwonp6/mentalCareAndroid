package com.busanit.mentalcareandroid.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.ActivityUpdateBinding
import com.busanit.mentalcareandroid.model.Board
import com.busanit.mentalcareandroid.model.UpdateBoard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "UpdateActivity"
class UpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boardId = intent.getLongExtra("boardId", -1)
        val boardTitleBefore = intent.getStringExtra("boardTitle")
        val boardContentBefore = intent.getStringExtra("boardContent")
        val boardTagBefore = intent.getStringExtra("boardTag")


        binding.updateEditTitle.setText(boardTitleBefore)
        binding.editContent.setText(boardContentBefore)
        when (boardTagBefore) {
            "COMMON" -> binding.commonButton.isChecked = true
            "MENTAL" -> binding.mentalButton.isChecked = true
            "CHEERING" -> binding.cheeringButton.isChecked = true
        }

        binding.completeButton.setOnClickListener {
            val boardTitle = binding.updateEditTitle.text.toString()
            val boardContent = binding.editContent.text.toString()
            var tag = "COMMON"

            when (binding.tagGroup.checkedRadioButtonId) {
                R.id.commonButton -> tag = "COMMON"
                R.id.mentalButton -> tag = "MENTAL"
                R.id.cheeringButton -> tag = "CHEERING"
            }

            val newBoard = UpdateBoard(boardContent, tag, boardTitle)

            RetrofitClient.api.updateBoard(newBoard, boardId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    if(response.isSuccessful) {
                        Toast.makeText(this@UpdateActivity, "게시글 수정 완료", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: 응답 성공 ${response.body()}")
                        val resultIntent = Intent(this@UpdateActivity, BoardDetailActivity::class.java).apply {
                            putExtra("newBoardContent", response.body()?.boardContent)
                            putExtra("newBoardTitle", response.body()?.boardTitle)
                            putExtra("newBoardTag", response.body()?.boardTag)
                            putExtra("modifyBoardId", response.body()?.boardId)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }else{
                        Log.d(TAG, "onResponse: 응답 실패 ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    Log.d(TAG, "onFailure: 연결 실패 ${t.message}")
                }
            })
        }


    }
}