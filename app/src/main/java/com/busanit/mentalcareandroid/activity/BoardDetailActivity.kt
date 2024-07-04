package com.busanit.mentalcareandroid.activity

import RetrofitClient
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.adapter.CommentAdapter
import com.busanit.mentalcareandroid.databinding.ActivityBoardDetailBinding
import com.busanit.mentalcareandroid.model.Board
import com.busanit.mentalcareandroid.model.Comment
import com.busanit.mentalcareandroid.model.Heart
import com.busanit.mentalcareandroid.model.HeartResponse
import com.busanit.mentalcareandroid.model.NewComment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "BoardDetailActivity"
class BoardDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityBoardDetailBinding
    lateinit var commentAdapter: CommentAdapter
    lateinit var comments: List<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 어댑터에서 데이터 받아오기
        val boardId = intent.getLongExtra("boardId", -1)
        val boardTitle = intent.getStringExtra("boardTitle")
        val userNickName = intent.getStringExtra("userNickname")
        val boardTime = intent.getStringExtra("boardTime")
        val boardContent = intent.getStringExtra("boardContent")
        val heartCount = intent.getIntExtra("heartCount", 0)
        val commentCount = intent.getIntExtra("commentCount", 0)
        val boardTag = intent.getStringExtra("boardTag")

        // 어댑터에서 받아온 데이터 입력
        inputBoardText(boardTitle, userNickName, boardTime, boardContent, heartCount, commentCount, boardTag)

        // 댓글 데이터 어댑터
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter()

        // 댓글 데이터 api
        RetrofitClient.api.getCommentsByBoardId(boardId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    comments = response.body() ?: emptyList<Comment>().toMutableList()
                    Log.d(TAG, "onResponse: ${response.body()}")
                    binding.recyclerView.adapter = commentAdapter
                    // 갱신을 위한 메소드
                    commentAdapter.updateComments(comments)
                } else {
                    Log.d(TAG, "onResponse: ${response.body()}")
                }
            }
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })


        // 하트 클릭시 공감 버튼 활성화
        binding.heart.setOnClickListener {
            // 객체 생성
            val heart = Heart(1)

            // 공감 생성 및 취소를 위한 api
            RetrofitClient.api.upAndDownHeart(heart, boardId)
                .enqueue(object : Callback<HeartResponse> {
                    override fun onResponse(call: Call<HeartResponse>, response: Response<HeartResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@BoardDetailActivity, "공감버튼 클릭", Toast.LENGTH_SHORT).show()

                            // 갯수 받아서 heartCount에 입력
                            val count = response.body()?.count
                            binding.heartCount.text = count.toString()
                        }
                    }
                    override fun onFailure(call: Call<HeartResponse>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message} ${t.printStackTrace()}")
                    }
                })
        }

        // 댓글 입력
        binding.commentButton.setOnClickListener {
            // 댓글 입력을 위한 데이터 받기 + 새로운 객체 생성
            val commentContent = binding.commentContent.text.toString()
            val newComment = NewComment(commentContent, "마이콜", boardId)

            // 댓글 추가를 위한 api
            RetrofitClient.api.createComment(newComment).enqueue(object : Callback<Comment> {
                override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@BoardDetailActivity, "새로운 댓글 작성", Toast.LENGTH_SHORT).show()
                        binding.commentContent.text.clear()
                        // 입력된 데이터 리사이클러뷰에 업데이트
                        val comment = response.body()!!
                        val commentMutableList = comments.toMutableList()
                        commentMutableList.add(comment)
                        commentAdapter.updateComments(commentMutableList.toList())
                        // 댓글 갯수 +1
                        binding.commentCount.text = (intent.getIntExtra("commentCount", 0) + 1).toString()
                    } else {
                        Log.d(TAG, "onResponse: ${response.body()}")
                    }
                }
                override fun onFailure(call: Call<Comment>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
        }

        // 게시글 삭제를 위한 api
        binding.boardDeleteButton.setOnClickListener {
            RetrofitClient.api.deleteBoard(boardId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    if (response.isSuccessful) {
                        // 게시글 삭제 이후 리사이클러뷰에 업데이트를 위해 boardId를 전달하는 인텐트
                        val resultIntent = Intent().apply {
                            putExtra("deletedBoardId", boardId) }
                        setResult(Activity.RESULT_OK, resultIntent)

                        Toast.makeText(this@BoardDetailActivity, "게시글이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Log.d(TAG, "onResponse: ${response.body()}")
                    }
                }
                override fun onFailure(call: Call<Board>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                }
            })
        }


        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                RetrofitClient.api.getBoardBYId(boardId).enqueue(object : Callback<Board> {
                    override fun onResponse(
                        call: Call<Board>,
                        response: Response<Board>
                    ) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "onResponse: 응답 성공 ${response.body()}")
                            inputBoardText(response.body()?.boardTitle, userNickName, boardTime, response.body()?.boardContent,
                                heartCount, commentCount, response.body()?.boardTag)
                        } else {
                            Log.d(TAG, "onResponse: 응답 실패 ${response.body()}")
                        }
                    }

                    override fun onFailure(call: Call<Board>, t: Throwable) {
                        Log.d(TAG, "onFailure: 연결 실패 ${t.message}")
                    }
                })
            }
        }

        binding.boardModifyButton.setOnClickListener {
            val boardTitle = intent.getStringExtra("boardTitle")
            val boardContent = intent.getStringExtra("boardContent")
            val userNickname = intent.getStringExtra("userNickname")
            val boardTag = intent.getStringExtra("boardTag")
            val boardLikeCount = intent.getIntExtra("boardLikeCount", -1)
            val boardCommentCount = intent.getIntExtra("boardCommentCount", -1)
            val calculateTime = intent.getStringExtra("calculateTime")

            val intent = Intent(this, UpdateActivity::class.java)
            intent.putExtra("boardId", boardId)
            intent.putExtra("boardTitle", boardTitle)
            intent.putExtra("boardContent", boardContent)
            intent.putExtra("userNickname", userNickname)
            intent.putExtra("boardTag", boardTag)
            intent.putExtra("boardLikeCount", boardLikeCount)
            intent.putExtra("boardCommentCount", boardCommentCount)
            intent.putExtra("calculateTime", calculateTime)
            activityResultLauncher.launch(intent)

        }

    }

    // 데이터 입력을 위한 메소드
    private fun inputBoardText(
        boardTitle: String?,
        userNickName: String?,
        boardTime: String?,
        boardContent: String?,
        heartCount: Int?,
        commentCount: Int?,
        boardTag : String?
    ) {
        binding.boardTitle.text = boardTitle
        binding.userNickName.text = userNickName
        binding.boardTime.text = boardTime
        binding.BoardContent.text = boardContent
        binding.heartCount.text = heartCount.toString()
        binding.commentCount.text = commentCount.toString()

        if (boardTag == "COMMON") {
            binding.boardTag.text = "일반고민 게시판"
        } else if (boardTag == "MENTAL") {
            binding.boardTag.text = "정신건강 게시판"
        } else {
            binding.boardTag.text = "응원 게시판"
        }

    }

}