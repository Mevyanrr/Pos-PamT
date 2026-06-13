package com.example.pos_pamt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun PosPamTTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary          = Teal,
            secondary        = Teal2,
            primaryContainer = Teal3,
            secondaryContainer = Teal4,
            background       = BgPage,
            surface          = White,
            error            = Danger,
            onPrimary        = White,
            onBackground     = TextDark,
            onSurface        = TextDark,
            onError          = White
        ),
        typography = Typography,
        content    = content
    )
}
