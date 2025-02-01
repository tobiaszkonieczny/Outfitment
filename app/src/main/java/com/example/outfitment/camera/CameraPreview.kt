package com.example.outfitment.camera

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.outfitment.R

/**
 * This function renders a composable component that displays live camera feed.
 */
@Composable
fun GetCameraPreview(context: Context, showGrid: Boolean = true) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager() }
    val previewView = remember { PreviewView(context) }
    var img: ImageCapture? = null
    val widthAndHeight = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)


    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = widthAndHeight
                .align(Alignment.TopCenter)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxWidth(),
                update = { preview ->
                    cameraManager.startCamera(context, preview, lifecycleOwner)
                }
            )
        }

        if (showGrid) {
            PersonPostureOverlay(widthAndHeight)
        }

        // Dodanie przycisku do zrobienia zdjęcia
        IconButton (
            onClick = {
                cameraManager.stopCamera()
                //cameraManager.takePhoto(context) { imageProxy -> imageProxy.close() }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Take a photo."
            )
        }
    }
}

@Composable
fun PersonPostureOverlay(modifier: Modifier) {
    val imagePainter = painterResource(id = R.drawable.person_outline)  // logo.png w res/drawable

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}