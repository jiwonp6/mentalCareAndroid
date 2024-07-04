package com.busanit.mentalcareandroid.consultation

import java.sql.Blob

data class Consultation(
    val reservationId: Long,
    val consultationId: Long?,
    val consultationDetails: String,
    val myChange: String,
    val picture: Blob?
)