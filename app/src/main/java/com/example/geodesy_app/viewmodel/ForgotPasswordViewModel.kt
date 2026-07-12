package com.example.geodesy_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geodesy_app.data.*
import com.example.geodesy_app.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<String?>(null)
    val uiState = _uiState.asStateFlow()

    private val _tfaToken = MutableStateFlow<String?>(null)
    val tfaToken = _tfaToken.asStateFlow()

    private val _isStep2 = MutableStateFlow(false)
    val isStep2 = _isStep2.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    fun forgotPasswordStep1(email: String, newPass: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.forgotPasswordStep1(ForgotPasswordRequest(email, newPass))
                if (response.isSuccessful) {
                    _tfaToken.value = response.body()?.tfaToken
                    _isStep2.value = true
                    _uiState.value = "Код подтверждения отправлен на почту"
                } else {
                    _uiState.value = "Ошибка: ${response.code()}"
                }
            } catch (e: Exception) {
                _uiState.value = "Ошибка сети: ${e.message}"
            }
        }
    }

    fun verifyStep2(confirmCode: String) {
        val token = _tfaToken.value ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.forgotPasswordStep2(VerificationRequest(token, confirmCode))
                if (response.isSuccessful) {
                    _success.value = true
                    _uiState.value = "Пароль успешно изменен!"
                } else {
                    _uiState.value = "Неверный код или ошибка сервера"
                }
            } catch (e: Exception) {
                _uiState.value = "Ошибка сети: ${e.message}"
            }
        }
    }
}
