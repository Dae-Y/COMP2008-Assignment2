package com.example.foodmanager

import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodmanager.activities.MoreDetailActivity
import com.example.foodmanager.ui.theme.FoodManagerTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                val context = LocalContext.current
                val db = DatabaseProvider.getDatabase(this)
                initialLaunchCheck(db.foodDao())
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues())  // Handle system bars
                ) {
                    // Main content that fills the rest of the screen
                    Box(
                        modifier = Modifier
                            .weight(1f)  // The content should take the remaining space
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainActivityHome(foodDao = db.foodDao(), context = context)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()  // Exit the app
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainActivityHome(foodDao: FoodDao, context: Context) {
    var clickedStudent by remember { mutableIntStateOf(0) }
    var showDialogSetDailyKcal by remember { mutableStateOf(false) }

    val currentDateTime = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDateTime = currentDateTime.format(formatter)

    var setDailyKcal by remember { mutableDoubleStateOf(foodDao.getUserProfile().kcalDailyLimit) }
    val totalKcal by remember { mutableIntStateOf(foodDao.getDayTotalKcal(formattedDateTime)) }  // Total kcal from roomdatabase
    var remainingKcal by remember { mutableDoubleStateOf(0.0) }  // setDailyKcal - totalKcal

    val food by remember { mutableStateOf(foodDao.getFoodsByDate(formattedDateTime)) }

    var expanded by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        AnimatedVisibility(visible = expanded){
            Column{ // Wrap the CurvedText composables in a Column
                Spacer(modifier = Modifier.height(20.dp))
                CurvedTextWithElevation(
                    text = "Set Daily Kcal: $setDailyKcal",
                    onClick = { showDialogSetDailyKcal = true }
                )
                Spacer(modifier = Modifier.height(5.dp))
                CurvedTextWithElevation(text = "Total Kcal: $totalKcal", onClick = {  })
                Spacer(modifier = Modifier.height(5.dp))
                remainingKcal = setDailyKcal - totalKcal
                CurvedTextWithElevation(text = "Remaining Kcal: $remainingKcal", onClick = {  })
            }
        }


        Icon(
            imageVector = (if(expanded)Icons.Rounded.KeyboardArrowUp else Icons.Rounded.ArrowDropDown),
            contentDescription = "Expand/Collapse",
            tint = Color.Black,
            modifier = Modifier.fillMaxWidth()
                .clickable{ expanded = !expanded }
        )

        UpdateDailyKcalDialog(
            dailyKcal = setDailyKcal.toString(),
            onDailyKcalUpdate = { newKcal -> setDailyKcal = newKcal.toDouble() },
            showDialog = showDialogSetDailyKcal,
            setShowDialog = { showDialogSetDailyKcal = it },
            foodDao = foodDao,
            context = context
        )

        if (food.isEmpty()) {
            // Display an image when the list is empty
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Center both horizontally and vertically
            ) {
                Text(
                    text = "NOTHING IS \nHERE FOR TODAY",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2f,2f)
                        )
                    )
                )
            }
        } else{
        LazyColumn (modifier = Modifier.padding(16.dp)) {
            items(food) { foodItem ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable {
                            clickedStudent = foodItem.id
                        }){
                        FoodCard(foodItem, foodDao, context)
                }}
        }}
    }
}

@Composable
fun CurvedTextWithElevation(text: String, onClick: () -> Unit) {
    val isClickable = text.contains("Set Daily Kcal: ", ignoreCase = true)
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FoodCard(food: Food, foodDao: FoodDao, context: Context) {

    var isLoading by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            // Navigate to MoreDetailActivity with food details as extras
            val intent = Intent(context, MoreDetailActivity::class.java).apply {
                putExtra("foodID", food.id.toString())
                putExtra("foodName", food.name)
                putExtra("foodImageUrl", food.image) // Pass the image URL (or empty string if null)
                putExtra("mealType", food.mealType) // Pass the meal type
                putExtra("date", food.date) // Pass the date
            }
            context.startActivity(intent)
        }
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.align(Alignment.CenterVertically),
                contentAlignment=Alignment.Center){
                AsyncImage(
                    model = food.image,
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

            Spacer(modifier = Modifier.width(8.dp)) // Changed to width for horizontal spacing
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Food Name: ${food.name}",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Meal Type: ${food.mealType}",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Food portion: ${food.portion} g",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Food Kcal: ${food.kcal}",
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

@Composable
fun UpdateDailyKcalDialog(
    dailyKcal: String,
    onDailyKcalUpdate: (String) -> Unit,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    foodDao: FoodDao,
    context: Context
) {
    var updatedDailyKcal by remember { mutableStateOf(TextFieldValue(dailyKcal)) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = { Text("Update Daily Kcal intake") },
            text = {
                OutlinedTextField(
                    value = updatedDailyKcal,
                    onValueChange = { newValue ->
                                        val text = newValue.text
                                        if (text.isEmpty() || text.toDoubleOrNull() != null) {
                                            updatedDailyKcal = newValue
                                        } else {
                                            Toast.makeText(context, "Please input numbers only", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    },
                    label = { Text("Daily Calories") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    var tempDailyKcal = foodDao.getUserProfile()
                    tempDailyKcal = tempDailyKcal.copy(
                        deviceID = foodDao.getUserProfile().deviceID,
                        kcalDailyLimit = updatedDailyKcal.text.toDouble()
                    )
                    onDailyKcalUpdate(updatedDailyKcal.text)
                    foodDao.updateUserProfile(tempDailyKcal)
                    Toast.makeText(context, "Daily Kcal intake Updated", Toast.LENGTH_LONG).show()
                    setShowDialog(false)
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun initialLaunchCheck(foodDao: FoodDao)
{
    if(!foodDao.hasProfile())
    {
        val database = Firebase
        var id by remember { mutableIntStateOf(Random.nextInt()) }

        generateUniqueID(database){ uniqueID ->
            id = uniqueID
        }

        foodDao.newUserProfile(UserProfile(
            deviceID = id,
            kcalDailyLimit = 0.0
        ))

        val myRef = database.database.getReference(id.toString())
        val foodID = 1
        val foodImage = "temp"
        val foodData = mapOf("id" to foodID, "food_image" to foodImage)

        // Add the foodID and pictureID
        myRef.child(foodID.toString()).setValue(foodData)
    }
}

fun generateUniqueID(database: Firebase, callback: (Int) -> Unit){
    val id = Random.nextInt()
    database.database.getReference(id.toString()).get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()){
            // If the ID already exists, it will try again
            generateUniqueID(database, callback)
        } else {
            // ID is unique, return it
            callback(id)
        }
    }
}