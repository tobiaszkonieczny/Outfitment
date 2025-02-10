package com.example.outfitment

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun LoadingScreen(navController: NavController, resultViewModel: ResultViewModel) {
    var isProcessingComplete by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val app = context.applicationContext as App
    val modelService = app.modelService
    modelService.imageBitmap = resultViewModel.imageBitmap!!

    LaunchedEffect(modelService.isInterpreterInitialized) {
        Log.d("LoadingScreen", "isInterpreterInitialized: ${modelService.isInterpreterInitialized}")
        withContext(Dispatchers.IO) {
            if (modelService.isInterpreterInitialized) {
                val result = modelService.runInterpreter()
                result?.let {
                    val detectedPixels = modelService.getDetectedPixels(it, threshold = 0.5f)
                    resultViewModel.detectedPixels = detectedPixels // Store detected pixels in ViewModel
                    resultViewModel.imageBitmap = resultViewModel.imageBitmap // Store image bitmap in ViewModel
                    // Once processing is complete, update state
                    isProcessingComplete = true
                }
            }
        }
    }

    // Navigate when processing is complete
    LaunchedEffect(isProcessingComplete) {
        if (isProcessingComplete) {
            isProcessingComplete = false
            navController.navigate("result")
        }
    }

    // UI while processing
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}