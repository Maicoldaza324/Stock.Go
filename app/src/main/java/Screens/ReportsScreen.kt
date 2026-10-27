package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun ReportsScreen(
    productos: SnapshotStateList<Producto>,
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    val totalProductos = productos.size
    val totalUnidades = productos.sumOf { it.stock }
    val productosSinStock = productos.count { it.stock == 0 }
    val productosStockBajo = productos.count { it.stock in 1..it.stockMinimo }

    val totalEntradas = movimientos.filter { it.tipo == TipoMovimiento.ENTRADA }.sumOf { it.cantidad }
    val totalSalidas = movimientos.filter { it.tipo == TipoMovimiento.SALIDA }.sumOf { it.cantidad }

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

        // Tarjetas de Resumen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportCard(
                titulo = "Total Productos",
                valor = "$totalProductos",
                modifier = Modifier.weight(1f)
            )
            ReportCard(
                titulo = "Unidades Stock",
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
                colorTexto = Color(0xFFB3261E),
                modifier = Modifier.weight(1f)
            )
            ReportCard(
                titulo = "Stock Bajo",
                valor = "$productosStockBajo",
                colorTexto = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Resumen de Movimientos",
            color = AzulStock,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Entradas registradas: $totalEntradas unidades", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Salidas registradas: $totalSalidas unidades", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Total movimientos: ${movimientos.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNavigate("Inicio") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AzulStock)
        ) {
            Text("Volver al Inicio")
        }
    }
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorTexto)
        }
    }
}

