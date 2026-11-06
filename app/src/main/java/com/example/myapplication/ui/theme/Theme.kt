package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Terracota,
    onPrimary = Color.White,
    secondary = AzulMarinho,
    onSecondary = Color.White,
    background = CremeFundo,
    onBackground = AzulMarinho,
    surface = BrancoCard,
    onSurface = AzulMarinho,
    error = VermelhoCartao,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
