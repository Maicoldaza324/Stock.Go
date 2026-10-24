package com.example.stockgo.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Producto

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAgregar: (Producto) -> Unit,
    siguienteCodigo: String
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Agregar producto")
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                    },
                    label = {
                        Text("Nombre del producto")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = {
                        categoria = it
                    },
                    label = {
                        Text("Categoría")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = {
                        stock = it
                    },
                    label = {
                        Text("Cantidad disponible")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stockMinimo,
                    onValueChange = {
                        stockMinimo = it
                    },
                    label = {
                        Text("Stock mínimo")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
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

                    if (nombre.isBlank() || categoria.isBlank()) {

                        error = "Completa el nombre y la categoría."

                    } else if (
                        cantidad == null ||
                        minimo == null ||
                        cantidad < 0 ||
                        minimo < 0
                    ) {

                        error = "Ingresa cantidades válidas."

                    } else {

                        onAgregar(
                            Producto(
                                codigo = siguienteCodigo,
                                nombre = nombre.trim(),
                                categoria = categoria.trim(),
                                stock = cantidad,
                                stockMinimo = minimo
                            )
                        )
                    }
                }
            ) {
                Text("Agregar")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}