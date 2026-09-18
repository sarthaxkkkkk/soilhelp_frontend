package com.soilhelp.android.data

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class ReadingResponse(
    val temperature: Double?,
    val humidity: Double?,
    val soilMoisture: Double?,
    val rainfall: Double?,
    val recordedAt: String?
)
