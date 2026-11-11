package com.ufpso.finanapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorIngreso = Color(0xFF2E7D32)
val ColorGasto = Color(0xFFC62828)

private val EsquemaClaro = lightColorScheme(primary = Color(0xFF1565C0), secondary = Color(0xFF00897B))
private val EsquemaOscuro = darkColorScheme(primary = Color(0xFF90CAF9), secondary = Color(0xFF80CBC4))

@Composable
fun FinanAppTema(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}
