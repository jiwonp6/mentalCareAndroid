package com.busanit.mentalcareandroid.model

import java.util.Date

data class EmotionDiary (
    var userId: String,
    var emotionId: Long,
    var emotionType: String,
    var emotionWord: String,
    var edReason: String,
    var edDate: String
)