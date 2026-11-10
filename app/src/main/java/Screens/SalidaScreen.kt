
package com.example.stockgo.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stockgo.Data.MovimientoRepository
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalidasScreen(
    productos: SnapshotStateList<Producto>,
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }

    var cantidad by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("Venta") }
    var observaciones by remember { mutableStateOf("") }
    var menuProductosAbierto by remember { mutableStateOf(false) }
    var menuMotivosAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    val repository = remember { MovimientoRepository() }
    val scope = rememberCoroutineScope()

    val motivos = listOf(
        "Venta",
        "Uso interno",
        "Dañado / Expirado",
        "Otro"
    )

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CO"))
    }

    // Recuperar historial desde Firebase
    LaunchedEffect(Unit) {
        try {
            val historial = repository.obtenerMovimientos()
            movimientos.clear()
            movimientos.addAll(historial)
        } catch (e: Exception) {
            mensaje = "Error cargando historial: ${e.localizedMessage}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Registrar salida",
            color = Color(0xFF2C365E),
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Registra las unidades que salen del inventario.")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFBE9E7)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Salida de inventario",
                    color = Color(0xFFC62828),
                    style = MaterialTheme.typography.titleMedium
                )
                Text("El stock del producto disminuirá.")
            }
        }

        Text("Producto")

        Button(
            onClick = { menuProductosAbierto = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                productoSeleccionado?.let {
                    "${it.codigo} - ${it.nombre}"
                } ?: "Seleccionar producto"
            )
        }

        DropdownMenu(
            expanded = menuProductosAbierto,
            onDismissRequest = { menuProductosAbierto = false }
        ) {
            productos.forEach { producto ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${producto.codigo} - ${producto.nombre} " +
                                    "(Stock: ${producto.stock})"
                        )
                    },
                    onClick = {
                        productoSeleccionado = producto
                        menuProductosAbierto = false
                        mensaje = ""
                    }
                )
            }
        }

        if (productos.isEmpty()) {
            Text(
                text = "No hay productos registrados. Primero agrega productos.",
                color = Color(0xFFB3261E)
            )
        }

        productoSeleccionado?.let {
            Text("Stock disponible: ${it.stock} unidades")
        }

        OutlinedTextField(
            value = cantidad,
            onValueChange = { valor ->
                if (valor.all { it.isDigit() } && valor.length <= 7) {
                    cantidad = valor
                    mensaje = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cantidad que sale") },
            placeholder = { Text("Ej. 5") },
            singleLine = true
        )

        Text("Motivo")

        Button(
            onClick = { menuMotivosAbierto = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(motivo)
        }

        DropdownMenu(
            expanded = menuMotivosAbierto,
            onDismissRequest = { menuMotivosAbierto = false }
        ) {
            motivos.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        motivo = opcion
                        menuMotivosAbierto = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Observaciones (opcional)") },
            placeholder = { Text("Notas adicionales...") }
        )

        Button(
            onClick = {
                val producto = productoSeleccionado
                val unidades = cantidad.toIntOrNull()

                when {
                    producto == null -> {
                        mensaje = "Selecciona un producto."
                    }

                    unidades == null || unidades <= 0 -> {
                        mensaje = "Ingresa una cantidad mayor que cero."
                    }

                    else -> {
                        val indice = productos.indexOfFirst {
                            it.codigo == producto.codigo
                        }

                        if (indice == -1) {
                            mensaje = "El producto ya no está disponible."
                        } else {
                            guardando = true

                            scope.launch {
                                try {
                                    val detalleObservaciones =
                                        "Motivo: $motivo" +
                                                if (observaciones.isBlank()) {
                                                    ""
                                                } else {
                                                    " - ${observaciones.trim()}"
                                                }

                                    val movimiento =
                                        repository.registrarMovimiento(
                                            producto = producto,
                                            tipo = TipoMovimiento.SALIDA,
                                            cantidad = unidades,
                                            observaciones = detalleObservaciones
                                        )

                                    val indiceActual = productos.indexOfFirst {
                                        it.codigo == producto.codigo
                                    }

                                    if (indiceActual >= 0) {
                                        productos[indiceActual] =
                                            productos[indiceActual].copy(
                                                stock = productos[indiceActual].stock - unidades
                                            )
                                    }

                                    movimientos.add(movimiento)

                                    mensaje = "Salida registrada correctamente."
                                    cantidad = ""
                                    observaciones = ""
                                    productoSeleccionado = null
                                    motivo = "Venta"

                                } catch (e: Exception) {
                                    mensaje = "Error al registrar: ${e.localizedMessage}"
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !guardando
        ) {
            if (guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text("Registrar salida")
            }
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (mensaje == "Salida registrada correctamente.") {
                    Color(0xFF16803D)
                } else {
                    Color(0xFFB3261E)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Últimas salidas",
            color = Color(0xFF2C365E),
            style = MaterialTheme.typography.titleLarge
        )

        val ultimasSalidas = movimientos
            .filter { it.tipo == TipoMovimiento.SALIDA }
            .sortedByDescending { it.fecha }
            .take(10)

        if (ultimasSalidas.isEmpty()) {
            Text("Todavía no hay salidas registradas.")
        } else {
            ultimasSalidas.forEach { movimiento ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
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

                        Text("Código: ${movimiento.codigoProducto}")
                        Text("Cantidad retirada: ${movimiento.cantidad}")

                        Text(
                            "Fecha: ${
                                formatoFecha.format(Date(movimiento.fecha))
                            }"
                        )

                        if (movimiento.usuarioNombre.isNotBlank()) {
                            Text("Registrado por: ${movimiento.usuarioNombre}")
                        }

                        if (movimiento.observaciones.isNotBlank()) {
                            Text(movimiento.observaciones)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { onNavigate("Inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}
