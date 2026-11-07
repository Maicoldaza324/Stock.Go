
package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)
private val VerdeEntrada = Color(0xFF218838)
private val RojoSalida = Color(0xFFB3261E)
private val NaranjaStock = Color(0xFFE65100)

private enum class FiltroMovimiento(val etiqueta: String) {
    TODOS("Todos"),
    ENTRADAS("Entradas"),
    SALIDAS("Salidas")
}

@Composable
fun ReportsScreen(
    productos: SnapshotStateList<Producto>,
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    val totalProductos = productos.size
    val totalUnidades = productos.sumOf { it.stock }
    val productosSinStock = productos.count { it.stock == 0 }
    val productosStockBajo = productos.count {
        it.stock > 0 && it.stock <= it.stockMinimo
    }

    val totalEntradas = movimientos
        .filter { it.tipo == TipoMovimiento.ENTRADA }
        .sumOf { it.cantidad }

    val totalSalidas = movimientos
        .filter { it.tipo == TipoMovimiento.SALIDA }
        .sumOf { it.cantidad }

    val movimientosPorProducto = movimientos
        .groupBy { it.codigoProducto }
        .map { (_, lista) ->
            lista.first().nombreProducto to lista.sumOf { it.cantidad }
        }
        .sortedByDescending { it.second }
        .take(5)

    val inventarioPorCategoria = productos
        .groupBy { it.categoria.ifBlank { "Sin categoría" } }
        .map { (categoria, lista) ->
            categoria to lista.sumOf { it.stock }
        }
        .sortedByDescending { it.second }

    val maxMovimiento = movimientosPorProducto.maxOfOrNull { it.second } ?: 0
    val maxCategoria = inventarioPorCategoria.maxOfOrNull { it.second } ?: 0
    val maxEntradasSalidas = maxOf(totalEntradas, totalSalidas, 1)

    var filtroSeleccionado by remember {
        mutableStateOf(FiltroMovimiento.TODOS)
    }

    val movimientosFiltrados = movimientos
        .filter {
            when (filtroSeleccionado) {
                FiltroMovimiento.TODOS -> true
                FiltroMovimiento.ENTRADAS ->
                    it.tipo == TipoMovimiento.ENTRADA
                FiltroMovimiento.SALIDAS ->
                    it.tipo == TipoMovimiento.SALIDA
            }
        }
        .sortedByDescending { it.fecha }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Reportes e Inventario",
            color = AzulStock,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportCard(
                titulo = "Total productos",
                valor = "$totalProductos",
                modifier = Modifier.weight(1f)
            )

            ReportCard(
                titulo = "Unidades en stock",
                valor = "$totalUnidades",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportCard(
                titulo = "Agotados",
                valor = "$productosSinStock",
                colorTexto = RojoSalida,
                modifier = Modifier.weight(1f)
            )

            ReportCard(
                titulo = "Stock bajo",
                valor = "$productosStockBajo",
                colorTexto = NaranjaStock,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Gráficos estadísticos",
            color = AzulStock,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Gráfico de entradas y salidas
        ChartCard(title = "Entradas vs. salidas") {
            Text(
                text = "Unidades registradas",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChartBar(
                titulo = "Entradas",
                valor = totalEntradas,
                maximo = maxEntradasSalidas,
                color = VerdeEntrada
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChartBar(
                titulo = "Salidas",
                valor = totalSalidas,
                maximo = maxEntradasSalidas,
                color = RojoSalida
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Total de unidades movilizadas: ${totalEntradas + totalSalidas}",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de productos con mayor movimiento
        ChartCard(title = "Productos con mayor movimiento") {
            if (movimientosPorProducto.isEmpty()) {
                EmptyChartMessage("Todavía no hay movimientos registrados.")
            } else {
                movimientosPorProducto.forEachIndexed { index, producto ->
                    ChartBar(
                        titulo = "${index + 1}. ${producto.first}",
                        valor = producto.second,
                        maximo = maxMovimiento,
                        color = AzulStock
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de inventario por categorías
        ChartCard(title = "Inventario por categorías") {
            if (inventarioPorCategoria.isEmpty()) {
                EmptyChartMessage("No hay productos para mostrar.")
            } else {
                inventarioPorCategoria.forEach { categoria ->
                    ChartBar(
                        titulo = categoria.first,
                        valor = categoria.second,
                        maximo = maxCategoria,
                        color = Color(0xFF7B61A8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumen de movimientos",
            color = AzulStock,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Entradas registradas: $totalEntradas unidades",
                    fontSize = 16.sp,
                    color = VerdeEntrada
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Salidas registradas: $totalSalidas unidades",
                    fontSize = 16.sp,
                    color = RojoSalida
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Total de movimientos: ${movimientos.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Historial de movimientos",
            color = AzulStock,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroMovimiento.entries.forEach { filtro ->
                FilterChip(
                    selected = filtroSeleccionado == filtro,
                    onClick = { filtroSeleccionado = filtro },
                    label = { Text(filtro.etiqueta) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (movimientosFiltrados.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sin movimientos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulStock
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (filtroSeleccionado) {
                            FiltroMovimiento.TODOS ->
                                "Todavía no hay movimientos registrados."
                            FiltroMovimiento.ENTRADAS ->
                                "Todavía no hay entradas registradas."
                            FiltroMovimiento.SALIDAS ->
                                "Todavía no hay salidas registradas."
                        },
                        color = Color.Gray
                    )
                }
            }
        } else {
            movimientosFiltrados.forEach { movimiento ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = movimiento.nombreProducto,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            val esEntrada =
                                movimiento.tipo == TipoMovimiento.ENTRADA

                            Surface(
                                color = if (esEntrada) {
                                    Color(0xFFE4F5E8)
                                } else {
                                    Color(0xFFFCE8E6)
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = if (esEntrada) "ENTRADA" else "SALIDA",
                                    color = if (esEntrada) VerdeEntrada else RojoSalida,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 5.dp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Código: ${movimiento.codigoProducto}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "Cantidad: ${movimiento.cantidad} unidades",
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Fecha: ${
                                SimpleDateFormat(
                                    "dd/MM/yyyy HH:mm",
                                    Locale.getDefault()
                                ).format(Date(movimiento.fecha))
                            }",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )

                        if (movimiento.proveedor.isNotBlank()) {
                            Text(
                                text = "Proveedor: ${movimiento.proveedor}",
                                fontSize = 13.sp
                            )
                        }

                        if (movimiento.observaciones.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Observaciones: ${movimiento.observaciones}",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onNavigate("Inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AzulStock
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ChartBar(
    titulo: String,
    valor: Int,
    maximo: Int,
    color: Color
) {
    val porcentaje = if (maximo > 0) {
        (valor.toFloat() / maximo.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$valor",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .background(
                    color = Color(0xFFE8EAF0),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje)
                    .fillMaxHeight()
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}

@Composable
private fun EmptyChartMessage(mensaje: String) {
    Text(
        text = mensaje,
        color = Color.Gray,
        fontSize = 14.sp
    )
}

@Composable
fun ReportCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    colorTexto: Color = AzulStock
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = valor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
        }
    }
}
