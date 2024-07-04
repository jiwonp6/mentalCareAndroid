import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.model.Emotion

class EmotionRecyclerViewAdapter(
    private val emotionList: List<Emotion>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<EmotionRecyclerViewAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    interface OnItemClickListener {
        fun onItemClick(emotion: Emotion)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emotionType: TextView = view.findViewById(R.id.emotionType)
        val emotionWord: TextView = view.findViewById(R.id.emotionWord)
        val container: LinearLayout = view.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emotion_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val emotion = emotionList[position]
        holder.emotionType.text = emotion.emotionType
        holder.emotionWord.text = emotion.emotionWord

        // 아이템 배경색 변경
        if (selectedPosition == position) {
            holder.container.setBackgroundColor(Color.parseColor("#81BF80")) // 선택된 색
        } else {
            holder.container.setBackgroundColor(Color.parseColor("#FFFFFF")) // 원래 색
        }

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION // 선택 해제
            } else {
                selectedPosition = position // 선택된 아이템 업데이트
                itemClickListener.onItemClick(emotion)
            }
            notifyDataSetChanged() // 어댑터에 데이터 변경 알림
        }
    }

    override fun getItemCount(): Int {
        return emotionList.size
    }

    // 선택된 감정 ID에 해당하는 포지션을 설정하는 메서드
    fun setSelectedEmotionId(emotionId: Long) {
        selectedPosition = emotionList.indexOfFirst { it.emotionId == emotionId }
        notifyDataSetChanged() // 선택된 포지션을 반영하기 위해 어댑터에 데이터 변경 알림
    }
}
