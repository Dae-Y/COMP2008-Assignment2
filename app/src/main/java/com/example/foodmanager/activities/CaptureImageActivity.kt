package com.example.foodmanager.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.foodmanager.R
import java.io.ByteArrayOutputStream

@Composable
fun ThumbnailCaptureScreen(): Bitmap ?{
    var thumbnailImage by remember { mutableStateOf<Bitmap?>(null) }
    // This launcher is equivalent to ActivityResultLauncher
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
//    Image(
//        painter = rememberAsyncImagePainter(
//            model = thumbnailImage,
//            placeholder = painterResource(R.drawable.defaultfoodimg),
//            error = painterResource(R.drawable.defaultfoodimg),
//            contentScale = ContentScale.Fit
//        ),
//        contentDescription = "Tap to Capture Image",
//        modifier = Modifier
//            .size(150.dp)
//            .background(Color.LightGray, shape = CircleShape)
//            .clip(CircleShape)
//            .clickable {
//                cameraLauncher.launch()
//            }
//    )

//    Image(
//        painter = defaultImage,
//        contentDescription = "Contact Picture",
//        modifier = Modifier
//            .size(100.dp)
//            .clip(CircleShape)
//            .align(Alignment.CenterVertically)
//    )


    return thumbnailImage
}

fun bitmapToString(bitmap: Bitmap?): String{
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun stringToBitmap(base64String: String): Bitmap?{
    return try{
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }catch (e: IllegalArgumentException){
        e.printStackTrace()
        null
    }
}

fun drawableToBitmap(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    return (drawable as BitmapDrawable).bitmap
}

fun isEmptyBitmap(bitmap: Bitmap?): Boolean {
    if (bitmap == null) return true

    // Check dimensions
    if (bitmap.width != 1 || bitmap.height != 1) return false

    // Check pixel data
    val emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    return bitmap.sameAs(emptyBitmap)
}

