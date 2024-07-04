package com.busanit.mentalcareandroid.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.databinding.ChildrenItemBinding
import com.busanit.mentalcareandroid.diff.DiffUtilCallback
import com.busanit.mentalcareandroid.model.ChildrenComment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ChildrenAdapter"
class ChildrenAdapter  : RecyclerView.Adapter<ChildrenAdapter.ItemViewHolder>() {
    private var childrenComments: MutableList<ChildrenComment> = mutableListOf<ChildrenComment>()

    inner class ItemViewHolder(val binding: ChildrenItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(children: ChildrenComment) {
            binding.commentUser.text = children.userNickname
            binding.commentContent.text = children.childrenContent
            binding.commentTime.text = children.childrenTime

            binding.commentDeleteButton.setOnClickListener {
                RetrofitClient.api.deleteChildrenComment(children.childrenId).enqueue(object :
                    Callback<ChildrenComment> {
                    override fun onResponse(
                        call: Call<ChildrenComment>,
                        response: Response<ChildrenComment>
                    ) {
                        if(response.isSuccessful) {
                            removeByChildrenId(children.childrenId)
                            Toast.makeText(it.context, "대댓글 삭제 성공", Toast.LENGTH_SHORT).show()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ChildrenItemBinding.inflate(LayoutInflater.from(parent.context), parent, false )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = childrenComments.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(childrenComments[position])
    }

    // 댓글 목록 갱신
    fun updateChildren(newChildren: MutableList<ChildrenComment>?) {
        newChildren?.let {
            val diffCallback = DiffUtilCallback(this.childrenComments, newChildren)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.childrenComments.run {
                clear()
                addAll(newChildren)
                diffResult.dispatchUpdatesTo(this@ChildrenAdapter)
            }
        }
    }

    fun removeByChildrenId(childrenId: Long) {
        val position = childrenComments.indexOfFirst { it.childrenId == childrenId }

        if (position != -1) {
            childrenComments.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}