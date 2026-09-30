package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Components.ProductAlert
import com.example.stockgo.Components.StatCard

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
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

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = "Stock.Go",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
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

            // Tarjetas de estadísticas
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    titulo = "Total productos",
                    valor = "124",
                    color = AzulStock,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "Disponibles",
                    valor = "98",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    titulo = "Bajo stock",
                    valor = "18",
                    color = Color(0xFFE5A323),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    titulo = "Agotados",
                    valor = "8",
                    color = Color(0xFFC33B3B),
                    modifier = Modifier.weight(1f)
                )
            }

            // Alertas
            Text(
                text = "⚠ Atención requerida",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202536)
            )

            ProductAlert(
                nombre = "Mouse inalámbrico Logitech",
                detalle = "Stock: 3 · Mínimo: 10",
                estado = "Bajo stock",
                color = Color(0xFFE5A323)
            )

            ProductAlert(
                nombre = "Resma papel A4 75g",
                detalle = "Stock: 0 · Mínimo: 20",
                estado = "Agotado",
                color = Color(0xFFC33B3B)
            )

            ProductAlert(
                nombre = "Tóner HP LaserJet 85A",
                detalle = "Stock: 2 · Mínimo: 5",
                estado = "Bajo stock",
                color = Color(0xFFE5A323)
            )

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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 2.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = opcion,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        color = if (opcion == "Inicio") {
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