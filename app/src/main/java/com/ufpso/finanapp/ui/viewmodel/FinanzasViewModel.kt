package com.ufpso.finanapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ufpso.finanapp.data.model.Movimiento
import com.ufpso.finanapp.data.model.ResumenFinanciero
import com.ufpso.finanapp.data.model.TipoMovimiento
import com.ufpso.finanapp.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanzasViewModel(private val repositorio: MovimientoRepository) : ViewModel() {

    // Lista completa (se actualiza sola cuando cambia la BD)
    val movimientos: StateFlow<List<Movimiento>> = repositorio.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // RF04: cálculo automático del balance
    val resumen: StateFlow<ResumenFinanciero> = movimientos.map { lista ->
        ResumenFinanciero(
            totalIngresos = lista.filter { it.tipo == TipoMovimiento.INGRESO }.sumOf { it.valor },
            totalGastos = lista.filter { it.tipo == TipoMovimiento.GASTO }.sumOf { it.valor }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResumenFinanciero())

    // RF05: gastos agrupados por categoría, de mayor a menor
    val gastosPorCategoria: StateFlow<List<Pair<String, Long>>> = movimientos.map { lista ->
        lista.filter { it.tipo == TipoMovimiento.GASTO }
            .groupBy { it.categoria }
            .map { (categoria, items) -> categoria to items.sumOf { it.valor } }
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // RF03: filtros
    private val _filtroTipo = MutableStateFlow<TipoMovimiento?>(null)
    val filtroTipo: StateFlow<TipoMovimiento?> = _filtroTipo.asStateFlow()

    private val _filtroCategoria = MutableStateFlow<String?>(null)
    val filtroCategoria: StateFlow<String?> = _filtroCategoria.asStateFlow()

    val movimientosFiltrados: StateFlow<List<Movimiento>> =
        combine(movimientos, _filtroTipo, _filtroCategoria) { lista, tipo, categoria ->
            lista.filter {
                (tipo == null || it.tipo == tipo) && (categoria == null || it.categoria == categoria)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cambiarFiltroTipo(tipo: TipoMovimiento?) {
        _filtroTipo.value = tipo
        _filtroCategoria.value = null
    }

    fun cambiarFiltroCategoria(categoria: String?) {
        _filtroCategoria.value = categoria
    }

    // CRUD
    suspend fun obtenerPorId(id: Long): Movimiento? = repositorio.obtenerPorId(id)

    fun guardar(movimiento: Movimiento) {
        viewModelScope.launch { repositorio.guardar(movimiento) }
    }

    fun eliminar(movimiento: Movimiento) {
        viewModelScope.launch { repositorio.eliminar(movimiento) }
    }

    class Factory(private val repositorio: MovimientoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FinanzasViewModel(repositorio) as T
    }
}
