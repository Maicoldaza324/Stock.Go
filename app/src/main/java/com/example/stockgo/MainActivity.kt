package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalidasScreen(
    productos: MutableList<Producto>,
    movimientos: MutableList<MovimientoInventario>,
    onNavigate: (String) -> Unit
) {
    val darkBlue = Color(0xFF2C365E)
    val lightRedBg = Color(0xFFFBE9E7)
    val iconRedBg = Color(0xFFEF5350)
    val buttonRed = Color(0xFFC62828)

    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var expandedProducto by remember { mutableStateOf(false) }

    var cantidad by remember { mutableStateOf("") }
    var observacionesInput by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar salida",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("Inicio") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBlue)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val cantInt = cantidad.toIntOrNull() ?: 0
                        if (productoSeleccionado == null) {
                            mensaje = "Por favor selecciona un producto."
                            mostrarDialogo = true
                        } else if (cantInt <= 0) {
                            mensaje = "La cantidad debe ser mayor a 0."
                            mostrarDialogo = true
                        } else if (cantInt > productoSeleccionado!!.stock) {
                            mensaje = "Stock insuficiente. Stock actual: ${productoSeleccionado!!.stock}"
                            mostrarDialogo = true
                        } else {
                            val index = productos.indexOf(productoSeleccionado!!)
                            if (index != -1) {
                                val prodActual = productos[index]
                                productos[index] = prodActual.copy(stock = prodActual.stock - cantInt)
                            }

                            movimientos.add(
                                MovimientoInventario(
                                    id = System.currentTimeMillis().toString(),
                                    codigoProducto = productoSeleccionado!!.codigo,
                                    nombreProducto = productoSeleccionado!!.nombre,
                                    tipo = TipoMovimiento.SALIDA,
                                    cantidad = cantInt,
                                    fecha = System.currentTimeMillis(),
                                    observaciones = observacionesInput.ifEmpty { "Salida de inventario" }
                                )
                            )

                            mensaje = "¡Salida registrada con éxito! Stock actualizado."
                            mostrarDialogo = true

                            cantidad = ""
                            observacionesInput = ""
                            productoSeleccionado = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar salida",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = lightRedBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(iconRedBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Salida de inventario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = "El stock del producto disminuirá automáticamente",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Producto", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Box {
                        OutlinedTextField(
                            value = productoSeleccionado?.let { "${it.codigo} - ${it.nombre} (Stock: ${it.stock})" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Seleccionar producto...") },
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedProducto = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = Color.LightGray,
                                disabledPlaceholderColor = Color.Gray
                            )
                        )
                        DropdownMenu(
                            expanded = expandedProducto,
                            onDismissRequest = { expandedProducto = false }
                        ) {
                            productos.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text("${prod.codigo} - ${prod.nombre} (Stock: ${prod.stock})") },
                                    onClick = {
                                        productoSeleccionado = prod
                                        expandedProducto = false
                                    }
                                )
                            }
                        }
                    }

                    Text("Cantidad", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text("Observación (opcional)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = observacionesInput,
                        onValueChange = { observacionesInput = it },
                        placeholder = { Text("Notas adicionales...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Aviso") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}