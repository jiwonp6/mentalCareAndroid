package com.busanit.mentalcareandroid.model

import java.time.LocalDate

data class McUser(
    val userId: String,
    val userPw: String,
    var userNickname: String,
    val userGender: Char,
    val userBirth: String,
    val userEmail: String,
    val userPhonenumber: String
)

