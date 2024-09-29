package com.example.foodmanager

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodmanager.activities.AddActivity
import com.example.foodmanager.activities.SummaryActivity

@Composable
fun NavBar(context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .height(58.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
                .padding(5.dp)
        ) {
            val homeButtonColors = btnColorChange(context, "HomeActivity")
            val addButtonColors = btnColorChange(context, "AddActivity")
            val summaryButtonColors = btnColorChange(context, "SummaryActivity")

            Button(onClick = { homeActivity(context) },
                colors = homeButtonColors,
                border = BorderStroke(0.dp, Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp),
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Icon(imageVector = Icons.Rounded.Home,
                    contentDescription = "Home",
                    tint = Color.White)}

            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .height(48.dp) // Adjust height as needed
                    .width(1.dp)
            )

            Button(onClick = { addActivity(context) },
                colors = addButtonColors,
                border = BorderStroke(0.dp, Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp),
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Icon(imageVector = Icons.Rounded.Add,
                    contentDescription = "Add",
                    tint = Color.White) }

            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .height(48.dp) // Adjust height as needed
                    .width(1.dp)
            )

            Button(onClick = { summaryActivity(context) },
                colors = summaryButtonColors,
                border = BorderStroke(0.dp, Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp),
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.List,
                    contentDescription = "Summary",
                    tint = Color.White)}
        }
    }
}
@Composable
@Preview
fun AddActivityPreview() {
    NavBar(context = LocalContext.current)
}

private fun homeActivity(context: Context) {
//    Toast.makeText(context, "Item added Into dataBase", Toast.LENGTH_LONG).show()
    if (context !is MainActivity) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}
private fun addActivity(context: Context) {
//    Toast.makeText(context, "Item added Into dataBase", Toast.LENGTH_LONG).show()
    if (context !is AddActivity) {
        val intent = Intent(context, AddActivity::class.java)
        context.startActivity(intent)
    }
}
private fun summaryActivity(context: Context) {
//    Toast.makeText(context, "Item added Into dataBase", Toast.LENGTH_LONG).show()
    if (context !is SummaryActivity) {
        val intent = Intent(context, SummaryActivity::class.java)
        context.startActivity(intent)
    }
}

@Composable
private fun btnColorChange(context: Context, currentClass: String): ButtonColors {
    val currentActivity = context::class.java.simpleName
    return if (currentActivity == currentClass) {
        ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
    } else {
        ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
}