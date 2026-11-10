
package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stockgo.Data.MovimientoRepository
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento
import kotlinx.coroutines.launch

private val AzulEntradas = Color(0xFF3044B5)
private val FondoEntradas = Color(0xFFF5F7FB)

@Composable
fun EntradasScreen(
    productos: SnapshotStateList<Producto>,
    movimientos: SnapshotStateList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    var menuProductosAbierto by remember { mutableStateOf(false) }
    var menuProveedoresAbierto by remember { mutableStateOf(false) }

    var mensaje by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    val repository = remember { MovimientoRepository() }
    val scope = rememberCoroutineScope()

    val proveedores = remember {
        mutableStateListOf<String>()
    }

    // Recuperar historial desde Firebase
    LaunchedEffect(Unit) {
        try {
            val historial = repository.obtenerMovimientos()
            movimientos.clear()
            movimientos.addAll(historial.filter {
                it.tipo == TipoMovimiento.ENTRADA
            })

            historial.filter {
                it.tipo == TipoMovimiento.ENTRADA
            }.map { it.proveedor }
                .filter { it.isNotBlank() }
                .distinct()
                .forEach {
                    if (!proveedores.contains(it)) proveedores.add(it)
                }
        } catch (e: Exception) {
            mensaje = "Error cargando historial: ${e.localizedMessage}"
        }
    }

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
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Registra las unidades que ingresan al inventario.")

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
                    onDismissRequest = {
                        menuProductosAbierto = false
                    }
                ) {
                    productos.forEach { producto ->
                        DropdownMenuItem(
                            text = {
                                Text("${producto.codigo} - ${producto.nombre}")
                            },
                            onClick = {
                                productoSeleccionado = producto
                                menuProductosAbierto = false
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

                Text("Proveedor")

                OutlinedTextField(
                    value = proveedor,
                    onValueChange = {
                        proveedor = it
                        mensaje = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre del proveedor") },
                    placeholder = { Text("Ej. Distribuidora Central") },
                    singleLine = true
                )

                if (proveedores.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                menuProveedoresAbierto = true
                            }
                        ) {
                            Text("Seleccionar proveedor existente")
                        }

                        DropdownMenu(
                            expanded = menuProveedoresAbierto,
                            onDismissRequest = {
                                menuProveedoresAbierto = false
                            }
                        ) {
                            proveedores.forEach { nombreProveedor ->
                                DropdownMenuItem(
                                    text = { Text(nombreProveedor) },
                                    onClick = {
                                        proveedor = nombreProveedor
                                        menuProveedoresAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }

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
                        val nombreProveedor = proveedor.trim()

                        when {
                            producto == null -> {
                                mensaje = "Selecciona un producto."
                            }

                            unidades == null || unidades <= 0 -> {
                                mensaje = "Ingresa una cantidad mayor que cero."
                            }

                            nombreProveedor.isBlank() -> {
                                mensaje = "Ingresa o selecciona un proveedor."
                            }

                            else -> {
                                guardando = true

                                scope.launch {
                                    try {
                                        val movimiento =
                                            repository.registrarMovimiento(
                                                producto = producto,
                                                tipo = TipoMovimiento.ENTRADA,
                                                cantidad = unidades,
                                                observaciones = observaciones.trim(),
                                                proveedor = nombreProveedor
                                            )

                                        val indice = productos.indexOfFirst {
                                            it.codigo == producto.codigo
                                        }

                                        if (indice >= 0) {
                                            productos[indice] =
                                                productos[indice].copy(
                                                    stock = productos[indice].stock + unidades
                                                )
                                        }

                                        movimientos.add(movimiento)

                                        if (proveedores.none {
                                                it.equals(
                                                    nombreProveedor,
                                                    ignoreCase = true
                                                )
                                            }
                                        ) {
                                            proveedores.add(nombreProveedor)
                                        }

                                        mensaje = "Entrada registrada correctamente."
                                        cantidad = ""
                                        proveedor = ""
                                        observaciones = ""
                                        productoSeleccionado = null

                                    } catch (e: Exception) {
                                        mensaje = "Error al registrar: ${e.localizedMessage}"
                                    } finally {
                                        guardando = false
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
                        Text("Registrar entrada")
                    }
                }

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = if (mensaje == "Entrada registrada correctamente.") {
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
            style = MaterialTheme.typography.titleLarge
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
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(movimiento.nombreProducto)
                        Text("Código: ${movimiento.codigoProducto}")
                        Text("Cantidad ingresada: ${movimiento.cantidad}")
                        Text("Proveedor: ${movimiento.proveedor.ifBlank { "No especificado" }}")

                        if (movimiento.usuarioNombre.isNotBlank()) {
                            Text("Registrado por: ${movimiento.usuarioNombre}")
                        }

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
