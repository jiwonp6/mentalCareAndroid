package com.busanit.mentalcareandroid.model

import java.util.Date

data class EmotionDiary (
    val edId: Long,
    val userId: String,
    val emotionId: Int,
    val emotionType: String,
    val emotionWord: String,
    val edReason: String,
    val edDate: String
)