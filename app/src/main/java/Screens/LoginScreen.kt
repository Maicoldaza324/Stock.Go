
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
import androidx.compose.ui.platform.LocalContext

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

import com.example.stockgo.Auth.FirebaseAuthManager
import com.example.stockgo.Data.UsuarioRepository
import com.example.stockgo.Model.Usuario
import com.example.stockgo.R

import kotlinx.coroutines.launch

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

@Composable
fun LoginScreen(
    onLoginSuccess: (Usuario) -> Unit
) {
    val contexto = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(true) }
    var cargando by remember { mutableStateOf(false) }

    val authManager = remember { FirebaseAuthManager() }
    val usuarioRepository = remember { UsuarioRepository() }
    val credentialManager = remember {
        CredentialManager.create(contexto)
    }

    val scope = rememberCoroutineScope()

    // ==========================================
    // INICIO DE SESIÓN CON CORREO Y CONTRASEÑA
    // ==========================================

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

                        val perfil = usuarioRepository.obtenerUsuario(
                            usuarioFirebase.uid
                        )

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

    // ==========================================
    // INICIO DE SESIÓN Y REGISTRO CON GOOGLE
    // ==========================================

    fun iniciarSesionGoogle() {
        if (cargando) return

        scope.launch {
            cargando = true
            mensaje = ""

            try {
                // 1. Configurar Google Credential Manager.
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        contexto.getString(R.string.default_web_client_id)
                    )
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // 2. Mostrar selector de cuentas Google.
                val resultado = credentialManager.getCredential(
                    context = contexto,
                    request = request
                )

                val credential = resultado.credential

                // 3. Obtener el token de Google.
                if (
                    credential !is CustomCredential ||
                    credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    throw IllegalStateException(
                        "No se pudo obtener una credencial válida de Google."
                    )
                }

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                // 4. Autenticar con Firebase.
                val resultadoFirebase = authManager.iniciarSesionGoogle(
                    googleCredential.idToken
                )

                val usuarioFirebase = resultadoFirebase.user
                    ?: throw IllegalStateException(
                        "No se pudo obtener el usuario de Firebase."
                    )

                // 5. Buscar si ya existe un perfil.
                var perfil = usuarioRepository.obtenerUsuario(
                    usuarioFirebase.uid
                )

                if (perfil == null) {

                    // 6. Crear perfil nuevo como empleado.
                    // No consultamos toda la colección de usuarios:
                    // las reglas permiten al usuario leer únicamente su perfil.

                    val nombre = usuarioFirebase.displayName
                        ?.takeIf { it.isNotBlank() }
                        ?: usuarioFirebase.email
                            ?.substringBefore("@")
                        ?: "Usuario Google"

                    val correoGoogle = usuarioFirebase.email
                        ?: throw IllegalStateException(
                            "La cuenta de Google no tiene un correo disponible."
                        )

                    val nuevoId = (
                            System.currentTimeMillis() % Int.MAX_VALUE
                            ).toInt()

                    val nuevoUsuario = Usuario(
                        id = nuevoId,
                        uid = usuarioFirebase.uid,
                        nombre = nombre,
                        correo = correoGoogle,
                        contrasena = "",
                        rol = "Empleado",
                        activo = true
                    )

                    // 7. Guardar perfil en Firestore.
                    usuarioRepository.guardarUsuario(nuevoUsuario)

                    perfil = nuevoUsuario
                }

                // 8. Comprobar el estado de la cuenta.
                if (!perfil.activo) {
                    authManager.cerrarSesion()

                    mensaje = "Tu cuenta está inactiva. Contacta al administrador."
                    esError = true

                    return@launch
                }

                // 9. Acceso permitido.
                mensaje = ""
                esError = false

                onLoginSuccess(perfil)

            } catch (e: Exception) {

                // Evitar dejar una sesión abierta si falla
                // la creación o consulta del perfil.
                authManager.cerrarSesion()

                mensaje = when {
                    e.message?.contains(
                        "cancel",
                        ignoreCase = true
                    ) == true -> "Inicio de sesión cancelado."

                    e.message?.contains(
                        "network",
                        ignoreCase = true
                    ) == true -> "No hay conexión. Verifica tu internet."

                    else -> "No se pudo iniciar sesión con Google: ${
                        e.localizedMessage ?: "Inténtalo nuevamente."
                    }"
                }

                esError = true

            } finally {
                cargando = false
            }
        }
    }

    // ==========================================
    // INTERFAZ
    // ==========================================

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

                // CORREO
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

                // CONTRASEÑA
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

                // RECUPERAR CONTRASEÑA
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

                // BOTÓN INICIAR SESIÓN
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

                // SEPARADOR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )

                    Text(
                        text = "  O continúa con  ",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray
                    )
                }

                // BOTÓN GOOGLE
                OutlinedButton(
                    onClick = { iniciarSesionGoogle() },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF202536)
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = AzulStock
                        )
                    } else {
                        Text(
                            text = "G",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4285F4)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Continuar con Google",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // MENSAJES
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
