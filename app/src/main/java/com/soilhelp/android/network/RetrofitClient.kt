package com.soilhelp.android.network

import com.soilhelp.android.data.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulator localhost alias. For a physical device, replace with your machine's
    // LAN IP (e.g. "http://192.168.1.42:8080/"), and once deployed, your Render URL
    // (e.g. "https://soilhelp-backend.onrender.com/").
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var apiService: ApiService? = null

    fun getApiService(tokenManager: TokenManager): ApiService {
        return apiService ?: buildRetrofit(tokenManager).also { apiService = it }
    }

    private fun buildRetrofit(tokenManager: TokenManager): ApiService {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = tokenManager.getToken()
            val requestBuilder = original.newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
