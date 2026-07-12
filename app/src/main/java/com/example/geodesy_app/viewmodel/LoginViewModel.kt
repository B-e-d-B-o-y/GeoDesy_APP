package com.example.geodesy_app.viewmodel

import com.example.geodesy_app.data.LoginRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geodesy_app.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, pass))
                if (response.isSuccessful) {
                    onResult(true, response.body()?.access)
                } else {
                    onResult(false, "Код ошибки: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
