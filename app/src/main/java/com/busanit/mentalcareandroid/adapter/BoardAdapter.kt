package com.busanit.mentalcareandroid.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.activity.BoardDetailActivity
import com.busanit.mentalcareandroid.databinding.BoardItemBinding
import com.busanit.mentalcareandroid.diff.DiffUtilCallback
import com.busanit.mentalcareandroid.model.Board
import java.util.Collections.addAll

private const val TAG = "BoardAdapter"

class BoardAdapter(val activityResultLauncher: ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<BoardAdapter.ItemViewHolder>() {
    private val boards = mutableListOf<Board>()

    // 매개변수로 항목을 레이아웃 뷰 바인딩을 삽입
    inner class ItemViewHolder(val binding: BoardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(board: Board) {
            binding.boardTitle.text = board.boardTitle
            binding.boardContent.text = board.boardContent
            binding.boardTime.text = board.calculateTime
            binding.userNickName.text = board.userNickname
            binding.heartCount.text = board.boardLikeCount.toString()
            binding.commentCount.text = board.boardCommentCount.toString()

            val context = binding.root.context
            binding.root.setOnClickListener {
                val intent = Intent(context, BoardDetailActivity::class.java)
                intent.putExtra("boardId", board.boardId)
                intent.putExtra("boardTitle", board.boardTitle)
                intent.putExtra("boardContent", board.boardContent)
                intent.putExtra("userNickname", board.userNickname)
                intent.putExtra("heartCount", board.boardLikeCount)
                intent.putExtra("commentCount", board.boardCommentCount)
                intent.putExtra("boardTag", board.boardTag)
                intent.putExtra("boardTime", board.calculateTime)
                activityResultLauncher.launch(intent)
            }


        }
    }

    // 어댑터의 메서드 구현
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = BoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // 데이터와 뷰홀더 바인딩
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(boards[position])
    }

    // 데이터의 개수
    override fun getItemCount(): Int = boards.size

    // 게시글 목록 갱신
    fun updateBoards(newBoards: List<Board>?) {
        newBoards?.let {
            val diffCallback = DiffUtilCallback(this.boards, newBoards)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.boards.run {
                clear()
                addAll(newBoards)
                diffResult.dispatchUpdatesTo(this@BoardAdapter)
            }
        }
    }

    fun removeByBoardId(boardId: Long) {
        val position = boards.indexOfFirst { it.boardId == boardId }

        if (position != -1) {
            boards.removeAt(position)
            notifyItemRemoved(position)
        }
    }




}