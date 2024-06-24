package com.busanit.mentalcareandroid.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.busanit.mentalcareandroid.R
import com.busanit.mentalcareandroid.activity.LoginActivity
import com.busanit.mentalcareandroid.activity.TitleActivity
import com.busanit.mentalcareandroid.activity.UpdateUserActivity
import com.busanit.mentalcareandroid.activity.WithdrawUserActivity
import com.busanit.mentalcareandroid.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)

        // 로그인 시 저장된 사용자 이름을 가져옴
        val userNickname = sharedPreferences?.getString("userNickname", "")

        binding.textViewHello.text = "안녕하세요, ${userNickname}님"

        // 로그아웃 버튼 클릭 -> 로그아웃
        binding.buttonLogout.setOnClickListener { logout() }

        // 회원 정보 수정 버튼 클릭 -> 회원정보수정 페이지
        binding.buttonUpdateUser.setOnClickListener { updateUser() }

        // 회원 탈퇴 버튼 클릭 -> 회원 확인
        binding.buttonWithDrawUser.setOnClickListener { withdrawUser() }
    }

    // 회원 탈퇴 함수
    private fun withdrawUser() {
        startActivity(Intent(activity, WithdrawUserActivity::class.java))
    }

    // 로그아웃 함수
    private fun logout() {
        // 1. SharedPreferences 에서 토큰, 사용자 정보 삭제
        val sharedPreferences =
            activity?.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        sharedPreferences?.edit()
            ?.remove("token")
            ?.remove("useId")
            ?.remove("userNickname")
            ?.apply()

        // 2. TitleActivity 로 돌려 보내고 현재 화면 종료
        startActivity(Intent(activity, TitleActivity::class.java))
        activity?.finish()
    }

    // 회원 정보 수정 함수
    private fun updateUser() {
        startActivity(Intent(activity, UpdateUserActivity::class.java))
    }
}