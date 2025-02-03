package com.example.outfitment

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoadingScreen(navController: NavController, bitmap: Bitmap, resultViewModel: ResultViewModel) {
    var isProcessingComplete by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val app = context.applicationContext as App
    val modelService = app.modelService
    modelService.imageBitmap = bitmap

    LaunchedEffect(modelService.isInterpreterInitialized) {
        withContext(Dispatchers.IO) {
            if (modelService.isInterpreterInitialized) {
                val result = modelService.runInterpreter()
                result?.let {
                    val detectedPixels = modelService.getDetectedPixels(it, threshold = 0.5f)
                    resultViewModel.detectedPixels = detectedPixels // Store detected pixels in ViewModel
                    resultViewModel.imageBitmap = bitmap // Store image bitmap in ViewModel
                    // Once processing is complete, update state
                    isProcessingComplete = true
                }
            }
        }
    }

    // Navigate when processing is complete
    LaunchedEffect(isProcessingComplete) {
        if (isProcessingComplete) {
            navController.navigate("result")
        }
    }

    // UI while processing
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}