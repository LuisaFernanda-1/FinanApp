package com.ufpso.finanapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.data.model.Categorias
import com.ufpso.finanapp.data.model.Movimiento
import com.ufpso.finanapp.data.model.TipoMovimiento
import com.ufpso.finanapp.ui.components.BotonVolver
import com.ufpso.finanapp.ui.theme.ColorGasto
import com.ufpso.finanapp.ui.theme.ColorIngreso
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel
import com.ufpso.finanapp.util.formatearFecha
import com.ufpso.finanapp.util.formatearPesos
import com.ufpso.finanapp.util.hoyEnMillisUtc

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormularioMovimientoScreen(
    viewModel: FinanzasViewModel,
    idMovimiento: Long,
    onVolver: () -> Unit
) {
    val esEdicion = idMovimiento != 0L

    var tipo by rememberSaveable { mutableStateOf(TipoMovimiento.GASTO) }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableLongStateOf(hoyEnMillisUtc()) }
    var cargado by rememberSaveable { mutableStateOf(!esEdicion) }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // Si es edición, cargar los datos del movimiento
    LaunchedEffect(idMovimiento) {
        if (esEdicion && !cargado) {
            viewModel.obtenerPorId(idMovimiento)?.let { m ->
                tipo = m.tipo
                descripcion = m.descripcion
                valor = m.valor.toString()
                categoria = m.categoria
                fecha = m.fecha
            }
            cargado = true
        }
    }

    // Validaciones
    val valorNumero = valor.toLongOrNull()
    val errorDescripcion = intentoGuardar && descripcion.isBlank()
    val errorValor = intentoGuardar && (valorNumero == null || valorNumero <= 0)
    val errorCategoria = intentoGuardar && categoria.isBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar movimiento" else "Nuevo movimiento") },
                navigationIcon = { BotonVolver(onVolver) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Tipo de movimiento", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoMovimiento.entries.forEach { t ->
                    val colorTipo = if (t == TipoMovimiento.INGRESO) ColorIngreso else ColorGasto
                    FilterChip(
                        selected = tipo == t,
                        onClick = {
                            if (tipo != t) {
                                tipo = t
                                categoria = "" // las categorías cambian según el tipo
                            }
                        },
                        label = { Text(if (t == TipoMovimiento.INGRESO) "Ingreso" else "Gasto") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorTipo.copy(alpha = 0.25f)
                        )
                    )
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                isError = errorDescripcion,
                supportingText = { if (errorDescripcion) Text("La descripción es obligatoria") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { nuevo -> valor = nuevo.filter { it.isDigit() }.take(12) },
                label = { Text("Valor") },
                prefix = { Text("$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorValor,
                supportingText = {
                    when {
                        errorValor -> Text("El valor debe ser mayor que cero")
                        valorNumero != null -> Text(formatearPesos(valorNumero))
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Categoría", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Categorias.de(tipo).forEach { c ->
                    FilterChip(
                        selected = categoria == c,
                        onClick = { categoria = c },
                        label = { Text(c) }
                    )
                }
            }
            if (errorCategoria) {
                Text(
                    "Selecciona una categoría",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedButton(onClick = { mostrarCalendario = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Fecha: ${formatearFecha(fecha)}")
            }

            Button(
                onClick = {
                    intentoGuardar = true
                    val v = valor.toLongOrNull()
                    if (descripcion.isNotBlank() && v != null && v > 0 && categoria.isNotBlank()) {
                        viewModel.guardar(
                            Movimiento(
                                id = idMovimiento,
                                descripcion = descripcion.trim(),
                                valor = v,
                                categoria = categoria,
                                fecha = fecha,
                                tipo = tipo
                            )
                        )
                        onVolver()
                    }
                },
                enabled = cargado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar" else "Guardar")
            }
        }
    }

    if (mostrarCalendario) {
        val estadoFecha = rememberDatePickerState(initialSelectedDateMillis = fecha)
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let { fecha = it }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoFecha)
        }
    }
}
