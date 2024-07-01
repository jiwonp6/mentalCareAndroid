package com.busanit.mentalcareandroid.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.activity.TitleActivity
import com.busanit.mentalcareandroid.activity.UpdateUserActivity
import com.busanit.mentalcareandroid.activity.WithdrawUserActivity
import com.busanit.mentalcareandroid.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인 시 저장된 사용자 이름을 가져옴
        val sharedPreferences = activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        val userNickname = sharedPreferences?.getString("userNickname", "")

        setOf(binding.userNickname.text, userNickname)
    }




}