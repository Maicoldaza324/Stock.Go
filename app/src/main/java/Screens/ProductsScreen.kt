
package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Components.AddProductDialog
import com.example.stockgo.Components.ProductItem
import com.example.stockgo.Data.ProductoRepository
import com.example.stockgo.Model.Producto
import kotlinx.coroutines.launch

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun ProductsScreen(
    productos: SnapshotStateList<Producto>,
    categorias: SnapshotStateList<String>,
    esAdministrador: Boolean,
    onNavigate: (String) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoEditando by remember { mutableStateOf<Producto?>(null) }
    var productoDetalle by remember { mutableStateOf<Producto?>(null) }

    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val repository = remember { ProductoRepository() }
    val scope = rememberCoroutineScope()

    // Cargar productos desde Firestore
    LaunchedEffect(Unit) {
        cargando = true
        mensajeError = null

        try {
            val productosFirebase = repository.obtenerProductos()

            productos.clear()
            productos.addAll(productosFirebase)

        } catch (e: Exception) {
            mensajeError = "No se pudieron cargar los productos: ${e.localizedMessage}"
        } finally {
            cargando = false
        }
    }

    // Mostrar detalle del producto seleccionado
    if (productoDetalle != null) {
        ProductDetailScreen(
            producto = productoDetalle!!,
            onNavigateBack = {
                productoDetalle = null
            }
        )
        return
    }

    val productosFiltrados = productos.filter { producto ->
        val coincideBusqueda =
            producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true)

        val coincideFiltro = when (filtro) {
            "Bajo stock" ->
                producto.stock > 0 &&
                        producto.stock <= producto.stockMinimo

            "Agotados" -> producto.stock <= 0
            else -> true
        }

        coincideBusqueda && coincideFiltro
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulStock)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Productos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (esAdministrador) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate("Categorias") },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Categorías")
                    }

                    Button(
                        onClick = {
                            productoEditando = null
                            mostrarDialogo = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = AzulStock
                        )
                    ) {
                        Text("+ Agregar")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar producto...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todos", "Bajo stock", "Agotados").forEach { opcion ->
                    Button(
                        onClick = { filtro = opcion },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (filtro == opcion) {
                                AzulStock
                            } else {
                                Color.White
                            },
                            contentColor = if (filtro == opcion) {
                                Color.White
                            } else {
                                AzulStock
                            }
                        )
                    ) {
                        Text(
                            text = opcion,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Mensajes de carga y errores
            if (cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AzulStock)
                }
            }

            mensajeError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "${productosFiltrados.size} productos encontrados",
                color = Color(0xFF858B98),
                fontSize = 14.sp
            )

            if (!cargando && productosFiltrados.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se encontraron productos.",
                        color = Color(0xFF596273)
                    )
                }
            } else if (!cargando) {
                productosFiltrados.forEach { producto ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ProductItem(
                            producto = producto,
                            esAdministrador = esAdministrador,
                            onEditar = {
                                if (esAdministrador) {
                                    productoEditando = producto
                                    mostrarDialogo = true
                                }
                            },
                            onEliminar = {
                                if (esAdministrador) {
                                    scope.launch {
                                        try {
                                            repository.eliminarProducto(producto.codigo)
                                            productos.remove(producto)
                                            mensajeError = null
                                        } catch (e: Exception) {
                                            mensajeError =
                                                "No se pudo eliminar: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            }
                        )

                        OutlinedButton(
                            onClick = {
                                productoDetalle = producto
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver detalle")
                        }
                    }
                }
            }
        }

        // Navegación inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val opciones = listOf(
                "Inicio",
                "Productos",
                "Entradas",
                "Salidas",
                "Reportes",
                "Más"
            )

            opciones.forEach { opcion ->
                val icono = when (opcion) {
                    "Inicio" -> Icons.Filled.Home
                    "Productos" -> Icons.Filled.Inventory
                    "Entradas" -> Icons.Filled.TrendingUp
                    "Salidas" -> Icons.Filled.TrendingDown
                    "Reportes" -> Icons.Filled.Assessment
                    else -> Icons.Filled.MoreHoriz
                }

                val seleccionado = opcion == "Productos"

                TextButton(
                    onClick = { onNavigate(opcion) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 2.dp,
                        vertical = 4.dp
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = icono,
                            contentDescription = opcion,
                            modifier = Modifier.size(22.dp),
                            tint = if (seleccionado) {
                                AzulStock
                            } else {
                                Color(0xFF858B98)
                            }
                        )

                        Text(
                            text = opcion,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = if (seleccionado) {
                                AzulStock
                            } else {
                                Color(0xFF858B98)
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para agregar o editar productos
    if (mostrarDialogo && esAdministrador) {
        AddProductDialog(
            onDismiss = {
                mostrarDialogo = false
                productoEditando = null
            },
            onAgregar = { productoActualizado ->
                scope.launch {
                    try {
                        // Guardar primero en Firestore
                        repository.guardarProducto(productoActualizado)

                        // Actualizar la lista local después de guardar
                        if (productoEditando == null) {
                            productos.add(productoActualizado)
                        } else {
                            val indice = productos.indexOfFirst {
                                it.codigo == productoEditando?.codigo
                            }

                            if (indice != -1) {
                                productos[indice] = productoActualizado
                            }
                        }

                        mensajeError = null
                        mostrarDialogo = false
                        productoEditando = null

                    } catch (e: Exception) {
                        mensajeError =
                            "No se pudo guardar el producto: ${e.localizedMessage}"
                    }
                }
            },
            siguienteCodigo = "PROD-${(productos.size + 1).toString().padStart(3, '0')}",
            productoEditar = productoEditando,
            categorias = categorias.toList()
        )
    }
}
