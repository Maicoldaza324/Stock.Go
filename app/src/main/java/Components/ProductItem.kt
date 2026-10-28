package com.example.stockgo.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Producto

@Composable
fun ProductItem(
    producto: Producto,
    onEliminar: () -> Unit
) {
    val estado = when {
        producto.stock == 0 -> "Agotado"
        producto.stock <= producto.stockMinimo -> "Bajo stock"
        else -> "Disponible"
    }

    val colorEstado = when (estado) {
        "Agotado" -> Color(0xFFC33B3B)
        "Bajo stock" -> Color(0xFFE5A323)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = producto.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF202536)
            )

            Text(
                text = "${producto.codigo} · ${producto.categoria}",
                fontSize = 13.sp,
                color = Color(0xFF858B98)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Stock: ${producto.stock}",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202536)
                    )

                    Text(
                        text = "Mínimo: ${producto.stockMinimo}",
                        fontSize = 12.sp,
                        color = Color(0xFF858B98)
                    )
                }

                Text(
                    text = estado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )
            }

            TextButton(
                onClick = onEliminar,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Eliminar",
                    color = Color(0xFFC33B3B)
                )
            }
        }
    }
}