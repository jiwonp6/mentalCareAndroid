package com.busanit.mentalcareandroid.model

import java.util.Date

data class MyEmotion (
    val myEmotionId: Long,
    val userId: String,
    val emotionId: Int,
    val myEmotionReason: String,
    val myEmotionDate: Date
)