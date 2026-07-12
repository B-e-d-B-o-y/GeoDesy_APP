package com.example.geodesy_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geodesy_app.data.repository.CardData
import com.example.geodesy_app.data.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

import androidx.lifecycle.ViewModelProvider

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    class Factory(private val repository: CardRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CardViewModel(repository) as T
        }
    }

    private val _uiState = MutableStateFlow<String?>(null)
    val uiState = _uiState.asStateFlow()

    fun createCard(token: String, data: CardData, files: List<File>) {
        // Валидация
        if (files.size < 2) {
            _uiState.value = "Ошибка: Выберите минимум 2 фото"
            return
        }

        viewModelScope.launch {
            val result = repository.sendCard(token, data, files)
            result.onSuccess {
                _uiState.value = "Карточка успешно создана!"
            }.onFailure {
                _uiState.value = "Ошибка: ${it.message}"
            }
        }
    }
}
