package com.ufpso.finanapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.data.model.Categorias
import com.ufpso.finanapp.data.model.Movimiento
import com.ufpso.finanapp.data.model.TipoMovimiento
import com.ufpso.finanapp.ui.components.BotonVolver
import com.ufpso.finanapp.ui.components.DialogoEliminar
import com.ufpso.finanapp.ui.components.MovimientoItem
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    viewModel: FinanzasViewModel,
    onVolver: () -> Unit,
    onNuevo: () -> Unit,
    onDetalle: (Long) -> Unit,
    onEditar: (Long) -> Unit
) {
    val lista by viewModel.movimientosFiltrados.collectAsState()
    val filtroTipo by viewModel.filtroTipo.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()
    var porEliminar by remember { mutableStateOf<Movimiento?>(null) }

    val tipoActual = filtroTipo
    val categorias = if (tipoActual == null) Categorias.ingresos + Categorias.gastos
                     else Categorias.de(tipoActual)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movimientos") }, navigationIcon = { BotonVolver(onVolver) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevo) { Icon(Icons.Default.Add, contentDescription = "Nuevo") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filtro por tipo
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = filtroTipo == null, onClick = { viewModel.cambiarFiltroTipo(null) }, label = { Text("Todos") })
                FilterChip(
                    selected = filtroTipo == TipoMovimiento.INGRESO,
                    onClick = { viewModel.cambiarFiltroTipo(TipoMovimiento.INGRESO) },
                    label = { Text("Ingresos") }
                )
                FilterChip(
                    selected = filtroTipo == TipoMovimiento.GASTO,
                    onClick = { viewModel.cambiarFiltroTipo(TipoMovimiento.GASTO) },
                    label = { Text("Gastos") }
                )
            }
            // Filtro por categoría
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filtroCategoria == null,
                        onClick = { viewModel.cambiarFiltroCategoria(null) },
                        label = { Text("Todas") }
                    )
                }
                items(categorias) { c ->
                    FilterChip(
                        selected = filtroCategoria == c,
                        onClick = { viewModel.cambiarFiltroCategoria(if (filtroCategoria == c) null else c) },
                        label = { Text(c) }
                    )
                }
            }

            if (lista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay movimientos para mostrar")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista, key = { it.id }) { m ->
                        MovimientoItem(
                            movimiento = m,
                            onClick = { onDetalle(m.id) },
                            onEditar = { onEditar(m.id) },
                            onEliminar = { porEliminar = m }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    porEliminar?.let { m ->
        DialogoEliminar(
            movimiento = m,
            onConfirmar = {
                viewModel.eliminar(m)
                porEliminar = null
            },
            onCancelar = { porEliminar = null }
        )
    }
}
