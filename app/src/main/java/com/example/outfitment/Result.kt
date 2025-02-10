package com.example.outfitment

import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.example.outfitment.palette.PaletteService

@Composable
fun Result(resultViewModel: ResultViewModel, navController: NavController) {
    val detectedPixels = resultViewModel.detectedPixels
    val imageBitmap = resultViewModel.imageBitmap
    var canvasBitmap by remember {
        mutableStateOf(
            imageBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                ?: throw Exception("Image bitmap is null")
        )
    }
    var colorList by remember { mutableStateOf(listOf<Color>()) }
    val labelMap = mapOf(
        0 to Pair("background", android.graphics.Color.rgb(0, 0, 0)),
        1 to Pair("skin", android.graphics.Color.rgb(255, 0, 0)),
        2 to Pair("hair", android.graphics.Color.rgb(0, 255, 0)),
        3 to Pair("cloth", android.graphics.Color.rgb(0, 0, 255))
    )
    val updatedBitmap = imageBitmap?.copy(Bitmap.Config.ARGB_8888, true)

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
        val paletteService = PaletteService(imageBitmap!!, detectedPixels!!)
        val newBitmap = paletteService.getProcessedBitmap()
        colorList = paletteService.getColors()
        canvasBitmap = newBitmap
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(5))
            ) {
                Text(
                    "Original Image",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primary)
                        .height(60.dp)
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                resultViewModel.imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Original Image",
                        modifier = Modifier
                            .height(300.dp)
                            .border(0.5.dp, MaterialTheme.colorScheme.secondary, RectangleShape)
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(5))

            ) {
                Text(
                    "Pixels detected as clothing",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primary)
                        .height(60.dp)
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                Image(
                    bitmap = canvasBitmap.asImageBitmap(),
                    contentDescription = "Segmented Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .align(Alignment.CenterHorizontally)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, RectangleShape)
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5))
        ) {
            Text(
                "Your Color Palette",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(colorList) { color ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .aspectRatio(1.5f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                    )
                }
            }
        }
        Button(
            onClick = {

                navController.navigate("camera")
                resultViewModel.imageBitmap = null
                resultViewModel.detectedPixels = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5))
        ) {
            Icon(
                imageVector = Icons.Filled.Replay,
                contentDescription = "Take a photo."
            )
        }

    }
}
