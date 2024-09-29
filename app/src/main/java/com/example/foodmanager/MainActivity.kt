package com.example.foodmanager

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.foodmanager.ui.theme.FoodManagerTheme

class MainActivity : ComponentActivity() {
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
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f))
                    {
                        MainActivityTest()
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth())
                    {
                        NavBar(context)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Exit the app
        finishAffinity()
    }
}


@Composable
fun MainActivityTest() {
    // TODO: Change later, this is just a test
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Magenta),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material.Text(
            text = "HOME",
            fontSize = MaterialTheme.typography.h3.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    MainActivityTest()
}
