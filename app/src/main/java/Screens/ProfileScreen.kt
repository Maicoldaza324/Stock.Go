
package com.example.stockgo.Screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Usuario

@Composable
fun ProfileScreen(
    usuario: Usuario,
    onActualizarPerfil: (String, String) -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    notificaciones: Boolean,
    onNotificacionesChange: (Boolean) -> Unit,
    correoDisponible: (String) -> Boolean
) {

    var editando by remember { mutableStateOf(false) }

    var nombre by remember(usuario.id, usuario.nombre) {
        mutableStateOf(usuario.nombre)
    }

    var correo by remember(usuario.id, usuario.correo) {
        mutableStateOf(usuario.correo)
    }

    var mensajeError by remember { mutableStateOf("") }

    val azul = Color(0xFF1976D2)
    val fondo = Color(0xFFF4F6FA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
    ) {

        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(azul)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { onNavigate("Más") }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Text(
                text = "Mi perfil",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Imagen de perfil
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(50.dp),
                color = azul
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Usuario",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = usuario.rol,
                color = azul,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Información personal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "Información personal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (!editando) {
                            IconButton(
                                onClick = {
                                    nombre = usuario.nombre
                                    correo = usuario.correo
                                    mensajeError = ""
                                    editando = true
                                }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar perfil",
                                    tint = azul
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = editando,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = editando,
                        singleLine = true
                    )

                    if (mensajeError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mensajeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }

                    if (editando) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    nombre = usuario.nombre
                                    correo = usuario.correo
                                    mensajeError = ""
                                    editando = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {

                                    val correoLimpio = correo.trim()

                                    when {
                                        nombre.isBlank() -> {
                                            mensajeError =
                                                "Ingresa tu nombre."
                                        }

                                        !Patterns.EMAIL_ADDRESS.matcher(
                                            correoLimpio
                                        ).matches() -> {
                                            mensajeError =
                                                "Ingresa un correo válido."
                                        }

                                        !correoDisponible(correoLimpio) -> {
                                            mensajeError =
                                                "Este correo ya está registrado."
                                        }

                                        else -> {
                                            onActualizarPerfil(
                                                nombre.trim(),
                                                correoLimpio
                                            )

                                            mensajeError = ""
                                            editando = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = azul
                                )
                            ) {

                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = null
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text("Guardar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preferencias
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        "Preferencias",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text("Notificaciones")

                            Text(
                                text = if (notificaciones) {
                                    "Preferencia activada"
                                } else {
                                    "Preferencia desactivada"
                                },
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Switch(
                            checked = notificaciones,
                            onCheckedChange = onNotificacionesChange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cerrar sesión
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                )
            ) {

                Icon(
                    Icons.Default.Logout,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Cerrar sesión")
            }
        }
    }
}
