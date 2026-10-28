package com.example.stockgo.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento

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
    var registroExitoso by remember { mutableStateOf(false) }

    val motivos = listOf(
        "Venta",
        "Uso interno",
        "Dañado / Expirado",
        "Otro"
    )

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
                        registroExitoso = false
                    }

                    unidades == null || unidades <= 0 -> {
                        mensaje = "Ingresa una cantidad mayor que cero."
                        registroExitoso = false
                    }

                    unidades > producto.stock -> {
                        mensaje = "No hay existencias suficientes."
                        registroExitoso = false
                    }

                    else -> {
                        val indice = productos.indexOfFirst {
                            it.codigo == producto.codigo
                        }

                        if (indice == -1) {
                            mensaje = "El producto ya no está disponible."
                            registroExitoso = false
                        } else {
                            val productoActual = productos[indice]

                            if (unidades > productoActual.stock) {
                                mensaje = "No hay existencias suficientes."
                                registroExitoso = false
                            } else {
                                productos[indice] = productoActual.copy(
                                    stock = productoActual.stock - unidades
                                )

                                movimientos.add(
                                    MovimientoInventario(
                                        id = "MOV-${(movimientos.size + 1)
                                            .toString().padStart(3, '0')}",
                                        codigoProducto = productoActual.codigo,
                                        nombreProducto = productoActual.nombre,
                                        tipo = TipoMovimiento.SALIDA,
                                        cantidad = unidades,
                                        fecha = System.currentTimeMillis(),
                                        observaciones = "Motivo: $motivo" +
                                                if (observaciones.isBlank()) {
                                                    ""
                                                } else {
                                                    " - ${observaciones.trim()}"
                                                }
                                    )
                                )

                                mensaje = "Salida registrada correctamente."
                                registroExitoso = true
                                cantidad = ""
                                observaciones = ""
                                productoSeleccionado = null
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar salida")
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (registroExitoso) {
                    Color(0xFF16803D)
                } else {
                    Color(0xFFB3261E)
                }
            )
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