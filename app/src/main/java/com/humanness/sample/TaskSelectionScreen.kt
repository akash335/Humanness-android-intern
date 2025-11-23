package com.humanness.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSelectionScreen(
    onBack: () -> Unit,
    onTextReading: () -> Unit,
    onImageDescription: () -> Unit,
    onPhotoCapture: () -> Unit,
    onViewHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Choose a task",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryFullWidthButton(
                label = "Text reading task",
                onClick = onTextReading
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryFullWidthButton(
                label = "Image description task",
                onClick = onImageDescription
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryFullWidthButton(
                label = "Photo + description task",
                onClick = onPhotoCapture
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryFullWidthButton(
                label = "View submitted tasks",
                onClick = onViewHistory
            )
        }
    }
}

@Composable
private fun PrimaryFullWidthButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
