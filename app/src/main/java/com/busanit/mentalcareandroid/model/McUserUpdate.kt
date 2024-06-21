package com.busanit.mentalcareandroid.model

data class McUserUpdate(
    val userPw: String,
    val userPwNew: String,
    val userNickname: String,
    val userGender: Char,
    val userBirth: String,
    val userEmail: String,
    val userPhonenumber: String
)

