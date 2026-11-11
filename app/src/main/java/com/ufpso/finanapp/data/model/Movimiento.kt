package com.ufpso.finanapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoMovimiento { INGRESO, GASTO }

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descripcion: String,
    val valor: Long,
    val categoria: String,
    val fecha: Long,          // milisegundos (UTC)
    val tipo: TipoMovimiento
)

data class ResumenFinanciero(
    val totalIngresos: Long = 0,
    val totalGastos: Long = 0
) {
    val balance: Long get() = totalIngresos - totalGastos
}

object Categorias {
    val ingresos = listOf("Salario", "Ventas", "Freelance", "Inversiones", "Otros ingresos")
    val gastos = listOf(
        "Alimentación", "Transporte", "Vivienda", "Servicios",
        "Salud", "Educación", "Entretenimiento", "Otros gastos"
    )
    fun de(tipo: TipoMovimiento) = if (tipo == TipoMovimiento.INGRESO) ingresos else gastos
}
