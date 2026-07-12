package com.example.geodesy_app.data.repository

data class CardData(
    val latitude: Double,
    val longitude: Double,
    val federalSubject: String,
    val signHeight: Double,
    val signHeightAbove: Double,
    val executeDate: String,
    val outdoorSignJson: String,
    val typeOfSignJson: String,
    val monolithOne: String,
    val monolithTwo: String,
    val monolithThreeFour: String,
    val trench: String,
    val identificationPillar: String,
    val orpOne: String,
    val orpTwo: String,
    val satelliteSurveillance: String
)
