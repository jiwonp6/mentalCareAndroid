package com.busanit.mentalcareandroid.service

import com.busanit.mentalcareandroid.model.Emotion
import com.busanit.mentalcareandroid.model.EmotionDiary
import com.busanit.mentalcareandroid.model.McUser
import com.busanit.mentalcareandroid.model.McUserLogin
import com.busanit.mentalcareandroid.model.McUserLoginSuccess
import com.busanit.mentalcareandroid.model.McUserUpdate
import com.busanit.mentalcareandroid.model.SleepData
import com.busanit.mentalcareandroid.model.StressData
import com.busanit.mentalcareandroid.model.Test
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.Call
import retrofit2.http.Query

interface ApiService {
    // 네트워크 테스트용 API
    @GET("/test")
    fun test(): retrofit2.Call<Test>

    // 스프링 Security로 보호되어있는 자원 테스트용 API
    // JWT 토큰을 헤더에 담아 요청
    @GET("/protect")
    fun protect(@Header("Authorization") token: String): Call<Test>

    /* 회원 기본 기능 */
    // 회원 가입 : 본문에 User 정보를 담아 User 정보 리턴
    @POST("/mcUser/createUser")
    fun createUser(@Body user: McUser): Call<McUser>

    // 로그인 : 본문에 User 정보를 담아 JWT 토큰 리턴
    @POST("/mcUser/authLogin")
    fun authLogin(@Body user: McUserLogin): Call<McUserLoginSuccess>

    // 회원 정보 수정
    @PUT("/mcUser/updateUser")
    fun updateUser(@Header("Authorization") token: String, @Query("userId") id: String, @Body user: McUserUpdate): Call<McUser>

    // id로 회원 정보 가져오기
    @GET("/mcUser/getByUserId")
    fun getByUserId(@Query("userId") id: String): Call<McUser>

    // 회원 탈퇴
    @PUT("/mcUser/withdrawUser")
    fun withdrawUser(@Query("userId") id: String): Call<McUser>

    /* 회원 데이터 */
    // 오늘 회원 데이터
    @GET("/mcUserData/getTodayStressData")
    fun getTodayStressData(@Query("userId") id: String): Call<StressData>
    @GET("/mcUserData/getTodaySleepData")
    fun getTodaySleepData(@Query("userId") id: String): Call<SleepData>

    // 특정날짜 회원 데이터
    @GET("/mcUserData/getSelectedDateStressData")
    fun getSelectedDateStressData(@Query("userId") id: String, @Query("stdDate") stdDate: String): Call<StressData>
    @GET("/mcUserData/getSelectedDateSleepData")
    fun getSelectedDateSleepData(@Query("userId") id: String, @Query("sldDate") sldDate: String): Call<SleepData>


    /* 감정 일지 */
    // 오늘 감정 일지
    @GET("/emotionDiary/getTodayEmotionDiary")
    fun getTodayEmotionDiary(@Query("userId") id: String): Call<EmotionDiary>

    // 특정 날짜 감정 일지
    @GET("/emotionDiary/getSelectedDateEmotionDiary")
    fun getSelectedDateEmotionDiary(@Query("userId") id: String, @Query("edDate") edDate: String): Call<EmotionDiary>

    // 감정일지 작성
    @POST("/emotionDiary/writeEmotionDiary")
    fun writeEmotionDiary(@Query("userId") id: String, @Body emotionDiary: EmotionDiary): Call<EmotionDiary>

    // 감정일지 수정
    @PUT("/emotionDiary/updateEmotionDiary")
    fun updateEmotionDiary(@Query("userId") id: String, @Body emotionDiary: EmotionDiary): Call<EmotionDiary>

    // 감정일지 삭제
    @PUT("/emotionDiary/deleteEmotionDiary")
    fun deleteEmotionDiary(@Query("userId") id: String, @Query("edId") edId: Long): Call<Integer>

    // 감정 태그 list
    @GET("/emotion/AllEmotions")
    fun getEmotionList(): Call<List<Emotion>>

    // 게시글 리스트를 가져오는 API (보안, 토큰 필요)
    // @GET("/articles")
    // fun getArticles(@Header("Authorization") token: String): Call<List<Article>>
}