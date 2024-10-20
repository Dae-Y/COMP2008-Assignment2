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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.database.database

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
    val profileID = foodDao.getUserProfile().deviceID

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
            SummaryActivityContent(foods = foods, profileID = profileID) // pass the food list to the content
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
fun SummaryActivityContent(foods: List<Food>, profileID: Int) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp) // Padding around the LazyColumn
    ) {
        items(foods) { food ->
            SingleFoodCard(food = food, profileID = profileID) // Call the Card Composable for each item
        }
    }
}

@Composable
fun SingleFoodCard(food: Food, profileID: Int) {
    val context = LocalContext.current
    val foodRef = Firebase.database.getReference(profileID.toString()).child(food.id.toString())
    var image by remember { mutableStateOf(R.drawable.defaultfoodimg.toString()) }
    var isLoading by remember { mutableStateOf(false) }

    foodRef.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val foodData = snapshot.value as Map<String, Any>
            image = foodData["food_image"] as String
        }
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        onClick = {
            // Navigate to MoreDetailActivity with food details as extras
            val intent = Intent(context, MoreDetailActivity::class.java).apply {
                putExtra("foodName", food.name)
                putExtra("foodImageUrl", image) // Pass the image URL (or empty string if null)
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
            Box(modifier = Modifier.align(Alignment.Center),
                contentAlignment=Alignment.Center){
                AsyncImage(
                    model = image,
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
                SummaryActivityContent(foods = sampleFoods, profileID = 123) // Pass the sample food list
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