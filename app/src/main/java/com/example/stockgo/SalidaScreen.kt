package com.example.stockgo // VERIFICA Y CAMBIA ESTO POR TU PAQUETE REAL

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalidaScreen(
    onBackClick: () -> Unit = {},
    onRegistrarClick: () -> Unit = {}
) {
    // Colores según Figma para Salida
    val darkBlue = Color(0xFF2C365E)
    val lightRedBg = Color(0xFFFBE9E7)
    val iconRedBg = Color(0xFFEF5350)
    val buttonRed = Color(0xFFC62828)

    // Estados del formulario
    var productoSeleccionado by remember { mutableStateOf("") }
    var expandedProducto by remember { mutableStateOf(false) }
    val productosLista = listOf("Producto A", "Producto B", "Producto C")

    var motivoSeleccionado by remember { mutableStateOf("Uso interno") }
    var expandedMotivo by remember { mutableStateOf(false) }
    val motivosLista = listOf("Uso interno", "Venta", "Dañado / Expirado", "Otro")

    var cantidad by remember { mutableStateOf("0") }
    var fecha by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

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
                    IconButton(onClick = onBackClick) {
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
                    onClick = onRegistrarClick,
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
            // Tarjeta informativa Roja
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
                            text = "El stock del producto disminuirá",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Tarjeta del Formulario
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
                    // Producto (Dropdown)
                    Text("Producto", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Box {
                        OutlinedTextField(
                            value = productoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Seleccionar producto...") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
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
                            productosLista.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text(prod) },
                                    onClick = {
                                        productoSeleccionado = prod
                                        expandedProducto = false
                                    }
                                )
                            }
                        }
                    }

                    // Cantidad
                    Text("Cantidad", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Motivo (Dropdown)
                    Text("Motivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Box {
                        OutlinedTextField(
                            value = motivoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedMotivo = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = Color.LightGray
                            )
                        )
                        DropdownMenu(
                            expanded = expandedMotivo,
                            onDismissRequest = { expandedMotivo = false }
                        ) {
                            motivosLista.forEach { mot ->
                                DropdownMenuItem(
                                    text = { Text(mot) },
                                    onClick = {
                                        motivoSeleccionado = mot
                                        expandedMotivo = false
                                    }
                                )
                            }
                        }
                    }

                    // Fecha
                    Text("Fecha", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        placeholder = { Text("dd/mm/aaaa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Observación
                    Text("Observación (opcional)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
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
}

