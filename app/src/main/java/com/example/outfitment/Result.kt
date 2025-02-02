package com.example.outfitment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette

@Composable
fun Result(resultViewModel: ResultViewModel) {
    val detectedPixels = resultViewModel.detectedPixels
    val imageBitmap = resultViewModel.imageBitmap
    var canvasBitmap by remember { mutableStateOf(imageBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        ?: throw Exception("Image bitmap is null")) }
    val labelMap = mapOf(
        0 to Pair("background", android.graphics.Color.rgb(0, 0, 0)),
        1 to Pair("skin", android.graphics.Color.rgb(255, 0, 0)),
        2 to Pair("hair", android.graphics.Color.rgb(0, 255, 0)),
        3 to Pair("cloth", android.graphics.Color.rgb(0, 0, 255))
    )
    val updatedBitmap = imageBitmap?.copy(Bitmap.Config.ARGB_8888, true)
    val palette = Palette.from(canvasBitmap).generate()
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
    LaunchedEffect(Unit) {
        if (detectedPixels != null) {
            for (column in detectedPixels) {
                for (pixel in column) {
                    if (pixel != null) {
                        updatedBitmap?.let { Canvas(it) }?.drawPoint(
                            pixel.y.toFloat(),
                            pixel.x.toFloat(),
                            Paint().apply {
                                color = labelMap[pixel.classId]?.second
                                    ?: android.graphics.Color.MAGENTA
                                strokeWidth = 1f
                                alpha = 128
                            }
                        )
                    }
                }
            }
        }

        if (updatedBitmap != null) {
            canvasBitmap = updatedBitmap
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {

        Image(
            bitmap = canvasBitmap.asImageBitmap(),
            contentDescription = "Segmented Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            colorList.forEach { color ->
                Box(
                    modifier = Modifier
                        .background(color)
                        .size(40.dp, 80.dp)
                )
            }
        }
    }
}
