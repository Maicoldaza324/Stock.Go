
package com.example.stockgo.Screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Usuario

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun LoginScreen(
    usuarios: List<Usuario>,
    onLoginSuccess: (Usuario) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    var esError by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(AzulStock)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▦",
                    fontSize = 58.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Stock.Go",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sistema de Gestión de Inventarios",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-38).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                Text(
                    text = "Bienvenido de nuevo",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202536)
                )

                Text(
                    text = "Ingresa tus credenciales para continuar",
                    fontSize = 15.sp,
                    color = Color(0xFF858B98)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Correo electrónico",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202536)
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            mensaje = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("admin@stockgo.com")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Contraseña",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202536)
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            mensaje = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Ingresa tu contraseña")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    mostrarContrasena = !mostrarContrasena
                                }
                            ) {
                                Text(
                                    if (mostrarContrasena) "Ocultar"
                                    else "Mostrar",
                                    color = AzulStock
                                )
                            }
                        }
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = {
                            mensaje = "La recuperación de contraseña se implementará posteriormente."
                            esError = false
                        }
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = AzulStock
                        )
                    }
                }

                Button(
                    onClick = {
                        val correoIngresado = correo.trim()

                        when {
                            correoIngresado.isBlank() || contrasena.isBlank() -> {
                                mensaje = "Ingresa tu correo y contraseña."
                                esError = true
                            }

                            !Patterns.EMAIL_ADDRESS.matcher(correoIngresado).matches() -> {
                                mensaje = "Ingresa un correo electrónico válido."
                                esError = true
                            }

                            else -> {
                                val usuario = usuarios.find {
                                    it.correo.equals(
                                        correoIngresado,
                                        ignoreCase = true
                                    )
                                }

                                when {
                                    usuario == null -> {
                                        mensaje = "No existe un usuario con ese correo."
                                        esError = true
                                    }

                                    !usuario.activo -> {
                                        mensaje = "Tu cuenta está inactiva. Contacta al administrador."
                                        esError = true
                                    }

                                    usuario.contrasena != contrasena -> {
                                        mensaje = "La contraseña es incorrecta."
                                        esError = true
                                    }

                                    else -> {
                                        mensaje = ""
                                        onLoginSuccess(usuario)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF465DE0)
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = if (esError) {
                            Color(0xFFB3261E)
                        } else {
                            AzulStock
                        },
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
