package com.example.outfitment

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {
    var detectedPixels: Array<Array<DetectedPixel?>>? = null
    var imageBitmap : Bitmap? = null
}