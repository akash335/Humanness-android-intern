package com.humanness.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppRoot(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        // Step 1 – Start Screen
        composable("start") {
            StartScreen(
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                // 👉 PDF says: Start Sample Task → Noise Test Screen :contentReference[oaicite:0]{index=0}
                onStartSampleTask = {
                    navController.navigate("noiseTest")
                }
            )
        }

        // Step 2 – Noise Test Screen
        composable("noiseTest") {
            NoiseTestScreen(
                // back arrow should return to start screen
                onBack = { navController.popBackStack() },
                // On pass → go to Task Selection Screen :contentReference[oaicite:1]{index=1}
                onPassed = {
                    navController.navigate("taskSelection") {
                        // keep start in back stack
                        popUpTo("start") { inclusive = false }
                    }
                }
            )
        }

        // Step 3 – Task Selection Screen
        composable("taskSelection") {
            TaskSelectionScreen(
                onBack = { navController.popBackStack() },
                onTextReading = { navController.navigate("textReading") },
                onImageDescription = { navController.navigate("imageDescription") },
                onPhotoCapture = { navController.navigate("photoCapture") },
                onViewHistory = { navController.navigate("taskHistory") }
            )
        }

        // Step 4 – Text Reading
        composable("textReading") {
            TextReadingScreen(onBack = { navController.popBackStack() })
        }

        // Step 5 – Image Description
        composable("imageDescription") {
            ImageDescriptionScreen(onBack = { navController.popBackStack() })
        }

        // Step 6 – Photo Capture
        composable("photoCapture") {
            PhotoCaptureScreen(onBack = { navController.popBackStack() })
        }

        // Step 7 – Task History
        composable("taskHistory") {
            TaskHistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
