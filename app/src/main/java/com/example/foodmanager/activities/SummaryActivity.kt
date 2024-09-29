package com.example.foodmanager.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodmanager.MainActivity
import com.example.foodmanager.NavBar
import com.example.foodmanager.ui.theme.FoodManagerTheme

class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        SummaryActivityTest()
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
    }

    // Make it so when user press back will go to home by default
    // Instead of going to the previous Activity
    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun SummaryActivityTest() {
    // TODO: Change later, this is just a test
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Summary",
            fontSize = MaterialTheme.typography.h3.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun SummaryActivityPreview() {
    SummaryActivityTest()
}