
package com.example.stockgo.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stockgo.Data.MovimientoRepository
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO"))
    }

    // Cargar historial desde Firestore
    LaunchedEffect(Unit) {
        cargando = true
        errorCarga = ""

        try {
            val repositorio = MovimientoRepository()
            val historial = repositorio.obtenerMovimientos()

            movimientos.clear()
            movimientos.addAll(historial)
        } catch (e: Exception) {
            errorCarga = e.localizedMessage
                ?: "No fue posible cargar el historial."
        } finally {
            cargando = false
        }
    }

    // Filtrar movimientos
    val movimientosFiltrados = movimientos
        .filter { movimiento ->

            val coincideBusqueda =
                movimiento.nombreProducto.contains(
                    busqueda,
                    ignoreCase = true
                ) ||
                        movimiento.codigoProducto.contains(
                            busqueda,
                            ignoreCase = true
                        ) ||
                        movimiento.usuarioNombre.contains(
                            busqueda,
                            ignoreCase = true
                        )

            val coincideTipo = when (filtroSeleccionado) {
                "Entradas" -> movimiento.tipo == TipoMovimiento.ENTRADA
                "Salidas" -> movimiento.tipo == TipoMovimiento.SALIDA
                else -> true
            }

            coincideBusqueda && coincideTipo
        }
        .sortedByDescending { it.fecha }

    val totalEntradas = movimientos.count {
        it.tipo == TipoMovimiento.ENTRADA
    }

    val totalSalidas = movimientos.count {
        it.tipo == TipoMovimiento.SALIDA
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver a más opciones",
                tint = Color(0xFF3044B5),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onNavigate("Más")
                    }
            )

            Text(
                text = "Historial de movimientos",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2C365E),
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // Resumen de entradas y salidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Entradas")
                    Text(
                        text = totalEntradas.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF16803D)
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Salidas")
                    Text(
                        text = totalSalidas.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }

        // Búsqueda
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar movimiento") },
            placeholder = {
                Text("Producto, código o usuario")
            },
            singleLine = true
        )

        Text(
            text = "Filtrar por tipo",
            style = MaterialTheme.typography.titleMedium
        )

        // Filtros
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            val filtros = listOf("Todos", "Entradas", "Salidas")

            filtros.forEachIndexed { index, filtro ->
                SegmentedButton(
                    selected = filtroSeleccionado == filtro,
                    onClick = {
                        filtroSeleccionado = filtro
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = filtros.size
                    )
                ) {
                    Text(filtro)
                }
            }
        }

        Text(
            text = "Movimientos encontrados: ${movimientosFiltrados.size}",
            style = MaterialTheme.typography.titleSmall
        )

        if (cargando) {
            Text(
                text = "Cargando historial...",
                color = Color.Gray
            )
        } else if (errorCarga.isNotBlank()) {
            Text(
                text = "Error al cargar: $errorCarga",
                color = Color(0xFFC62828)
            )
        } else if (movimientosFiltrados.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No se encontraron movimientos.",
                color = Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = movimientosFiltrados,
                    key = { it.id }
                ) { movimiento ->

                    val esEntrada =
                        movimiento.tipo == TipoMovimiento.ENTRADA

                    val colorMovimiento = if (esEntrada) {
                        Color(0xFF16803D)
                    } else {
                        Color(0xFFC62828)
                    }

                    val tipoTexto = if (esEntrada) {
                        "ENTRADA"
                    } else {
                        "SALIDA"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = movimiento.nombreProducto,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = tipoTexto,
                                color = colorMovimiento,
                                style = MaterialTheme.typography.labelLarge
                            )

                            Text(
                                "Código: ${movimiento.codigoProducto}"
                            )

                            Text(
                                "Cantidad: ${movimiento.cantidad} unidades"
                            )

                            Text(
                                "Fecha: ${
                                    formatoFecha.format(
                                        Date(movimiento.fecha)
                                    )
                                }"
                            )

                            if (movimiento.usuarioNombre.isNotBlank()) {
                                Text(
                                    "Responsable: ${movimiento.usuarioNombre}"
                                )
                            }

                            if (movimiento.proveedor.isNotBlank()) {
                                Text(
                                    "Proveedor: ${movimiento.proveedor}"
                                )
                            }

                            if (movimiento.observaciones.isNotBlank()) {
                                Text(
                                    "Observaciones: ${movimiento.observaciones}"
                                )
                            }

                            Text(
                                text = "ID: ${movimiento.id}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
