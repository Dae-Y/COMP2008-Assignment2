package com.example.foodmanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.R
import com.example.foodmanager.ui.theme.FoodManagerTheme

class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues())  // Handle system bars
                ) {
                    // Main content for Add Activity
                    Box(
                        modifier = Modifier
                            .weight(1f)  // The content should take the remaining space
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AddActivityContent()
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
        }
    }
}

@Composable
fun AddActivityContent() {
    // TODO TEMP DATA
    var foodImage by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }
    var foodKcal by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.TopCenter
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "ADD NEW ITEM",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            // TODO: Temp Img placement
            Image(
                painter = painterResource(R.drawable.defaultfoodimg),
                contentDescription = "Food Image",
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(50.dp))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = foodName,
                onValueChange = {foodName = it},
                label = { Text("Food Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = portionSize,
                onValueChange = {portionSize = it},
                label = { Text("Portion Size") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = mealType,
                onValueChange = {mealType = it},
                label = { Text("Meal Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = foodKcal,
                onValueChange = {foodKcal = it},
                label = { Text("Food Kcal") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
//                contactDAO.insertContact(
//                    Contact(
//                        picture = bitmapToString(image),
//                        name = name,
//                        email = email,
//                        number = number
//                    )
//                )
//                state = 0
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }, modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) { Text(text = "Add contact into database") }
        }

    }
}

@Composable
@Preview
fun AddActivityPreview() {
    AddActivityContent()
}
