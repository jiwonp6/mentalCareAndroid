package com.busanit.mentalcareandroid.hospital

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.mentalcareandroid.databinding.HospitalItemBinding
import com.busanit.mentalcareandroid.reservation.ReservationActivity

class HospitalAdapter(var hospitalList: List<Hospital>) :
    RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    inner class HospitalViewHolder(val binding: HospitalItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // 항목 뷰에 데이터를 바인딩
        fun bind(hospital: Hospital) {
            binding.hospitalName.text = hospital.hospitalName
            binding.hospitalLocation.text = hospital.hospitalLocation
            binding.hospitalCall.text = hospital.hospitalCall
            binding.hospitalWebsite.text = hospital.hospitalWebsite
            // 항목을 클릭했을 때 Reservation 액티비티를 시작하고 데이터 전달
            binding.root.setOnClickListener {
                // 컨테스트 추출
                val context = it.context
                val intent = Intent(context, ReservationActivity::class.java)
                intent.putExtra("hospitalId", hospital.hospitalId)
                context.startActivity(intent)
            }
        }
    }

    // 6. 어댑터의 메서드 구현
    // 6-1. onCreateViewHolder : 뷰 홀더를 초기화
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val binding = HospitalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HospitalViewHolder(binding)
    }

    // 6-2. getItemCount : 데이터의 개수
    override fun getItemCount(): Int = hospitalList.size

    // 6-3. onBindViewHolder : 데이터와 뷰홀더 바인딩
    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        holder.bind(hospitalList[position])
        /*holder.binding.run {
            hospitalName.text = hospitalList[position].hospitalName
            hospitalLocation.text = hospitalList[position].hospitalLocation
            hospitalCall.text = hospitalList[position].hospitalCall
            hospitalWebsite.text = hospitalList[position].hospitalWebsite

        }*/
    }



    fun updateList(newList: List<Hospital>) {
        hospitalList = newList
        notifyDataSetChanged()
    }

    fun abc(location: String ): List<Hospital> {
        return hospitalList.filter { hospital: Hospital -> hospital.hospitalLocation.contains(location) }
    }
}