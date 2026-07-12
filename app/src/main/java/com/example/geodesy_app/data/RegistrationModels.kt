package com.example.geodesy_app.data

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("second_name") val secondName: String,
    @SerializedName("third_name") val thirdName: String,
    val sex: String,
    val email: String,
    val password: String
)

data class RegistrationResponse(
    @SerializedName("tfa_token") val tfaToken: String,
    @SerializedName("expiration_confirm_code_in_seconds") val expirationSeconds: Int
)

data class VerificationRequest(
    @SerializedName("tfa_token") val tfaToken: String,
    @SerializedName("confirm_code") val confirmCode: String
)

data class VerificationResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expiration_access_token_in_seconds") val expirationSeconds: Int
)
