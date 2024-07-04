package com.busanit.mentalcareandroid.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.busanit.mentalcareandroid.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragmentReservationNot : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 레이아웃 연결
        return inflater.inflate(R.layout.reservation_not, container, false)
    }
}