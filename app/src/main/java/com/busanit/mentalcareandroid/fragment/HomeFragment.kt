package com.busanit.mentalcareandroid.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.activity.MyEmotionDiaryActivity
import com.busanit.mentalcareandroid.activity.TitleActivity
import com.busanit.mentalcareandroid.activity.UpdateUserActivity
import com.busanit.mentalcareandroid.activity.WithdrawUserActivity
import com.busanit.mentalcareandroid.activity.WriteMyEmotionDiaryActivity
import com.busanit.mentalcareandroid.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navibar.run {
            inflateMenu(R.menu.menu)

        }

    }
    // EmotionDiary
    private fun writeEmotionDiary() {
        startActivity(Intent(activity, MyEmotionDiaryActivity::class.java))
    }
}
