package com.example.geodesy_app.data

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    val email: String,
    val password: String
)
