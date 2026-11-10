
package com.example.stockgo.Screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Auth.FirebaseAuthManager
import com.example.stockgo.Data.UsuarioRepository
import com.example.stockgo.Model.Usuario
import kotlinx.coroutines.launch

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun LoginScreen(
    onLoginSuccess: (Usuario) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(true) }
    var cargando by remember { mutableStateOf(false) }

    val authManager = remember { FirebaseAuthManager() }
    val usuarioRepository = remember { UsuarioRepository() }
    val scope = rememberCoroutineScope()

    fun iniciarSesion() {
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

            cargando -> return

            else -> {
                scope.launch {
                    cargando = true
                    mensaje = ""

                    try {
                        // 1. Autenticar con Firebase Authentication
                        val resultado = authManager.iniciarSesionCorreo(
                            correoIngresado,
                            contrasena
                        )

                        val usuarioFirebase = resultado.user

                        if (usuarioFirebase == null) {
                            mensaje = "No fue posible obtener tu cuenta."
                            esError = true
                            return@launch
                        }

                        // 2. Consultar el perfil en Firestore
                        val perfil = usuarioRepository.obtenerUsuario(
                            usuarioFirebase.uid
                        )

                        // 3. Validar el perfil
                        when {
                            perfil == null -> {
                                authManager.cerrarSesion()
                                mensaje = "Tu cuenta no tiene un perfil registrado en Stock.Go. Contacta al administrador."
                                esError = true
                            }

                            !perfil.activo -> {
                                authManager.cerrarSesion()
                                mensaje = "Tu cuenta está inactiva. Contacta al administrador."
                                esError = true
                            }

                            else -> {
                                mensaje = ""
                                esError = false
                                onLoginSuccess(perfil)
                            }
                        }

                    } catch (e: Exception) {
                        mensaje = when {
                            e.message?.contains(
                                "password",
                                ignoreCase = true
                            ) == true -> "Correo o contraseña incorrectos."

                            e.message?.contains(
                                "network",
                                ignoreCase = true
                            ) == true -> "No hay conexión. Verifica tu internet."

                            else -> "No se pudo iniciar sesión. Verifica tus datos e inténtalo nuevamente."
                        }
                        esError = true

                    } finally {
                        cargando = false
                    }
                }
            }
        }
    }

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
                        enabled = !cargando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
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
                        enabled = !cargando,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { iniciarSesion() }
                        ),
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    mostrarContrasena = !mostrarContrasena
                                },
                                enabled = !cargando
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
                        },
                        enabled = !cargando
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = AzulStock
                        )
                    }
                }

                Button(
                    onClick = { iniciarSesion() },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF465DE0)
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
