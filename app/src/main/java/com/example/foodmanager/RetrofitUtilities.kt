package com.example.foodmanager

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// NutritionData class to capture API JSON response
data class NutritionData(
    val food_name: String,
    val serving_qty: Int,
    val serving_unit: String,
    val serving_weight_grams: Double,
    val nf_calories: Double,
    val nf_total_fat: Double,
    val nf_saturated_fat: Double,
    val nf_cholesterol: Double,
    val nf_sodium: Double,
    val nf_total_carbohydrate: Double,
    val nf_dietary_fiber: Double,
    val nf_sugars: Double,
    val nf_protein: Double,
    val nf_potassium: Double,
    val nf_p: Double
)



data class NutritionResponse(
    val foods: List<NutritionData> // Use NutritionData instead of NutritionInfo
)

interface RemoteAPICalls {
    @POST("/v2/natural/nutrients")
    suspend fun getNutritionData(
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String,
        @Body requestBody: Map<String, String>
    ): NutritionResponse
}


object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://trackapi.nutritionix.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RemoteAPICalls by lazy {
        retrofit.create(RemoteAPICalls::class.java)
    }
}
// https://docx.syndigo.com/developers/docs/natural-language-for-nutrients


