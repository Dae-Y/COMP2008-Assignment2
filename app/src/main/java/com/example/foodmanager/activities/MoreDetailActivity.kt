package com.example.foodmanager.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.ui.theme.FoodManagerTheme
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.foodmanager.NutritionViewModel
import com.example.foodmanager.R
import java.net.URL

class MoreDetailActivity : ComponentActivity() {
    private val viewModel: NutritionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodName = intent.getStringExtra("foodName")

        setContent {
            FoodManagerTheme {
                val context = LocalContext.current
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        MoreDetailScreen(foodName ?: "", viewModel)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        NavBar(context)
                    }
                }
            }
        }

        foodName?.let {
            viewModel.fetchNutritionData(it)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@MoreDetailActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}

@Composable
fun MoreDetailScreen(foodName: String, viewModel: NutritionViewModel) {
    val nutritionData by viewModel.nutritionData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        errorMessage?.let {
            if (it.isNotEmpty()) {
                Text(text = it)
            }
        }
        nutritionData?.let { data ->
            Text(text = "Food: $foodName")
            Text(text = "Calories: ${data.nf_calories} kcal")
            Text(text = "Fat: ${data.nf_total_fat} g")
            Text(text = "Saturated Fat: ${data.nf_saturated_fat} g")
            Text(text = "Cholesterol: ${data.nf_cholesterol} mg")
            Text(text = "Sodium: ${data.nf_sodium} mg")
            Text(text = "Carbohydrates: ${data.nf_total_carbohydrate} g")
            Text(text = "Dietary Fiber: ${data.nf_dietary_fiber} g")
            Text(text = "Sugars: ${data.nf_sugars} g")
            Text(text = "Protein: ${data.nf_protein} g")
            Text(text = "Potassium: ${data.nf_potassium} mg")
            Text(text = "Phosphorus: ${data.nf_p} mg")
        }
    }
}