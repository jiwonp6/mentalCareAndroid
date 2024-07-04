package com.busanit.mentalcareandroid.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.activity.BoardDetailActivity
import com.busanit.mentalcareandroid.activity.CommentDetailActivity
import com.busanit.mentalcareandroid.databinding.CommentItemBinding
import com.busanit.mentalcareandroid.diff.CommentDiff
import com.busanit.mentalcareandroid.model.ChildrenComment
import com.busanit.mentalcareandroid.model.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CommentAdapter"
class CommentAdapter: RecyclerView.Adapter<CommentAdapter.ItemViewHolder>() {
    private var comments = mutableListOf<Comment>()
    lateinit var childrenComments : List<ChildrenComment>
    lateinit var childrenAdapter : ChildrenAdapter


    inner class ItemViewHolder(val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentUser.text = comment.userNickname
            binding.commentContent.text = comment.commentContent
            binding.commentTime.text = comment.commentTime

            val context = binding.root.context

            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            childrenAdapter = ChildrenAdapter()

            RetrofitClient.api.getChildrenByCommentId(comment.commentId).enqueue(object :
                Callback<List<ChildrenComment>> {
                override fun onResponse(call: Call<List<ChildrenComment>>, response: Response<List<ChildrenComment>>) {
                    if(response.isSuccessful) {
                        childrenComments = response.body() ?: emptyList()
                        binding.recyclerView.adapter = childrenAdapter
                        childrenAdapter.updateChildren(childrenComments.toMutableList()) // 이 문장 없으면 나타나지 않음
                        Log.d(TAG, "onResponse: 응답 성공 ${childrenComments}")
                    } else {
                        Log.d(TAG, "onResponse: 응답 실패 ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<List<ChildrenComment>>, t: Throwable) {
                    Log.d(TAG, "onFailure: 연결 실패 ${t.message}")
                }
            })

            binding.commentDeleteButton.setOnClickListener {
                RetrofitClient.api.deleteComment(comment.commentId).enqueue(object :
                    Callback<Comment> {
                    override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                        if(response.isSuccessful) {
                            removeByCommentId(comment.commentId)
                            Log.d(TAG, "onResponse: ${response.body()}")
                            Toast.makeText(it.context, "댓글이 정상적으로 삭제되었습니다", Toast.LENGTH_SHORT).show()

                            val activity = binding.root.context as BoardDetailActivity
                            val count = activity.binding.commentCount.text.toString().toInt()
                            activity.binding.commentCount.text = (count - 1).toString()


                        }
                    }
                    override fun onFailure(call: Call<Comment>, t: Throwable) {
                        Log.d(TAG, "onFailure: ${t.message}")
                    }
                })

            }

            binding.childrenButton.setOnClickListener {
                val intent = Intent(context, CommentDetailActivity::class.java)
                intent.putExtra("commentId", comment.commentId)
                intent.putExtra("commentContent", comment.commentContent)
                intent.putExtra("userNickname", comment.userNickname)
                intent.putExtra("commentTime", comment.commentTime)
                context.startActivity(intent)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(comments[position])
    }


    fun updateComments(newComments: List<Comment>) {
        newComments?.let {
            val diffCallback = CommentDiff(this.comments, newComments)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.comments.run {
                clear()
                addAll(newComments)
                diffResult.dispatchUpdatesTo(this@CommentAdapter)
            }
        }
    }

    fun removeByCommentId(commentId: Long) {
        val position = comments.indexOfFirst { it.commentId == commentId }
        if (position != -1) {
            if(childrenComments.isNotEmpty()){
                Log.d(TAG, "removeByCommentId: childrenComments ${childrenComments}")
                comments[position].commentContent = "삭제된 댓글입니다"
                notifyItemChanged(position)
            } else {
                comments.removeAt(position)
                notifyItemRemoved(position)
            }

        }
    }
}