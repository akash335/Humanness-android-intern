package com.humanness.sample

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun HoldToRecordButton(
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Button(
        onClick = { /* no-op, we use touch events instead */ },
        modifier = Modifier
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onStart()
                        tryAwaitRelease()
                        onStop()
                    }
                )
            }
    ) {
        Text("Hold to Record")
    }
}
