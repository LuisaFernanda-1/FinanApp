package com.ufpso.finanapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ufpso.finanapp.data.model.Movimiento
import com.ufpso.finanapp.data.model.ResumenFinanciero
import com.ufpso.finanapp.data.model.TipoMovimiento
import com.ufpso.finanapp.ui.theme.ColorGasto
import com.ufpso.finanapp.ui.theme.ColorIngreso
import com.ufpso.finanapp.util.formatearFecha
import com.ufpso.finanapp.util.formatearPesos

val ColoresGrafico = listOf(
    Color(0xFFC62828), Color(0xFFEF6C00), Color(0xFFF9A825), Color(0xFF6A1B9A),
    Color(0xFF1565C0), Color(0xFF00838F), Color(0xFF558B2F), Color(0xFF5D4037)
)

@Composable
fun BotonVolver(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
    }
}

@Composable
fun TarjetaBalance(resumen: ResumenFinanciero) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Balance disponible",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                formatearPesos(resumen.balance),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TarjetaTotal(titulo: String, valor: Long, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(
                formatearPesos(valor),
                color = color,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MovimientoItem(
    movimiento: Movimiento,
    onClick: () -> Unit,
    onEditar: (() -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) ColorIngreso else ColorGasto

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movimiento.descripcion,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${movimiento.categoria} · ${formatearFecha(movimiento.fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    (if (esIngreso) "+ " else "- ") + formatearPesos(movimiento.valor),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            if (onEditar != null) {
                IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            }
            if (onEliminar != null) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DialogoEliminar(movimiento: Movimiento, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Eliminar movimiento") },
        text = {
            Text("¿Seguro que deseas eliminar \"${movimiento.descripcion}\" por ${formatearPesos(movimiento.valor)}?")
        },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
fun FilaDato(etiqueta: String, valor: String, color: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontWeight = FontWeight.SemiBold, color = color)
    }
}

// Gráfico de dona dibujado con Canvas
@Composable
fun GraficoDona(valores: List<Pair<Long, Color>>, modifier: Modifier = Modifier) {
    val total = valores.sumOf { it.first }.toFloat()
    Canvas(modifier = modifier) {
        val grosor = size.minDimension * 0.18f
        val tamanoArco = Size(size.minDimension - grosor, size.minDimension - grosor)
        val esquina = Offset((size.width - tamanoArco.width) / 2, (size.height - tamanoArco.height) / 2)
        var inicio = -90f
        valores.forEach { (valor, color) ->
            val barrido = if (total > 0f) valor / total * 360f else 0f
            drawArc(
                color = color,
                startAngle = inicio,
                sweepAngle = barrido,
                useCenter = false,
                topLeft = esquina,
                size = tamanoArco,
                style = Stroke(width = grosor)
            )
            inicio += barrido
        }
    }
}

// Barra horizontal para cada categoría
@Composable
fun BarraCategoria(nombre: String, valor: Long, maximo: Long, porcentaje: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${formatearPesos(valor)} (${"%.1f".format(porcentaje)}%)",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (maximo > 0) valor.toFloat() / maximo else 0f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(color)
            )
        }
    }
}
