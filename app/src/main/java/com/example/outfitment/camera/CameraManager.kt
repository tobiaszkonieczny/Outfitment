package com.example.outfitment.camera


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CameraManager {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    object CameraConfig {
        val DEFAULT_BACK_CAMERA: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    fun startCamera(context: Context, previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            this.cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(android.util.Size(1080, 1080))
                .build()

            try {
                cameraProvider?.unbindAll()

                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraConfig.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        cameraProvider?.unbindAll()
    }

    fun setZoom(zoomLevel: Float, lifecycleOwner: LifecycleOwner) {
        cameraProvider?.let { provider ->
            val camera = provider.bindToLifecycle(
                lifecycleOwner, CameraConfig.DEFAULT_BACK_CAMERA
            )
            camera.cameraControl.setLinearZoom(zoomLevel / 5f) // Normalizacja zoomu (CameraX wymaga zakresu 0-1)
        }
    }

    fun takePhoto(context: Context, onImageCaptured: (Bitmap) -> Unit) {
        val imageCapture = imageCapture ?: return
        var imageBitmap: Bitmap
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    // Logowanie rozdzielczości zdjęcia
                    val width = image.width
                    val height = image.height
                    // Get the rotation degrees
                    val rotationDegrees = image.imageInfo.rotationDegrees
                    if (rotationDegrees != 0) {
                        // You can use a library like Glide or BitmapFactory to rotate the image
                        // Here's an example using BitmapFactory:
                        val bitmap = image.toBitmap() // Convert ImageProxy to Bitmap
                        imageBitmap = rotateBitmap(bitmap, rotationDegrees)
                        // Now you can use the rotatedBitmap as needed
                    }
                    else{
                        imageBitmap = image.toBitmap()
                    }
                    onImageCaptured(imageBitmap) // Przekazywanie zdjęcia po przechwyceniu
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraManagering", "Błąd przechwytywania zdjęcia", exception)
                }
            }
        )
    }
    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
