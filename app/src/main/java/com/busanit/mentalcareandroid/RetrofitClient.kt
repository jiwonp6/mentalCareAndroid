package com.busanit.mentalcareandroid

import com.busanit.mentalcareandroid.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 안드로이드에서 일반 HTTP 프로토콜 요청은 보안상 금지되어 있음.
    // https : http 프로토콜에 보안이 추가된 프로토콜
    // 안드로이드 에뮬레이터에서 localhost는 에뮬레이터 자기자신을 가리킴
    // 개발 서버의 localhost 접속시 10.0.2.2 IP를 사용
    // 공식문서 : https://developer.android.com/studio/run/emulator-networking?hl=ko

    private val BASE_URL = "http://10.0.2.2:8080"    // 개인 PC의 localhost(127.0.0.1) 루프백 주소
    
    // private val BASE_URL = "http://10.100.203.114:8080"   // 개인 PC의 IP주소

    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}