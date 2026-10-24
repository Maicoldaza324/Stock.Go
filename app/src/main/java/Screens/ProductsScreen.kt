package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.stockgo.Components.AddProductDialog
import com.example.stockgo.Components.ProductItem
import com.example.stockgo.Model.Producto

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun ProductsScreen(
    productos: SnapshotStateList<Producto>,
    onNavigate: (String) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val productosFiltrados = productos.filter { producto ->

        val coincideBusqueda =
            producto.nombre.contains(
                busqueda,
                ignoreCase = true
            ) ||
                    producto.codigo.contains(
                        busqueda,
                        ignoreCase = true
                    )

        val coincideFiltro = when (filtro) {

            "Bajo stock" ->
                producto.stock > 0 &&
                        producto.stock <= producto.stockMinimo

            "Agotados" ->
                producto.stock == 0

            else ->
                true
        }

        coincideBusqueda && coincideFiltro
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
    ) {

        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulStock)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Productos",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Button(
                onClick = {
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

        // Contenido
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar producto...")
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                listOf(
                    "Todos",
                    "Bajo stock",
                    "Agotados"
                ).forEach { opcion ->

                    Button(
                        onClick = {
                            filtro = opcion
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (filtro == opcion) {
                                    AzulStock
                                } else {
                                    Color.White
                                },
                            contentColor =
                                if (filtro == opcion) {
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

            Text(
                text = "${productosFiltrados.size} productos encontrados",
                color = Color(0xFF858B98),
                fontSize = 14.sp
            )

            // Lista de productos
            if (productosFiltrados.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "No se encontraron productos.",
                        color = Color(0xFF596273)
                    )
                }

            } else {

                productosFiltrados.forEach { producto ->

                    ProductItem(
                        producto = producto,
                        onEliminar = {
                            productos.remove(producto)
                        }
                    )
                }
            }
        }

        // Barra de navegación inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
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

                TextButton(
                    onClick = {
                        onNavigate(opcion)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 2.dp,
                        vertical = 4.dp
                    )
                ) {

                    Text(
                        text = opcion,
                        fontSize = 10.sp,
                        color =
                            if (opcion == "Productos") {
                                AzulStock
                            } else {
                                Color(0xFF858B98)
                            }
                    )
                }
            }
        }
    }

    // Diálogo para agregar producto
    if (mostrarDialogo) {

        AddProductDialog(
            onDismiss = {
                mostrarDialogo = false
            },

            onAgregar = { nuevoProducto ->

                productos.add(nuevoProducto)

                mostrarDialogo = false
            },

            siguienteCodigo =
                "PROD-${(productos.size + 1).toString().padStart(3, '0')}"
        )
    }
}