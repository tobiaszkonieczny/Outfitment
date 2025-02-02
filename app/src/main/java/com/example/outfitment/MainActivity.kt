package com.example.outfitment

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.outfitment.ui.theme.OutfitmentTheme

class MainActivity : ComponentActivity() {
    private lateinit var modelService: ModelService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        modelService = ModelService(this)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_image)
        setContent {
            val resultViewModel: ResultViewModel = viewModel()
            val navController = rememberNavController()
            OutfitmentTheme {
                NavHost(navController, startDestination = "loading") {
                    composable("loading") {
                        LoadingScreen(navController, bitmap, resultViewModel)
                    }
                    composable("result") {
                        Result(resultViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {


    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OutfitmentTheme {
        Greeting("Android")
    }
}