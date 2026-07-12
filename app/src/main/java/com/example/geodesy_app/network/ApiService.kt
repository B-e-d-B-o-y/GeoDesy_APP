package com.example.geodesy_app.network

import com.example.geodesy_app.data.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("api/v1/auth/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/registration/")
    suspend fun registerStep1(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @PUT("api/v1/registration/")
    suspend fun registerStep2(@Body request: VerificationRequest): Response<VerificationResponse>

    @POST("api/v1/auth/forgotten/password/")
    suspend fun forgotPasswordStep1(@Body request: ForgotPasswordRequest): Response<RegistrationResponse>

    @PUT("api/v1/auth/forgotten/password/")
    suspend fun forgotPasswordStep2(@Body request: VerificationRequest): Response<Unit>
}