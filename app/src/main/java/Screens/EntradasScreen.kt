package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

private val AzulEntradas = Color(0xFF3044B5)
private val FondoEntradas = Color(0xFFF5F7FB)

@Composable
fun EntradasScreen(
    productos: SnapshotStateList<Producto>,
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    var productoSeleccionado by remember {
        mutableStateOf<Producto?>(null)
    }
    var cantidad by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoEntradas)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Registrar entrada",
            color = AzulEntradas,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registra las unidades que ingresan al inventario."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Producto")

                Button(
                    onClick = { menuAbierto = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        productoSeleccionado?.let {
                            "${it.codigo} - ${it.nombre}"
                        } ?: "Seleccionar producto"
                    )
                }

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = { menuAbierto = false }
                ) {
                    productos.forEach { producto ->
                        DropdownMenuItem(
                            text = {
                                Text("${producto.codigo} - ${producto.nombre}")
                            },
                            onClick = {
                                productoSeleccionado = producto
                                menuAbierto = false
                                mensaje = ""
                            }
                        )
                    }
                }

                productoSeleccionado?.let {
                    Text("Stock actual: ${it.stock} unidades")
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
                    label = { Text("Cantidad que ingresa") },
                    placeholder = { Text("Ej. 10") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Observaciones (opcional)") },
                    placeholder = { Text("Ej. Compra a proveedor") }
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
                                val indice = productos.indexOf(producto)

                                if (indice >= 0) {
                                    productos[indice] = producto.copy(
                                        stock = producto.stock + unidades
                                    )

                                    movimientos.add(
                                        MovimientoInventario(
                                            id = "MOV-${(movimientos.size + 1).toString().padStart(3, '0')}",
                                            codigoProducto = producto.codigo,
                                            nombreProducto = producto.nombre,
                                            tipo = TipoMovimiento.ENTRADA,
                                            cantidad = unidades,
                                            fecha = System.currentTimeMillis(),
                                            observaciones = observaciones.trim()
                                        )
                                    )

                                    mensaje = "Entrada registrada correctamente."
                                    cantidad = ""
                                    observaciones = ""
                                    productoSeleccionado = null
                                } else {
                                    mensaje = "El producto ya no está disponible."
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar entrada")
                }

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = if (
                            mensaje == "Entrada registrada correctamente."
                        ) {
                            Color(0xFF16803D)
                        } else {
                            Color(0xFFB3261E)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Últimas entradas",
            color = AzulEntradas,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        val ultimasEntradas = movimientos
            .filter { it.tipo == TipoMovimiento.ENTRADA }
            .asReversed()
            .take(10)

        if (ultimasEntradas.isEmpty()) {
            Text("Todavía no hay entradas registradas.")
        } else {
            ultimasEntradas.forEach { movimiento ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(movimiento.nombreProducto)
                        Text("Código: ${movimiento.codigoProducto}")
                        Text("Cantidad ingresada: ${movimiento.cantidad}")

                        if (movimiento.observaciones.isNotBlank()) {
                            Text("Observaciones: ${movimiento.observaciones}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onNavigate("Inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}