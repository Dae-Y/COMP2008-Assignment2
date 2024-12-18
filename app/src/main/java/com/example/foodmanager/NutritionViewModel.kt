package com.example.foodmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
// MutableStateFlow is a state-holder that can emit and hold values over time
// primarily used for managing and sharing state in a reactive, lifecycle-aware way
// Lec 11 Kotlin Coroutines

class NutritionViewModel : ViewModel() {
    private val _nutritionData = MutableStateFlow<NutritionData?>(null)
    val nutritionData: StateFlow<NutritionData?> get() = _nutritionData

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val apiService = RetrofitInstance.api

    fun fetchNutritionData(foodName: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val requestBody = mapOf("query" to foodName)
                val response = withContext(Dispatchers.IO) {

                    apiService.getNutritionData("aaee087b",
                        "aa58793d24f5662a188b4ba5d672e39e", requestBody)
                }
                _nutritionData.value = response.foods.firstOrNull()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch data: ${e.message}"
                _nutritionData.value = null
            } finally {
                _loading.value = false
            }
        }
    }



}



