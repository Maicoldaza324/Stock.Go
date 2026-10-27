package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulPerfil = Color(0xFF3044B5)
private val FondoPerfil = Color(0xFFF5F7FB)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var notificacionesHabilitadas by remember { mutableStateOf(true) }
    var modoOscuro by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPerfil)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Perfil y Configuración",
            color = AzulPerfil,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de Información de Usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Juan David Hoyos Avila",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPerfil
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "jhoyos37@uan.edu.co",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = AzulPerfil.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Rol: Supervisor / Administrador",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = AzulPerfil,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Configuración de la Aplicación",
            color = AzulPerfil,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta de Preferencias
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Notificaciones de stock bajo", fontWeight = FontWeight.Medium)
                        Text(text = "Alertar cuando un producto esté agotado", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = notificacionesHabilitadas,
                        onCheckedChange = { notificacionesHabilitadas = it }
                    )
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Modo oscuro", fontWeight = FontWeight.Medium)
                        Text(text = "Cambiar apariencia visual de la app", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = { modoOscuro = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para Cerrar Sesión
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
        ) {
            Text("Cerrar Sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = { onNavigate("Inicio") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Inicio")
        }
    }
}

