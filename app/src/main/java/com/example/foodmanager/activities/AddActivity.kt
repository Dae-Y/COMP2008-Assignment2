package com.example.foodmanager.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodmanager.DatabaseProvider
import com.example.foodmanager.Food
import com.example.foodmanager.FoodDao
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.R
import com.example.foodmanager.ui.theme.FoodManagerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddActivity : ComponentActivity() {
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
                        AddActivityContent(foodDao = db.foodDao())
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
fun AddActivityContent(foodDao: FoodDao) {
    val context = LocalContext.current

    // TODO TEMP DATA
    var foodImage by remember { mutableStateOf("") }
    var foodName by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("") }
    var mealT by remember { mutableStateOf("") }
    var foodKcal by remember { mutableStateOf("") }
    // Required remember
    var isNameEmpty by remember { mutableStateOf(false) }
    var isPortionEmpty by remember { mutableStateOf(false) }
    var isMealTypeEmpty by remember { mutableStateOf(false) }
    var isKcalEmpty by remember { mutableStateOf(false) }

    // Getting DateTime [dd:mm:yyyy]
    val currentDateTime = remember { LocalDateTime.now() }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDateTime = currentDateTime.format(formatter)

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
            // TODO: Temp Img placement
            item { Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(R.drawable.defaultfoodimg),
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(50.dp))
                        .padding(8.dp)
                )}
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it
                                    isNameEmpty = false},
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth()
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
                if(mealT.isNotEmpty()) isMealTypeEmpty = false
                if(isMealTypeEmpty) {
                    Text(
                        text = "*required",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }}
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
                    modifier = Modifier.fillMaxWidth()
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
                    if (mealT.isEmpty()) {
                        isMealTypeEmpty = true
                        hasEmpty = true
                    }
                    if (foodKcal.isEmpty()) {
                        isKcalEmpty = true
                        hasEmpty = true
                    }
                    if(!hasEmpty) {
                        foodDao.insertFood(
                            Food(
                                name = foodName,
                                portion = portionSize.toInt(),
                                mealType = mealT,
                                kcal = foodKcal.toInt(),
                                date = formattedDateTime
                            )
                        )
                        Toast.makeText(context, "Food added!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = "Add contact into database") } }
        }
    }
}
data class ChoicesDataModel(val name: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTypeDropdown(
    options: List<ChoicesDataModel>,
    onSelect: (option: String) -> Unit,
    modifier: Modifier = Modifier)
{
    var expanded by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = it },
        modifier = Modifier.then(modifier)
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            value = selectedMealType.name,
            onValueChange = { },
            label = { Text("Meal Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Text(option.name)
                        }
                    },
                    onClick = {
                        selectedMealType = option
                        onSelect(selectedMealType.name)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}


