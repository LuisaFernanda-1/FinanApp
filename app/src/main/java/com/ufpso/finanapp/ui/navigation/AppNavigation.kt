package com.ufpso.finanapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ufpso.finanapp.ui.screens.*
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel

@Composable
fun AppNavigation(viewModel: FinanzasViewModel) {
    // La pila de navegación es una lista que nosotros controlamos
    val backStack = rememberNavBackStack(Dashboard)
    val volver: () -> Unit = {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    NavDisplay(
        backStack = backStack,
        onBack = volver,
        entryProvider = entryProvider {
            entry<Dashboard> {
                DashboardScreen(
                    viewModel = viewModel,
                    onNuevo = { backStack.add(Formulario()) },
                    onVerMovimientos = { backStack.add(ListaMovimientos) },
                    onEstadisticas = { backStack.add(Estadisticas) },
                    onDetalle = { id -> backStack.add(Detalle(id)) }
                )
            }
            entry<ListaMovimientos> {
                MovimientosScreen(
                    viewModel = viewModel,
                    onVolver = volver,
                    onNuevo = { backStack.add(Formulario()) },
                    onDetalle = { id -> backStack.add(Detalle(id)) },
                    onEditar = { id -> backStack.add(Formulario(id)) }
                )
            }
            entry<Formulario> { ruta ->
                FormularioMovimientoScreen(viewModel = viewModel, idMovimiento = ruta.id, onVolver = volver)
            }
            entry<Detalle> { ruta ->
                DetalleMovimientoScreen(
                    viewModel = viewModel,
                    idMovimiento = ruta.id,
                    onVolver = volver,
                    onEditar = { id -> backStack.add(Formulario(id)) }
                )
            }
            entry<Estadisticas> {
                EstadisticasScreen(viewModel = viewModel, onVolver = volver)
            }
        }
    )
}
