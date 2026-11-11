package com.ufpso.finanapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.ui.components.*
import com.ufpso.finanapp.ui.theme.ColorGasto
import com.ufpso.finanapp.ui.theme.ColorIngreso
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FinanzasViewModel,
    onNuevo: () -> Unit,
    onVerMovimientos: () -> Unit,
    onEstadisticas: () -> Unit,
    onDetalle: (Long) -> Unit
) {
    val resumen by viewModel.resumen.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("FinanApp", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevo,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo movimiento") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { TarjetaBalance(resumen) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaTotal("Ingresos", resumen.totalIngresos, ColorIngreso, Modifier.weight(1f))
                    TarjetaTotal("Gastos", resumen.totalGastos, ColorGasto, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onVerMovimientos, modifier = Modifier.weight(1f)) {
                        Text("Movimientos")
                    }
                    OutlinedButton(onClick = onEstadisticas, modifier = Modifier.weight(1f)) {
                        Text("Estadísticas")
                    }
                }
            }
            item {
                Text("Últimos movimientos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (movimientos.isEmpty()) {
                item { Text("Aún no hay movimientos registrados.") }
            } else {
                items(movimientos.take(5), key = { it.id }) { movimiento ->
                    MovimientoItem(movimiento = movimiento, onClick = { onDetalle(movimiento.id) })
                }
            }
            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}
