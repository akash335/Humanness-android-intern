package com.humanness.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onStartSampleTask: () -> Unit
) {
    val topBarBlue = Color(0xFF1976D2)   // same strong blue as your reference
    val buttonBlue = Color(0xFF1976D2)

    Scaffold(
        topBar = {
            Surface(
                color = topBarBlue,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(onClick = onToggleTheme) {
                        // Use emoji instead of drawable icons -> no missing resources
                        Text(
                            text = if (isDark) "☀️" else "🌙",
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Centered content block (English + Hindi) in the middle of the screen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Main heading: same style as reference
                    Text(
                        text = buildAnnotatedString {
                            append("Lets Start with a\n")
                            withStyle(
                                SpanStyle(
                                    color = buttonBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Sample Task\n")
                            }
                            append("for practice!")
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Hindi line below
                    Text(
                        text = "Pehele hum ek\nsample task karte h",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF8A8A8A)
                    )
                }
            }

            // Bottom button – fully visible
            Button(
                onClick = onStartSampleTask,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Start Sample Task",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
