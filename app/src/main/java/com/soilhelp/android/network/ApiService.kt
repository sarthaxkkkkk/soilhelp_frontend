package com.soilhelp.android.network

import com.soilhelp.android.data.AuthRequest
import com.soilhelp.android.data.AuthResponse
import com.soilhelp.android.data.ReadingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("api/readings/latest")
    suspend fun getLatestReading(): Response<ReadingResponse>

    @GET("api/readings/history")
    suspend fun getHistory(@Query("limit") limit: Int = 50): Response<List<ReadingResponse>>
}
