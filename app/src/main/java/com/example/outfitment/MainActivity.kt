package com.example.outfitment

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.outfitment.camera.GetCameraPreview
import com.example.outfitment.ui.theme.OutfitmentTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


class MainActivity : ComponentActivity() {
    private lateinit var modelService: ModelService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        modelService = ModelService(this)
        setContent {
            val resultViewModel: ResultViewModel = viewModel()
            val navController = rememberNavController()
            OutfitmentTheme {
                Scaffold(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(navController, startDestination = "camera") {
                        composable("camera") {
                            StartTheCamera(navController, modifier = Modifier.padding(16.dp), context = this@MainActivity, resultViewModel)
                        }
                        composable("loading") {
                            LoadingScreen(navController, resultViewModel)
                        }
                        composable("result") {
                            Result(resultViewModel, navController)
                        }
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartTheCamera(navController: NavController, modifier: Modifier = Modifier, context: Context, resultViewModel: ResultViewModel) {
    // pobranie stanu permisji
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        Log.d("TAG", "StartTheCamera: granted")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            GetCameraPreview(navController, context, resultViewModel)
        }
    }

    else {
        // wyswietlamy powiadomienie ze potrzebne sa permisje
        Column(
            modifier = modifier.fillMaxSize().wrapContentSize().widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // sprawdzamy, czy permisja zostala odrzucona, czy jednak to jest pierwszy raz po prostu w apce
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                "Camera access is required for the app to run"
            } else {
                "Please grant permission to camera."
            }
            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            // tu zmieniamy stan.
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Give permission")
            }
        }
    }
    }