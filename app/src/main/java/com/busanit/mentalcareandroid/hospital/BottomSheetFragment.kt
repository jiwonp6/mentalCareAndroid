package com.busanit.mentalcareandroid.hospital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.busanit.mentalcareandroid.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// 2. 프래그먼트 작성 : BottomSheetDialogFragment 상속
class BottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 레이아웃 연결
        return inflater.inflate(R.layout.local_code, container, false)
    }
}