package com.ufpso.finanapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.data.model.TipoMovimiento
import com.ufpso.finanapp.ui.components.BotonVolver
import com.ufpso.finanapp.ui.components.DialogoEliminar
import com.ufpso.finanapp.ui.components.FilaDato
import com.ufpso.finanapp.ui.theme.ColorGasto
import com.ufpso.finanapp.ui.theme.ColorIngreso
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel
import com.ufpso.finanapp.util.formatearFecha
import com.ufpso.finanapp.util.formatearPesos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleMovimientoScreen(
    viewModel: FinanzasViewModel,
    idMovimiento: Long,
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit
) {
    val movimientos by viewModel.movimientos.collectAsState()
    val movimiento = movimientos.find { it.id == idMovimiento }
    var confirmarEliminar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle") }, navigationIcon = { BotonVolver(onVolver) })
        }
    ) { padding ->
        if (movimiento == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Movimiento no encontrado")
            }
            return@Scaffold
        }

        val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
        val color = if (esIngreso) ColorIngreso else ColorGasto

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(if (esIngreso) "INGRESO" else "GASTO", color = color, style = MaterialTheme.typography.labelLarge)
                    Text(
                        formatearPesos(movimiento.valor),
                        color = color,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    FilaDato("Descripción", movimiento.descripcion)
                    FilaDato("Categoría", movimiento.categoria)
                    FilaDato("Fecha", formatearFecha(movimiento.fecha))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onEditar(movimiento.id) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar")
                }
                OutlinedButton(
                    onClick = { confirmarEliminar = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Eliminar")
                }
            }
        }
    }

    if (confirmarEliminar && movimiento != null) {
        DialogoEliminar(
            movimiento = movimiento,
            onConfirmar = {
                viewModel.eliminar(movimiento)
                confirmarEliminar = false
                onVolver()
            },
            onCancelar = { confirmarEliminar = false }
        )
    }
}
