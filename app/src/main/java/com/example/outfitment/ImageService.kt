package com.example.outfitment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageService {

    companion object{
        fun getImageForModel(context: Context, imageBitmap: Bitmap, modelInputSize: Int): ByteBuffer {

            // Allocate buffer based on model input size
            val byteBuffer = ByteBuffer.allocateDirect(4 * modelInputSize * modelInputSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            // resize
            val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, modelInputSize, modelInputSize, false)

            // convert
            for (y in 0 until resizedBitmap.height) {
                for (x in 0 until resizedBitmap.width) {
                    val pixel = resizedBitmap.getPixel(x, y)

                    // conversion of each pixel to float values
                    val r = ((pixel shr 16) and 0xFF) / 255.0f
                    val g = ((pixel shr 8) and 0xFF) / 255.0f
                    val b = (pixel and 0xFF) / 255.0f

                    // Add float rgb values to ByteBuffer
                    byteBuffer.putFloat(r)
                    byteBuffer.putFloat(g)
                    byteBuffer.putFloat(b)
                }
            }

            byteBuffer.rewind()
            return byteBuffer
        }
    }

}