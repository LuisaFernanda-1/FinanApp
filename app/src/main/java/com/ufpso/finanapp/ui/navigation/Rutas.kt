package com.ufpso.finanapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Dashboard : NavKey
@Serializable data object ListaMovimientos : NavKey
@Serializable data object Estadisticas : NavKey
@Serializable data class Formulario(val id: Long = 0L) : NavKey   // 0 = nuevo
@Serializable data class Detalle(val id: Long) : NavKey
