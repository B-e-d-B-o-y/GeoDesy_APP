package com.example.geodesy_app.data

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val access: String,

    @SerializedName("refresh_token")
    val refresh: String
)