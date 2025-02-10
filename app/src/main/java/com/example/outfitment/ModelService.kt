package com.example.outfitment

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.tasks.Task
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.InterpreterApi
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class ModelService(private val context: Context) {
    private var modelFileName: String = "unet_model6.tflite"
    private lateinit var interpreter: InterpreterApi
    var isInterpreterInitialized by mutableStateOf(false)

    var imageBitmap: Bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)

    private val initializeTask: Task<Void> by lazy { TfLite.initialize(context) }

    init {
        initializeTask.addOnSuccessListener {
            val interpreterOption =
                InterpreterApi.Options().setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            interpreter = InterpreterApi.create(
                loadModelFile(context, modelFileName),
                interpreterOption
            )
            isInterpreterInitialized = true
        }.addOnFailureListener { e ->
            Log.e("Interpreter", "Cannot initialize interpreter", e)
        }
    }

    fun runInterpreter(): Array<Array<Array<FloatArray>>>? {
        if (!isInterpreterInitialized) {
            Log.e("Interpreter", "Interpreter not initialized")
            return null
        }

        val labelProbArray = Array(1) {
            Array(256) {
                Array(256) {
                    FloatArray(4)
                }
            }
        }
        try {
            interpreter.run(ImageService.getImageForModel(context, imageBitmap, 256), labelProbArray)
        } catch (e: Exception) {
            Log.e("Interpreter", "Error running interpreter", e)
        }
        return labelProbArray
    }

    fun getDetectedPixels(labelProbArray: Array<Array<Array<FloatArray>>>, threshold: Float): Array<Array<DetectedPixel?>> {
        val pixelArray: Array<Array<DetectedPixel?>> = Array(256) {
            Array(256) { null }
        }
        for (i in 0 until 256) {
            for (j in 0 until 256) {
                for (k in 0 until 4) {
                    if(labelProbArray[0][i][j][k] > threshold&& k!=0){
                        pixelArray[i][j] = DetectedPixel(i, j, k)
//                        Log.d("Pixel", "Detected object at ($i, $j, $k) with confidence ${labelProbArray[0][i][j][k]}")
                    }
                }
            }
        }
        return pixelArray
    }

    private fun loadModelFile(context: Context, modelFileName: String): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFileName)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
data class DetectedPixel(
    val x: Int,
    val y: Int,
    val classId: Int,
)
