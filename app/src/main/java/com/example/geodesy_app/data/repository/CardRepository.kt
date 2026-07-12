package com.example.geodesy_app.data.repository

import com.example.geodesy_app.network.CardApiService
import com.example.geodesy_app.utils.toTextRequestBody
import com.example.geodesy_app.utils.toJsonRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CardRepository(private val api: CardApiService) {

    suspend fun sendCard(token: String, data: CardData, files: List<File>): Result<Unit> {
        return try {
            // 1. Конвертируем файлы в Part
            val photoParts = files.map { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photos", file.name, requestFile)
            }

            // 2. Делаем запрос к API
            val response = api.createCard(
                token = token,
                latitude = data.latitude.toString().toTextRequestBody(),
                longitude = data.longitude.toString().toTextRequestBody(),
                federalSubject = data.federalSubject.toTextRequestBody(),
                signHeight = data.signHeight.toString().toTextRequestBody(),
                signHeightAbove = data.signHeightAbove.toString().toTextRequestBody(),
                executeDate = data.executeDate.toTextRequestBody(),
                outdoorSign = data.outdoorSignJson.toJsonRequestBody(),
                typeOfSign = data.typeOfSignJson.toJsonRequestBody(),
                monolithOne = data.monolithOne.toTextRequestBody(),
                monolithTwo = data.monolithTwo.toTextRequestBody(),
                monolithThreeFour = data.monolithThreeFour.toTextRequestBody(),
                trench = data.trench.toTextRequestBody(),
                identificationPillar = data.identificationPillar.toTextRequestBody(),
                orpOne = data.orpOne.toTextRequestBody(),
                orpTwo = data.orpTwo.toTextRequestBody(),
                satelliteSurveillance = data.satelliteSurveillance.toTextRequestBody(),
                photos = photoParts
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Нет деталей ошибки"
                android.util.Log.e("API_CARD_ERROR", "Код: ${response.code()}, Тело: $errorBody")
                Result.failure(Exception("Ошибка: ${response.code()}. Детали: $errorBody"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
