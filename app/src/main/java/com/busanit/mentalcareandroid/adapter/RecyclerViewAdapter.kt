package com.busanit.mentalcareandroid.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.databinding.EmotionItemBinding
import com.busanit.mentalcareandroid.model.Emotion

class RecyclerViewAdapter(val emotionList: List<Emotion>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(val binding: EmotionItemBinding): RecyclerView.ViewHolder(binding.root) {
        // 뷰 홀더를 클랙 시 이벤트 리스너 설정
        init {
            binding.root.setOnClickListener {
                val emotionType = binding.emotionType.text.toString()
                val emotionWord = binding.emotionWord.text.toString()
                Log.d("mylog", "${emotionType}, ${emotionWord} 선택")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = EmotionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = emotionList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val emotion = emotionList[position]
        val binding = (holder as ViewHolder).binding
        binding.run {
            emotionType.text = emotion.emotionType
            emotionWord.text = emotion.emotionWord
        }
    }
}