package com.busanit.mentalcareandroid.model

data class McUserLoginSuccess(
    val userId: String,
    val userPw: String,
    val userNickname: String,
    val userGender: Char,
    val userBirth: String,
    val userEmail: String,
    val userPhonenumber: String,
    val userSecession: Boolean,
    val jwt: String
)
