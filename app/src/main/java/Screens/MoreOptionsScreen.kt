package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun MoreOptionsScreen(
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = AzulStock,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onNavigate("Inicio") }
            )

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Más opciones",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulStock
                )

                Text(
                    text = "Administra tu cuenta y aplicación",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = "Mi cuenta",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF202536),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OptionCard(
            titulo = "Mi perfil",
            descripcion = "Consulta tus datos personales",
            icono = Icons.Filled.Person,
            onClick = { onNavigate("Perfil") }
        )

        Spacer(modifier = Modifier.size(12.dp))

        OptionCard(
            titulo = "Configuración",
            descripcion = "Opciones y preferencias de la aplicación",
            icono = Icons.Filled.Settings,
            onClick = { onNavigate("Configuracion") }
        )

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = "Administración",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF202536),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OptionCard(
            titulo = "Usuarios",
            descripcion = "Accede a la gestión de usuarios",
            icono = Icons.Filled.People,
            onClick = { onNavigate("Usuarios") }
        )
    }
}

@Composable
private fun OptionCard(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = AzulStock,
                modifier = Modifier.size(30.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF202536)
                )

                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Abrir $titulo",
                tint = AzulStock
            )
        }
    }
}