package com.example.foodmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodmanager.ui.theme.FoodManagerTheme

class MainActivity : ComponentActivity() {
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
                    // Main content that fills the rest of the screen
                    Box(
                        modifier = Modifier
                            .weight(1f)  // The content should take the remaining space
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        MainActivityTest()
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

@Composable
fun MainActivityTest() {
    var clickedStudent by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    // TODO: Temp numbers for now
    // Probably needs to be stored into database after app close still needs to keep this data
    val setDailyKcal = 2500
    val totalKcal = 1000 // Total kcal from roomdatabase
    val remainingKcal = 1500 // setDailyKcal - totalKcal

    val testValue = mutableListOf<String>()
    for (i in 1..20) {
        testValue.add(("Apple"))
    }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier = Modifier.height(20.dp))
        CurvedTextWithElevation("Set Daily Kcal: ${setDailyKcal}")
        Spacer(modifier = Modifier.height(5.dp))
        CurvedTextWithElevation("Total Kcal: ${totalKcal}")
        Spacer(modifier = Modifier.height(5.dp))
        CurvedTextWithElevation("Remaining Kcal: ${remainingKcal}")

        LazyColumn (modifier = Modifier.padding(16.dp)) {
            items(testValue) { foodItem ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .shadow(4.dp, RoundedCornerShape(8.dp))
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .clickable {
                            showDialog = true;
//                            clickedStudent = contact.id
                        }){
                    FoodCard()
                }}
        }
    }
}
@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    MainActivityTest()
}

@Composable
fun CurvedTextWithElevation(text: String) {
    val isClickable = text.contains("Set Daily Kcal: ", ignoreCase = true)
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(4.dp)
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { /* TODO: Handle click */ } else Modifier)
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
@Preview
@Composable
fun PreviewCurvedTextWithElevation(){
    CurvedTextWithElevation("DO NOT LOOK BEHIND YOU!![JK]")
}

@Composable
fun FoodCard() {
//    val bitmap = stringToBitmap(contact.picture)
    val defaultImage = painterResource(id = R.drawable.defaultfoodimg)

    // Test values, delete when there is real data
    val foodName = "Rice and breadlol"
    val foodKcal = 1000
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth()
        ) {
            // painter = if(bitmap != null) BitmapPainter(bitmap.asImageBitmap()) else defaultImage
            Image(
                painter = defaultImage,
                contentDescription = "Contact Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp)) // Changed to width for horizontal spacing
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Food Name: $foodName",
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Food Kcal: $foodKcal",
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun ViewFoodCard(){
    FoodCard()
}