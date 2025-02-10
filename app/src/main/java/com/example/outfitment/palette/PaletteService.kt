package com.example.outfitment.palette

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.example.outfitment.DetectedPixel

class PaletteService(
    private val imageBitmap: Bitmap,
    private val detectedPixels: Array<Array<DetectedPixel?>>
){
    fun getColors(): List<Color> {
        val palette = Palette.from(getProcessedBitmap()).generate()
        val vibrantColor = palette.getVibrantColor(android.graphics.Color.BLACK)
        val lightVibrantColor = palette.getLightVibrantColor(android.graphics.Color.BLACK)
        val darkVibrantColor = palette.getDarkVibrantColor(android.graphics.Color.BLACK)
        val mutedColor = palette.getMutedColor(android.graphics.Color.BLACK)
        val lightMutedColor = palette.getLightMutedColor(android.graphics.Color.BLACK)
        val darkMutedColor = palette.getDarkMutedColor(android.graphics.Color.BLACK)

        val colorList = listOf(
            Color(vibrantColor),
            Color(lightVibrantColor),
            Color(darkVibrantColor),
            Color(mutedColor),
            Color(lightMutedColor),
            Color(darkMutedColor)
        )

        return colorList
    }

    fun getProcessedBitmap(): Bitmap {
        val scaledBitmap = getScaledBitmap(imageBitmap)
        val newBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        for(column in detectedPixels){
            for(pixel in column){
                if(pixel != null&&pixel.classId==3){
                   newBitmap.setPixel(pixel.y, pixel.x, scaledBitmap.getPixel(pixel.y, pixel.x))
                }
            }
        }
        return newBitmap
    }

    private fun getScaledBitmap(imageBitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(imageBitmap, 256, 256, true)

    }
}