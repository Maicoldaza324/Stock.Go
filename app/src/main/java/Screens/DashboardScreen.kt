
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Components.ProductAlert
import com.example.stockgo.Components.StatCard
import com.example.stockgo.Data.ProductoRepository
import com.example.stockgo.Model.Producto

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun DashboardScreen(
    productos: List<Producto> = emptyList(),
    onNavigate: (String) -> Unit
) {
    val productosDashboard = remember {
        mutableStateListOf<Producto>().apply {
            addAll(productos)
        }
    }

    // Consultar productos directamente desde Firestore
    LaunchedEffect(Unit) {
        try {
            val repositorio = ProductoRepository()
            val productosFirebase = repositorio.obtenerProductos()

            productosDashboard.clear()
            productosDashboard.addAll(productosFirebase)
        } catch (e: Exception) {
            // Si falla la conexión, se conservan los datos recibidos.
        }
    }

    // Estadísticas del inventario
    val totalProductos = productosDashboard.size

    val disponibles = productosDashboard.count {
        it.stock > 0
    }

    val bajoStock = productosDashboard.count {
        it.stock > 0 && it.stock <= it.stockMinimo
    }

    val agotados = productosDashboard.count {
        it.stock <= 0
    }

    // Alertas de inventario
    val productosAlerta = productosDashboard
        .filter {
            it.stock <= it.stockMinimo
        }
        .sortedWith(
            compareBy<Producto> { if (it.stock <= 0) 0 else 1 }
                .thenBy { it.stock }
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
    ) {

        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AzulStock,
                    RoundedCornerShape(
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "Buenos días 👋",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stock.Go",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Resumen de tu inventario",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        // Contenido del Dashboard
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Estadísticas
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    titulo = "Total productos",
                    valor = totalProductos.toString(),
                    color = AzulStock,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "Disponibles",
                    valor = disponibles.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    titulo = "Bajo stock",
                    valor = bajoStock.toString(),
                    color = Color(0xFFE5A323),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "Agotados",
                    valor = agotados.toString(),
                    color = Color(0xFFC33B3B),
                    modifier = Modifier.weight(1f)
                )
            }

            // Alertas de inventario
            Text(
                text = "⚠ Atención requerida",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202536)
            )

            if (productosAlerta.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFE7F5E9),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(18.dp)
                ) {
                    Text(
                        text = "¡Inventario en buen estado!",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "No tienes productos con bajo stock ni agotados.",
                        color = Color(0xFF388E3C),
                        fontSize = 14.sp
                    )
                }

            } else {

                Text(
                    text = "${productosAlerta.size} productos requieren atención",
                    color = Color(0xFF858B98),
                    fontSize = 14.sp
                )

                productosAlerta.forEach { producto ->

                    val agotado = producto.stock <= 0

                    ProductAlert(
                        nombre = producto.nombre,
                        detalle = "Stock: ${producto.stock} · Mínimo: ${producto.stockMinimo}",
                        estado = if (agotado) {
                            "Agotado"
                        } else {
                            "Bajo stock"
                        },
                        color = if (agotado) {
                            Color(0xFFC33B3B)
                        } else {
                            Color(0xFFE5A323)
                        }
                    )
                }
            }

            // Acciones rápidas
            Text(
                text = "Acciones rápidas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202536)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onNavigate("Entradas")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE7F5E9),
                        contentColor = Color(0xFF388E3C)
                    )
                ) {
                    Text("↑ Entrada")
                }

                Button(
                    onClick = {
                        onNavigate("Salidas")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBE9E7),
                        contentColor = Color(0xFFC33B3B)
                    )
                ) {
                    Text("↓ Salida")
                }
            }
        }

        // Barra de navegación inferior
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

                val seleccionado = opcion == "Inicio"

                TextButton(
                    onClick = {
                        if (!seleccionado) {
                            onNavigate(opcion)
                        }
                    },
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
}
