package com.ufpso.finanapp.data.repository

import com.ufpso.finanapp.data.local.MovimientoDao
import com.ufpso.finanapp.data.model.Movimiento
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(private val dao: MovimientoDao) {

    fun obtenerTodos(): Flow<List<Movimiento>> = dao.obtenerTodos()

    suspend fun obtenerPorId(id: Long): Movimiento? = dao.obtenerPorId(id)

    // Crear si es nuevo (id = 0), actualizar si ya existe
    suspend fun guardar(movimiento: Movimiento) {
        if (movimiento.id == 0L) dao.insertar(movimiento) else dao.actualizar(movimiento)
    }

    suspend fun eliminar(movimiento: Movimiento) = dao.eliminar(movimiento)
}
