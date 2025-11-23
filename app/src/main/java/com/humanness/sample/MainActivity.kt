package com.humanness.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // App-wide dark / light toggle
            var isDark by rememberSaveable { mutableStateOf(false) }

            HumannessTheme(darkTheme = isDark) {
                AppRoot(
                    isDark = isDark,
                    onToggleTheme = { isDark = !isDark }
                )
            }
        }
    }
}
