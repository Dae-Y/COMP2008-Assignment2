package com.example.foodmanager.activities

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.example.foodmanager.DatabaseProvider
import com.example.foodmanager.Food
import com.example.foodmanager.FoodDao
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.Nutrition
import com.example.foodmanager.NutritionViewModel
import com.example.foodmanager.R
import com.example.foodmanager.ui.theme.FoodManagerTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

@SuppressLint("CoroutineCreationDuringComposition")
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
    var isAPIgetFood by remember { mutableStateOf(false) }

    // Getting DateTime [dd:mm:yyyy]
    val currentDateTime = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDateTime = currentDateTime.format(formatter)

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showNutrientsDialog by remember { mutableStateOf(false) }

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
                    onValueChange = { foodName = it.lowercase()
                                    isNameEmpty = false
                    },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(Color.Black),
                    colors = myOutlinedTextFieldColors(),
                    trailingIcon = {  // Add a trailing icon
                        IconButton(onClick = {
                            viewModel.fetchNutritionData(foodName)
                            viewModel.nutritionData.value?.let { nutritionData ->
                                foodKcal = nutritionData.nf_calories.toString()
                                isAPIgetFood = true
                            }
                            if(isAPIgetFood){
                                Toast.makeText(context,
                                    "API found food ${foodName}, nutrients added",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                            else
                            {
                                Toast.makeText(context,
                                    "API fail to find food ${foodName}!,\nManual input require later",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        },
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .border(
                                    width = 1.dp, // Border width
                                    color = Color.Gray, // Border color
                                    shape = CircleShape
                                )) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        }
                }
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
                    label = { Text("Portion Size (1,2 etc.)") },
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
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            foodKcal = newValue
                            isKcalEmpty = false
                        } else {
                            Toast.makeText(context, "Please input numbers only", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    label = { Text("Food kcal") },
                    keyboardOptions =
                    KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = myOutlinedTextFieldColors(),
//                    trailingIcon = {
//                        if (!isAPIgetFood){
//                            Row(
//                                modifier = Modifier
//                                    .padding(end = 12.dp) // Add padding to the end (right)
//                                    .border(
//                                        width = 1.dp,
//                                        color = Color.Gray,
//                                        shape = RectangleShape
//                                    )
//                                    .clickable { showNutrientsDialog = true },
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Box(contentAlignment = Alignment.Center) { // Add a Box for content alignment
//                                    Text(
//                                        text = "Manually add Nutrients",
//                                        modifier = Modifier.padding(4.dp) // Add padding around the text
//                                    )
//                                }
//                            }
//                        }
//                    }
                )
                if(isKcalEmpty) {
                    Text(
                        text = "*required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                if(!isAPIgetFood) {
                    Text(
                        text = "*Manual nutrients input will be prompt after add food",
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
                    if (!isAPIgetFood && !isNameEmpty && !isPortionEmpty && !isKcalEmpty)
                    {
                        showNutrientsDialog = true
                    }
                    if(!hasEmpty) {

                       saveFood(foodDao,
                           foodImage,
                           foodName,
                           portionSize,
                           mealT,
                           foodKcal,
                           formattedDateTime)

                        if(!showNutrientsDialog) {
                            isLoading = true
                            coroutineScope.launch {
                                delay(4500L) // Wait for 4 seconds
                                isLoading = false
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = "Add food") } }
        }
    }
    if(isLoading){
        NewFoodAddedFloatingDialog()
        coroutineScope.launch {
            delay(4500L) // Wait for 4 seconds
            isLoading = false
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
    if(showNutrientsDialog){
        UpdateDailyKcalDialog(foodId = foodDao.getLatestFoodId(),
            showDialog = showNutrientsDialog,
            setShowDialog = { showNutrientsDialog = it },
            foodDao = foodDao,
            context = context,
            coroutineScope = coroutineScope,
            setIsLoading = {isLoading = it})
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

private fun saveFood(
    foodDao: FoodDao,
    foodImage: Bitmap?,
    foodName: String,
    portionSize: String,
    mealT: String,
    foodKcal: String,
    formattedDateTime: String
){
    val deviceID = foodDao.getUserProfile().deviceID
    val foodID = foodDao.getLatestFoodId()

    val storageRef = FirebaseStorage.getInstance().getReference(deviceID.toString())

    val myRef = Firebase.database.getReference(deviceID.toString())


    if(foodImage != null){
        foodImage.let { bitmap ->
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
                            foodDao.insertFood(
                                Food(
                                    image = imgUrl,
                                    name = foodName,
                                    portion = portionSize.toInt(),
                                    mealType = mealT,
                                    kcal = foodKcal.toDouble(),
                                    date = formattedDateTime
                                )
                            )
                        }
                }
        }
    }
    else
    {
        foodDao.insertFood(
            Food(
                image = R.drawable.defaultfoodimg.toString(),
                name = foodName,
                portion = portionSize.toInt(),
                mealType = mealT,
                kcal = foodKcal.toDouble(),
                date = formattedDateTime
            )
        )
    }

}

@Composable
fun UpdateDailyKcalDialog(
    foodId: Int,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    foodDao: FoodDao,
    context: Context,
    coroutineScope: CoroutineScope,
    setIsLoading: (Boolean) -> Unit
) {
    var servingQty by remember { mutableIntStateOf(0) }

    var servingWeight by remember { mutableDoubleStateOf(0.0) }
    var servingUnit by remember { mutableStateOf("grams") }
    var calories by remember { mutableDoubleStateOf(0.0) }

    var totalFat by remember { mutableDoubleStateOf(0.0) }
    var satFat by remember { mutableDoubleStateOf(0.0) }
    var cholesterol by remember { mutableDoubleStateOf(0.0) }

    var sodium by remember { mutableDoubleStateOf(0.0) }
    var carbohydrate by remember { mutableDoubleStateOf(0.0) }
    var fiber by remember { mutableDoubleStateOf(0.0) }

    var sugars by remember { mutableDoubleStateOf(0.0) }
    var protein by remember { mutableDoubleStateOf(0.0) }
    var potassium by remember { mutableDoubleStateOf(0.0) }

    var phosphorus by remember { mutableDoubleStateOf(0.0) }

    var isLoading by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { setShowDialog(false) },
            title = { Text("Update/add Nutrients") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    item { NutTextField(
                        value = servingQty.toString(),
                        onValueChange = { newValue -> servingQty = newValue.toInt() },
                        label = "calories",
                        context = context
                    )  }
                    item { NutTextField(
                        value = servingWeight.toString(),
                        onValueChange = { newValue -> servingWeight = newValue },
                        label = "serving weight",
                        context = context
                    )  }
                    item { OutlinedTextField(
                        value = servingUnit,
                        onValueChange = { newValue -> servingUnit = newValue },
                        label = { Text("Serving Unit") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(Color.Black),
                        colors = myOutlinedTextFieldColors(),
                    )  }
                    item { NutTextField(
                        value = calories.toString(),
                        onValueChange = { newValue -> calories = newValue },
                        label = "calories",
                        context = context
                    )  }
                    item { NutTextField(
                        value = totalFat.toString(),
                        onValueChange = { newValue -> totalFat = newValue },
                        label = "fat",
                        context = context
                    )  }
                    item { NutTextField(
                        value = satFat.toString(),
                        onValueChange = { newValue -> satFat = newValue },
                        label = "saturated fat",
                        context = context
                    )  }
                    item { NutTextField(
                        value = cholesterol.toString(),
                        onValueChange = { newValue -> cholesterol = newValue },
                        label = "cholesterol",
                        context = context
                    )  }
                    item { NutTextField(
                        value = sodium.toString(),
                        onValueChange = { newValue -> sodium = newValue },
                        label = "sodium",
                        context = context
                    )  }
                    item { NutTextField(
                        value = carbohydrate.toString(),
                        onValueChange = { newValue -> carbohydrate = newValue },
                        label = "carbohydrate",
                        context = context
                    )  }
                    item { NutTextField(
                        value = fiber.toString(),
                        onValueChange = { newValue -> fiber = newValue },
                        label = "carbohydrate",
                        context = context
                    )  }
                    item { NutTextField(
                        value = sugars.toString(),
                        onValueChange = { newValue -> sugars = newValue },
                        label = "sugars",
                        context = context
                    )  }
                    item { NutTextField(
                        value = protein.toString(),
                        onValueChange = { newValue -> protein = newValue },
                        label = "protein",
                        context = context
                    )  }
                    item { NutTextField(
                        value = potassium.toString(),
                        onValueChange = { newValue -> potassium = newValue },
                        label = "potassium",
                        context = context
                    )  }
                    item { NutTextField(
                        value = phosphorus.toString(),
                        onValueChange = { newValue -> phosphorus = newValue },
                        label = "phosphorus",
                        context = context
                    )  }
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    /*TODO: handle nutrients add/update*/
                    foodDao.insertNutrition(Nutrition(
                        foodId = foodId,
                        servingQty = servingQty.toInt(),
                        servingUnit = servingUnit,
                        servingWeight = servingWeight,
                        calories = calories,
                        totalFat = totalFat,
                        saturatedFat = satFat,
                        cholesterol = cholesterol,
                        sodium = sodium,
                        carbohydrate = carbohydrate,
                        fiber = fiber,
                        sugars = sugars,
                        protein = protein,
                        potassium = potassium,
                        phosphorus = phosphorus,
                    ))
                    Toast.makeText(context, "Nutrients Added/Updated", Toast.LENGTH_LONG).show()
                    setShowDialog(false)
                    setIsLoading(true)
                }) {
                    Text("Add/Update Nutrients")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Operation cancel", Toast.LENGTH_LONG).show()
                    setShowDialog(false)

                    foodDao.deleteFoodsId(id = foodId) // had to do this
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text("Cancel")
                    }
            }
        )
    }

}

@Composable
fun NutTextField(
    value: String,
    onValueChange: (Double) -> Unit,
    label: String,
    context: Context
) {
    var text by remember { mutableStateOf(TextFieldValue(value.toString())) }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            if (validateInputDouble(newValue, context)) {
                text = newValue  // Update the TextFieldValue state
                onValueChange(newValue.text.toDoubleOrNull() ?: 0.0) // Pass the Double value
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

fun validateInputDouble(value: TextFieldValue, context: Context): Boolean {
    val text = value.text // Extract the text from TextFieldValue
    if (text.isEmpty() || text.toDoubleOrNull() != null) {
        return true
    } else {
        Toast.makeText(context, "Please input a valid number", Toast.LENGTH_SHORT).show()
        return false
    }
}