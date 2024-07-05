import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.busanit.mentalcareandroid.reservation.LocalDateAdapter
import com.busanit.mentalcareandroid.reservation.LocalTimeAdapter
import com.busanit.mentalcareandroid.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // 개인 PC의 localhost(127.0.0.1) 루프백 주소

    val gson: Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()) // LocalDate 어댑터 등록
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter()) // LocalTime 어댑터 등록
        .create()

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    // Authorization 인터셉터 클래스
    class AuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = sharedPreferences.getString("access_token", null)
            val requestBuilder = chain.request().newBuilder()

            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }
}
