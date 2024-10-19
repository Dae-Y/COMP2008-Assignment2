package com.example.foodmanager.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodmanager.DatabaseProvider
import com.example.foodmanager.Food
import com.example.foodmanager.NavBar
import com.example.foodmanager.R
import com.example.foodmanager.ui.theme.FoodManagerTheme
import android.content.Intent

class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                SummaryActScreen() // Set the screen content to the UI
            }
        }
    }
}

@Composable
fun SummaryActScreen() {
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val foodDao = db.foodDao()
    // Fetch food list from the database
    val foods = remember { foodDao.getAllFoods() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())  // Handle system bars
    ) {
        // Main content for Summary Activity
        Box(
            modifier = Modifier
                .weight(1f)  // The content should take the remaining space
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SummaryActivityContent(foods = foods) // pass the food list to the content
        }

        // Navigation bar with fixed height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)  // Set a fixed height for the navigation bar
        ) {
            NavBar(context = context)
        }
    }


}

@Composable
fun SummaryActivityContent(foods: List<Food>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp) // Padding around the LazyColumn
    ) {
        items(foods) { food ->
            SingleFoodCard(food = food) // Call the Card Composable for each item
        }
    }
}

@Composable
fun SingleFoodCard(food: Food) {
    val context = LocalContext.current
    val bitmap = if (food.image != null) {
        BitmapFactory.decodeFile(food.image) // Decode image from the file path
    } else { // Use default image when food.image is null
        BitmapFactory.decodeResource(context.resources, R.drawable.empty_plate)
    } // empty plate reproduced from https://freepng.pictures/download/plate-17/

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        onClick = {
            // Navigate to MoreDetailActivity with food details as extras
            val intent = Intent(context, MoreDetailActivity::class.java).apply {
                putExtra("foodName", food.name)
                putExtra("foodImageUrl", food.image ?: "") // Pass the image URL (or empty string if null)
                putExtra("mealType", food.mealType) // Pass the meal type
                putExtra("date", food.date) // Pass the date
            }
            context.startActivity(intent)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp) // Size and padding for the image box
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .fillMaxSize(), // Ensure the image fills the box
                    contentScale = ContentScale.Fit // Fit the image to show fully without cropping
                )
            }
        }
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = "Food Name: ${food.name}")
            Text(text = "Meal Type: ${food.mealType}")
            Text(text = "Portion: ${food.portion}")
            Text(text = "Calories: ${food.kcal} kcal")
            Text(text = "Date: ${food.date}")
            }
        }
    }
}


@Composable
@Preview(showBackground = true, heightDp = 800) // Adjust height to simulate a realistic device screen
fun SummaryActScreenPreview() {
    val sampleFoods = listOf(
        Food(
            name = "Apple",
            portion = 150,
            kcal = 95,
            mealType = "Snack",
            date = "2024-10-04",
            image = null // No image for preview
        ),
        Food(
            name = "Pasta",
            portion = 300,
            kcal = 400,
            mealType = "Lunch",
            date = "2024-10-04",
            image = null // No image for preview
        )
    )

    FoodManagerTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()) // Handle system bars
        ) {
            // Main content for Summary Activity
            Box(
                modifier = Modifier
                    .weight(1f) // The content should take the remaining space
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SummaryActivityContent(foods = sampleFoods) // Pass the sample food list
            }

            // Navigation bar with fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp) // Set a fixed height for the navigation bar
            ) {
                NavBar(context = LocalContext.current) // Use current context for NavBar
            }
        }
    }
}