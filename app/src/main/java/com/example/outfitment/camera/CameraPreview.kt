package com.example.outfitment.camera

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.outfitment.R
import com.example.outfitment.ResultViewModel


/**
 * This function renders a composable component that displays live camera feed.
 */
@Composable
fun GetCameraPreview(navController: NavController, context: Context,resultViewModel: ResultViewModel, showGrid: Boolean = true) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager() }

    // States for the components display.
    val isPhotoCaptured = remember { mutableStateOf(false) }
    val capturedImage = remember { mutableStateOf<Bitmap?>(null) }

    // If the photo was captured, render new component
    if (isPhotoCaptured.value) {
        PhotoCapturedScreen(capturedImage.value)
    } else {
        // Render Camera preview
        CameraPreview(cameraManager, context, lifecycleOwner, showGrid) { imageBitmap ->
            capturedImage.value = imageBitmap
            isPhotoCaptured.value = true
            resultViewModel.imageBitmap = imageBitmap
            navController.navigate("loading")
        }
    }
}

@Composable
fun CameraPreview(
    cameraManager: com.example.outfitment.camera.CameraManager,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    showGrid: Boolean,
    onImageCaptured: (Bitmap) -> Unit
) {
    val zoomLevel = remember { mutableStateOf(1f) } // Zoom od 1x (bez powiększenia)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text above the preview.
        Text("TAKE A PHOTO", color = Color.DarkGray, modifier = Modifier.padding(bottom = 16.dp))

        // Camera preview with the person outline grid applied.
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Setting up the width to be 90% of parent
                .aspectRatio(1f) // 1:1 Aspect ratio
                .clipToBounds() // Clip the preview to a square
        ) {
            // Camera preview.
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(1080, 1080)
                    }
                },
                modifier = Modifier.fillMaxSize(), // Preview fills 90% of the screen.
                update = { preview ->
                    cameraManager.startCamera(context, preview, lifecycleOwner)
                }
            )

            // Person outline grid.
            if (showGrid) {
                PersonPostureOverlay(
                    modifier = Modifier
                        .padding(6.dp)
                        .aspectRatio(1f)
                        .align(Alignment.Center)
                )
            }
        }

        CameraZoomSlider(zoomLevel) { newZoom ->
            cameraManager.setZoom(newZoom, lifecycleOwner)
        }

        // Button
        Box(
            modifier = Modifier
                .padding(top = 32.dp)
        ) {
            IconButton(
                onClick = {
                    cameraManager.takePhoto(context) { imageBitmap ->
                        onImageCaptured(imageBitmap) // Callback to update the image and capture the photo
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Take a photo."
                )
            }
        }
    }
}

@Composable
fun PhotoCapturedScreen(imageBitmap: Bitmap?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            val bitmap = remember { imageBitmap }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Photo",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("Loading photo...")
        }
    }
}

@Composable
fun PersonPostureOverlay(modifier: Modifier = Modifier) {
    val imagePainter = painterResource(id = R.drawable.person_outline)

    Image(
        painter = imagePainter,
        contentDescription = null,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun CameraZoomSlider(zoomLevel: MutableState<Float>, onZoomChanged: (Float) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Zoom: ${String.format("%.2f", zoomLevel.value)}x")

        Slider(
            value = zoomLevel.value,
            onValueChange = { newZoom ->
                zoomLevel.value = newZoom
                onZoomChanged(newZoom)
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Gray,
                activeTrackColor = Color.DarkGray,
                inactiveTrackColor = Color.LightGray
            ),
            valueRange = 1f..5f, // Zoom from 1x to 5x.
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}