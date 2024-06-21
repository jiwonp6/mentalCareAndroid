package com.busanit.mentalcareandroid.model

import java.time.LocalDate


data class McUser(
    val userId: String,
    val userPw: String,
    val userNickname: String,
    val userGender: Char,
    val userBirth: String,
    val userEmail: String,
    val userPhonenumber: String
)

