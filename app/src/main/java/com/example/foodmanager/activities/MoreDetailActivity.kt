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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.foodmanager.NutritionViewModel
import com.example.foodmanager.R

class MoreDetailActivity : ComponentActivity() {
    private val viewModel: NutritionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodIMG = intent.getStringExtra("foodIMG")
        val foodName = intent.getStringExtra("foodName")
        val portionSize = intent.getIntExtra("portionSize", 1) // Default to 1 if not provided

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
                        MoreDetailScreen(foodIMG ?: "", foodName ?: "", portionSize, viewModel)
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
fun MoreDetailScreen(foodIMG: String, foodName: String, portionSize: Int, viewModel: NutritionViewModel) {
    val nutritionData by viewModel.nutritionData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                errorMessage?.let {
                    if (it.isNotEmpty()) {
                        Text(text = it, color = Color.Red)
                    }
                }
                nutritionData?.let { data ->
                    // Multiply values by portionSize
                    val servingQty = data.serving_qty * portionSize
                    val servingUnit = data.serving_unit
                    val servingWeight = data.serving_weight_grams * portionSize
                    val calories = data.nf_calories * portionSize
                    val totalFat = data.nf_total_fat * portionSize
                    val saturatedFat = data.nf_saturated_fat * portionSize
                    val cholesterol = data.nf_cholesterol * portionSize
                    val sodium = data.nf_sodium * portionSize
                    val carbohydrate = data.nf_total_carbohydrate * portionSize
                    val dietaryFiber = data.nf_dietary_fiber * portionSize
                    val sugars = data.nf_sugars * portionSize
                    val protein = data.nf_protein * portionSize
                    val potassium = data.nf_potassium * portionSize
                    val phosphorus = data.nf_p * portionSize

                    // Display the food name and calculated values
                    Text(
                        text = "Food: $foodName",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textDecoration = TextDecoration.Underline
                    )

                    Box(modifier = Modifier.align(Alignment.CenterHorizontally),
                        contentAlignment=Alignment.Center){
                        AsyncImage(
                            model = foodIMG,
                            error = painterResource(R.drawable.defaultfoodimg),
                            contentScale = ContentScale.Fit,
                            contentDescription = "Contact Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            onLoading = {isLoading=true},
                            onSuccess = {isLoading=false},
                            onError = { error ->
                                isLoading = false
                            }
                        )
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }


                    Text(text = "Serving Size: $servingQty $servingUnit")
                    Text(text = "Weight: $servingWeight grams")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nutrition facts section
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Nutrition Facts",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            NutritionRow(label = "Calories", value = String.format("%.3f", calories) + " kcal")
                            NutritionRow(label = "Total Fat", value = String.format("%.3f", totalFat) + " g")
                            NutritionRow(label = "Saturated Fat", value = String.format("%.3f", saturatedFat) + " g")
                            NutritionRow(label = "Cholesterol", value = String.format("%.3f", cholesterol) + " mg")
                            NutritionRow(label = "Sodium", value = String.format("%.3f", sodium) + " mg")
                            NutritionRow(label = "Carbohydrates", value = String.format("%.3f", carbohydrate) + " g")
                            NutritionRow(label = "Dietary Fiber", value = String.format("%.3f", dietaryFiber) + " g")
                            NutritionRow(label = "Sugars", value = String.format("%.3f", sugars) + " g")
                            NutritionRow(label = "Protein", value = String.format("%.3f", protein) + " g")
                            NutritionRow(label = "Potassium", value = String.format("%.3f", potassium) + " mg")
                            NutritionRow(label = "Phosphorus", value = String.format("%.3f", phosphorus) + " mg")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Normal)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}
