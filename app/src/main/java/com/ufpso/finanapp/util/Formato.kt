package com.ufpso.finanapp.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

private val localeCO: Locale = Locale.forLanguageTag("es-CO")

fun formatearPesos(valor: Long): String {
    val texto = "$" + String.format(localeCO, "%,d", abs(valor))
    return if (valor < 0) "-$texto" else texto
}

fun formatearFecha(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", localeCO)
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(millis))
}

// Fecha de hoy a medianoche UTC (el DatePicker trabaja en UTC)
fun hoyEnMillisUtc(): Long {
    val local = Calendar.getInstance()
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    return utc.timeInMillis
}
