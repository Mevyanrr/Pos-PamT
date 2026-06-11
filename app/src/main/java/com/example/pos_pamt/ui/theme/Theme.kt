package com.example.pos_pamt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PosPamTTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary          = Color(0xFF00B5A3),
            secondary        = Color(0xFF00CDB9),
            primaryContainer = Color(0xFFE0FAF7),
            background       = Color(0xFFF2F6F8),
            surface          = Color(0xFFFFFFFF),
            error            = Color(0xFFEF4444)
        ),
        content = content
    )
}
