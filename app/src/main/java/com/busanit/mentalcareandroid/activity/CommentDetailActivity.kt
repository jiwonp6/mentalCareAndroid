package com.busanit.mentalcareandroid.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.adapter.ChildrenAdapter
import com.busanit.mentalcareandroid.databinding.ActivityCommentDetailBinding
import com.busanit.mentalcareandroid.model.ChildrenComment
import com.busanit.mentalcareandroid.model.NewChildren
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CommentDetailActivity"
class CommentDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityCommentDetailBinding
    lateinit var childrenComments : List<ChildrenComment>
    lateinit var childrenAdapter: ChildrenAdapter
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_pref", MODE_PRIVATE)

        binding.commentContent.text = intent.getStringExtra("commentContent")
        binding.commentTime.text = intent.getStringExtra("commentTime")
        binding.commentUser.text = intent.getStringExtra("userNickname")

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        childrenAdapter = ChildrenAdapter()

        val commentId = intent.getLongExtra("commentId", -1)

        RetrofitClient.api.getChildrenByCommentId(commentId).enqueue(object :
            Callback<List<ChildrenComment>> {
            override fun onResponse(call: Call<List<ChildrenComment>>, response: Response<List<ChildrenComment>>) {
                if(response.isSuccessful) {
                    childrenComments = response.body() ?: emptyList<ChildrenComment>().toMutableList()
                    binding.recyclerView.adapter = childrenAdapter
                    childrenAdapter.updateChildren(childrenComments.toMutableList())
                    Log.d(TAG, "onResponse: 응답 성공 ${response.body()}")

                } else {
                    Log.d(TAG, "onResponse: 응답 실패 ${response.body()}")
                }
            }

            override fun onFailure(call: Call<List<ChildrenComment>>, t: Throwable) {
                Log.d(TAG, "onFailure: 연결 실패 ${t.message}")
            }
        })


        binding.commentButton.setOnClickListener {
            val childrenContent = binding.commentContentWrite.text.toString()
            val userNickname = sharedPreferences.getString("userNickname", null).toString()
            val newChildren = NewChildren(childrenContent, userNickname, commentId)

            RetrofitClient.api.createChildren(newChildren).enqueue(object :
                Callback<ChildrenComment> {
                override fun onResponse(
                    call: Call<ChildrenComment>,
                    response: Response<ChildrenComment>
                ) {
                    if(response.isSuccessful) {
                        val childrenComment = response.body()!!
                        val childrenMutableList = childrenComments.toMutableList()
                        childrenMutableList.add(childrenComment)
                        childrenAdapter.updateChildren(childrenMutableList)
                        binding.commentContentWrite.text.clear()
                        Toast.makeText(this@CommentDetailActivity, "답글 작성 완료", Toast.LENGTH_SHORT).show()

                        Log.d(TAG, "onResponse: 응답 성공 ${response.body()}")
                    } else {
                        Log.d(TAG, "onResponse: 응답 실패 ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<ChildrenComment>, t: Throwable) {
                    Log.d(TAG, "onFailure: 연결 실패 ${t.message}")
                }
            })
        }

    }
}