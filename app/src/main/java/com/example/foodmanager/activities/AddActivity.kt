package com.example.foodmanager.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.foodmanager.DatabaseProvider
import com.example.foodmanager.Food
import com.example.foodmanager.FoodDao
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.NutritionViewModel
import com.example.foodmanager.R
import com.example.foodmanager.ui.theme.FoodManagerTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddActivity : ComponentActivity() {
    private val viewModel: NutritionViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodManagerTheme {
                val context = LocalContext.current
                val db = DatabaseProvider.getDatabase(this)
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
                        AddActivityContent(foodDao = db.foodDao(), viewModel)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddActivityContent(foodDao: FoodDao, viewModel: NutritionViewModel) {
    val context = LocalContext.current

    var foodImage by remember { mutableStateOf<Bitmap?>(null) }
    var foodName by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("") }
    var mealT by remember { mutableStateOf("Breakfast") }
    var foodKcal by remember { mutableStateOf("") }
    // Required remember
    var isNameEmpty by remember { mutableStateOf(false) }
    var isPortionEmpty by remember { mutableStateOf(false) }
    var isKcalEmpty by remember { mutableStateOf(false) }

    // Getting DateTime [dd:mm:yyyy]
    val currentDateTime = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDateTime = currentDateTime.format(formatter)

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val dataModels = listOf(
        ChoicesDataModel(name = "Breakfast"),
        ChoicesDataModel(name = "Lunch"),
        ChoicesDataModel(name = "Dinner"),
        ChoicesDataModel(name = "Snack")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item { Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
            ){
                foodImage = ThumbnailCaptureScreen()
            }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it
                                    isNameEmpty = false
                                    viewModel.fetchNutritionData(foodName)

                                    viewModel.nutritionData.value?.let { nutritionData ->
                                        foodKcal = nutritionData.nf_calories.toString()
                                    }
                    },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(Color.Black),
                    colors = myOutlinedTextFieldColors()
                )
                if (isNameEmpty) {
                    Text(
                        text = "*required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }}
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(
                    value = portionSize,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            portionSize = newValue
                            isPortionEmpty = false
                        } else {
                            Toast.makeText(context, "Please input numbers only", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    label = { Text("Portion Size (grams)") },
                    keyboardOptions =
                    KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(Color.Black),
                    colors = myOutlinedTextFieldColors()
                )
                if(isPortionEmpty) {
                    Text(
                        text = "*required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }}
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                MealTypeDropdown(
                    options = dataModels,
                    onSelect = { newValue: String->
                        mealT = newValue
                    },
                    modifier = Modifier.padding(10.dp))
                }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(
                    value = foodKcal,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            foodKcal = newValue
                            isKcalEmpty = false
                        } else {
                            Toast.makeText(context, "Please input numbers only", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    label = { Text("Food Kcal") },
                    keyboardOptions =
                    KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = myOutlinedTextFieldColors()
                )
                if(isKcalEmpty) {
                    Text(
                        text = "*required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }}
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Button(
                onClick = {
                    var hasEmpty = false
                    if (foodName.isEmpty()) {
                        isNameEmpty = true
                        hasEmpty = true
                    }
                    if (portionSize.isEmpty()) {
                        isPortionEmpty = true
                        hasEmpty = true
                    }
                    if (foodKcal.isEmpty()) {
                        isKcalEmpty = true
                        hasEmpty = true
                    }
                    if(!hasEmpty) {
                        foodDao.insertFood(
                            Food(
                                image = (if(foodImage!=null) foodImage.toString() else R.drawable.defaultfoodimg.toString()),
                                name = foodName,
                                portion = portionSize.toInt(),
                                mealType = mealT,
                                kcal = foodKcal.toInt(),
                                date = formattedDateTime
                            )
                        )

                        if(foodImage!=null){
                           saveFood(foodDao.getLatestFoodId(), foodImage, foodDao.getUserProfile().deviceID)
                        }

                        isLoading = true
                        coroutineScope.launch {
                            delay(4500L) // Wait for 4 seconds
                            isLoading = false
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = "Add contact into database") } }
        }
    }
    if(isLoading){
        NewFoodAddedFloatingDialog()
    }
}
data class ChoicesDataModel(val name: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTypeDropdown(
    options: List<ChoicesDataModel>,
    onSelect: (option: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            singleLine = true,
            value = selectedMealType.name,
            onValueChange = { /* No-op */ },
            label = { Text("Meal Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = myOutlinedTextFieldColors() // Use menuAnchor to make the field clickable
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option.name)
                    },
                    onClick = {
                        selectedMealType = option
                        onSelect(selectedMealType.name)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
@Composable
fun myOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    focusedTextColor = Color.Black,
    focusedBorderColor = Color.Black,
    focusedLabelColor = Color.Black,
    unfocusedContainerColor = Color.Transparent,
    unfocusedTextColor = Color.Black,
    unfocusedBorderColor = Color.Black,
    unfocusedLabelColor = Color.Black
)

@Composable
fun NewFoodAddedFloatingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Food added!",
                    textDecoration = TextDecoration.Underline)
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(150.dp)
                )
            }
        },
        confirmButton = { },
        dismissButton = { },
    )
}

private fun saveFood(foodID: Int, foodImage: Bitmap?, deviceID: Int){
    val storageRef = FirebaseStorage.getInstance().getReference(deviceID.toString())

    val myRef = Firebase.database.getReference(deviceID.toString())

    foodImage?.let { bitmap ->
        val imageFile = File.createTempFile(foodImage.toString(), ".jpg") // Create a temporary file
        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save the Bitmap to the file
        outputStream.close()

        val imageUri = Uri.fromFile(imageFile) // Get the Uri of the file

        storageRef.child(foodID.toString()).putFile(imageUri) // Use the Uri to upload
            .addOnSuccessListener { url ->
                url.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri->
                        val imgUrl = uri.toString()
                        val foodData = mapOf("id" to foodID, "food_image" to imgUrl)
                        myRef.child(foodID.toString()).setValue(foodData)
                    }

            }
    }
}
