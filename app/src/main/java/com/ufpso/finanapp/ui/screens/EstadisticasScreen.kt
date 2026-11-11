package com.ufpso.finanapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.ui.components.*
import com.ufpso.finanapp.ui.theme.ColorGasto
import com.ufpso.finanapp.ui.theme.ColorIngreso
import com.ufpso.finanapp.ui.viewmodel.FinanzasViewModel
import com.ufpso.finanapp.util.formatearPesos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(viewModel: FinanzasViewModel, onVolver: () -> Unit) {
    val resumen by viewModel.resumen.collectAsState()
    val gastosCategoria by viewModel.gastosPorCategoria.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Estadísticas") }, navigationIcon = { BotonVolver(onVolver) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        FilaDato("Total ingresos", formatearPesos(resumen.totalIngresos), ColorIngreso)
                        FilaDato("Total gastos", formatearPesos(resumen.totalGastos), ColorGasto)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        FilaDato(
                            "Balance",
                            formatearPesos(resumen.balance),
                            if (resumen.balance >= 0) ColorIngreso else ColorGasto
                        )
                    }
                }
            }
            // Gráfico ingresos vs gastos
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ingresos vs. gastos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        if (resumen.totalIngresos + resumen.totalGastos == 0L) {
                            Text("Sin datos todavía")
                        } else {
                            GraficoDona(
                                valores = listOf(
                                    resumen.totalIngresos to ColorIngreso,
                                    resumen.totalGastos to ColorGasto
                                ),
                                modifier = Modifier.size(180.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Leyenda("Ingresos", ColorIngreso)
                                Leyenda("Gastos", ColorGasto)
                            }
                        }
                    }
                }
            }
            // Gastos por categoría
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Gastos por categoría", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (gastosCategoria.isEmpty()) {
                            Text("No hay gastos registrados")
                        } else {
                            val (mayorCategoria, mayorValor) = gastosCategoria.first()
                            Text(
                                "Categoría con mayor gasto: $mayorCategoria (${formatearPesos(mayorValor)})",
                                color = ColorGasto,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            gastosCategoria.forEachIndexed { i, (categoria, total) ->
                                BarraCategoria(
                                    nombre = categoria,
                                    valor = total,
                                    maximo = mayorValor,
                                    porcentaje = if (resumen.totalGastos > 0) total * 100f / resumen.totalGastos else 0f,
                                    color = ColoresGrafico[i % ColoresGrafico.size]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Leyenda(texto: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall)
    }
}
