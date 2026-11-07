
package com.example.stockgo.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAgregar: (Producto) -> Unit,
    siguienteCodigo: String,
    productoEditar: Producto? = null,
    categorias: List<String>
) {
    var nombre by remember(productoEditar) {
        mutableStateOf(productoEditar?.nombre ?: "")
    }

    var categoria by remember(productoEditar, categorias) {
        mutableStateOf(
            productoEditar?.categoria
                ?.takeIf { actual ->
                    categorias.any { it.equals(actual, true) }
                }
                ?: categorias.firstOrNull().orEmpty()
        )
    }

    var menuCategorias by remember { mutableStateOf(false) }

    var stock by remember(productoEditar) {
        mutableStateOf(productoEditar?.stock?.toString() ?: "")
    }

    var stockMinimo by remember(productoEditar) {
        mutableStateOf(productoEditar?.stockMinimo?.toString() ?: "")
    }

    var precio by remember(productoEditar) {
        mutableStateOf(
            productoEditar?.precio?.let {
                if (it % 1.0 == 0.0) it.toLong().toString()
                else it.toString()
            } ?: ""
        )
    }

    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (productoEditar == null) {
                    "Agregar producto"
                } else {
                    "Editar producto"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = menuCategorias,
                    onExpandedChange = {
                        menuCategorias = !menuCategorias
                    }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        placeholder = { Text("Selecciona una categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = menuCategorias
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = menuCategorias,
                        onDismissRequest = {
                            menuCategorias = false
                        }
                    ) {
                        categorias.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    categoria = opcion
                                    menuCategorias = false
                                }
                            )
                        }
                    }
                }

                if (categorias.isEmpty()) {
                    Text(
                        text = "Primero debes crear una categoría.",
                        color = Color(0xFFB3261E),
                        fontSize = 13.sp
                    )
                }

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Cantidad disponible") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stockMinimo,
                    onValueChange = { stockMinimo = it },
                    label = { Text("Stock mínimo") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = precio,
                    onValueChange = { nuevoValor ->
                        if (nuevoValor.matches(
                                Regex("^\\d*([.,]\\d{0,2})?$")
                            )
                        ) {
                            precio = nuevoValor
                        }
                    },
                    label = { Text("Precio de venta (COP)") },
                    placeholder = { Text("Ej. 25000") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        color = Color(0xFFC33B3B),
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cantidad = stock.toIntOrNull()
                    val minimo = stockMinimo.toIntOrNull()
                    val precioNumerico = precio
                        .replace(",", ".")
                        .toDoubleOrNull()

                    when {
                        nombre.isBlank() -> {
                            error = "Completa el nombre del producto."
                        }

                        categoria.isBlank() || categorias.isEmpty() -> {
                            error = "Selecciona una categoría."
                        }

                        cantidad == null || minimo == null ||
                                cantidad < 0 || minimo < 0 -> {
                            error = "Ingresa cantidades válidas."
                        }

                        precioNumerico == null || precioNumerico < 0 -> {
                            error = "Ingresa un precio válido."
                        }

                        else -> {
                            onAgregar(
                                Producto(
                                    codigo = productoEditar?.codigo
                                        ?: siguienteCodigo,
                                    nombre = nombre.trim(),
                                    categoria = categoria,
                                    stock = cantidad,
                                    stockMinimo = minimo,
                                    precio = precioNumerico
                                )
                            )
                        }
                    }
                },
                enabled = categorias.isNotEmpty()
            ) {
                Text(
                    if (productoEditar == null) "Agregar"
                    else "Guardar cambios"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
