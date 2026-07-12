package com.example.geodesy_app.data

import com.google.gson.annotations.SerializedName

data class StatusField(
    val value: String,
    val recommendation: String = "",
    val comment: String = ""
)

data class TypeOfSignField(
    val value: String,
    val properties: Map<String, String>? = null,
    val recommendation: String = "",
    val comment: String = ""
)
