package com.example.geodesy_app.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// Превращаем строку в RequestBody (для обычных полей)
fun String.toTextRequestBody(): RequestBody =
    this.toRequestBody("text/plain".toMediaTypeOrNull())

// Превращаем JSON-строку в RequestBody (для полей типа type_of_sign)
fun String.toJsonRequestBody(): RequestBody =
    this.toRequestBody("application/json".toMediaTypeOrNull())

fun Uri.toFile(context: Context): File? {
    val contentResolver = context.contentResolver
    
    // Получаем оригинальное имя файла с расширением
    var fileName = "temp_file_${System.currentTimeMillis()}.jpg" 
    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }

    val tempFile = File(context.cacheDir, fileName)
    return try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        null
    }
}
