package com.busanit.mentalcareandroid.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.busanit.mentalcareandroid.databinding.ActivityCommunityMainBinding
import com.busanit.mentalcareandroid.databinding.ActivityMainBinding
import com.busanit.mentalcareandroid.fragment.CheeringFragment
import com.busanit.mentalcareandroid.fragment.CommonFragment
import com.busanit.mentalcareandroid.fragment.MentalFragment
import com.google.android.material.tabs.TabLayoutMediator

class CommunityMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommunityMainBinding

    val fragmentList = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 글쓰기 화면으로 넘어가기
        val intent = Intent(this, WriteActivity::class.java)
        binding.writeButton.setOnClickListener { startActivity(intent) }



        // 사용할 프래그먼트
        fragmentList.add(CommonFragment())
        fragmentList.add(MentalFragment())
        fragmentList.add(CheeringFragment())

        binding.pager2.adapter = TabAdapter(this, fragmentList)

        // 탭 이름 설정
        val tabName = arrayOf("일반 고민", "정신건강", "응원")

        binding.run {
            TabLayoutMediator(tabLayout, pager2) { tab, position ->
                tab.text = tabName[position]        // 탭 이름 설정
            }.attach()
        }


    }

    class TabAdapter(fragmentActivity: FragmentActivity, val fragmentList: MutableList<Fragment>) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }

}