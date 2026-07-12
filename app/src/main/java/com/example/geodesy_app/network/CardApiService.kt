package com.example.geodesy_app.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CardApiService {
    @Multipart
    @POST("api/v1/card/create/")
    suspend fun createCard(
        @Header("Authorization") token: String, // Передаем "как есть", без Bearer
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("federal_subject") federalSubject: RequestBody,
        @Part("sign_height") signHeight: RequestBody,
        @Part("sign_height_above_ground_level") signHeightAbove: RequestBody,
        @Part("execute_date") executeDate: RequestBody,
        @Part("outdoor_sign") outdoorSign: RequestBody,
        @Part("type_of_sign") typeOfSign: RequestBody,
        @Part("monolith_one") monolithOne: RequestBody,
        @Part("monolith_two") monolithTwo: RequestBody,
        @Part("monolith_three_and_four") monolithThreeFour: RequestBody,
        @Part("trench") trench: RequestBody,
        @Part("identification_pillar") identificationPillar: RequestBody,
        @Part("ORP_one") orpOne: RequestBody,
        @Part("ORP_two") orpTwo: RequestBody,
        @Part("satellite_surveillance") satelliteSurveillance: RequestBody,
        @Part photos: List<MultipartBody.Part> // Список фотографий
    ): Response<Unit>
}