package com.example.foodmanager.activities

import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodmanager.R

@Composable
fun ThumbnailCaptureScreen(): Bitmap ?{
    var thumbnailImage by remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            thumbnailImage = bitmap
        } else {
            Log.e("CameraCapture", "Failed to capture image")
        }
    }

    // Display the captured image if available, otherwise show the default image
    AsyncImage(
        model = thumbnailImage,
        placeholder = painterResource(R.drawable.defaultfoodimg),
        error = painterResource(R.drawable.defaultfoodimg),
        contentScale = ContentScale.Crop,
        contentDescription = "Tap to Capture Image",
        modifier = Modifier
            .size(150.dp)
            .background(Color.LightGray, shape = CircleShape)
            .clip(CircleShape)
            .clickable {
                cameraLauncher.launch()
            }
    )
    return thumbnailImage
}
