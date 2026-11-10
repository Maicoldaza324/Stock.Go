
package com.example.stockgo.Screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Auth.FirebaseUsuarioAdminManager
import com.example.stockgo.Data.UsuarioRepository
import com.example.stockgo.Model.Usuario
import kotlinx.coroutines.launch

private val AzulUsuarios = Color(0xFF3044B5)
private val FondoUsuarios = Color(0xFFF5F7FB)

@Composable
fun UsersScreen(
    usuarios: SnapshotStateList<Usuario>,
    usuarioActual: Usuario?,
    onNavigate: (String) -> Unit
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember { UsuarioRepository() }
    val adminManager = remember {
        FirebaseUsuarioAdminManager(contexto)
    }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioEditando by remember { mutableStateOf<Usuario?>(null) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Empleado") }
    var activo by remember { mutableStateOf(true) }

    var errorFormulario by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    fun cargarUsuarios() {
        scope.launch {
            cargando = true

            try {
                val lista = repository.obtenerUsuarios()

                usuarios.clear()
                usuarios.addAll(lista)

                mensaje = ""
            } catch (e: Exception) {
                mensaje = "No se pudieron consultar los usuarios: ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarUsuarios()
    }

    val administradoresActivos = usuarios.count {
        it.rol == "Administrador" && it.activo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoUsuarios)
            .padding(16.dp)
    ) {
        Text(
            text = "Administración de usuarios",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = AzulUsuarios
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Registra y administra los usuarios del sistema.",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                usuarioEditando = null
                nombre = ""
                correo = ""
                contrasena = ""
                rol = "Empleado"
                activo = true
                errorFormulario = ""
                mensaje = ""
                mostrarFormulario = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulUsuarios
            )
        ) {
            Text("Registrar usuario")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Usuarios registrados: ${usuarios.size}",
            fontWeight = FontWeight.SemiBold,
            color = AzulUsuarios
        )

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensaje,
                color = Color(0xFFB3261E),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (cargando) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AzulUsuarios)
            }
        } else if (usuarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay usuarios registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = usuarios,
                    key = { if (it.uid.isNotBlank()) it.uid else it.id }
                ) { usuario ->

                    val esUsuarioActual =
                        usuarioActual?.uid?.isNotBlank() == true &&
                                usuarioActual.uid == usuario.uid

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = usuario.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )

                                    Text(
                                        text = usuario.correo,
                                        color = Color.Gray,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    color = if (usuario.activo) {
                                        Color(0xFFE2F5E9)
                                    } else {
                                        Color(0xFFFCE4E4)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (usuario.activo) "Activo" else "Inactivo",
                                        color = if (usuario.activo) {
                                            Color(0xFF207A3C)
                                        } else {
                                            Color(0xFFB3261E)
                                        },
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        ),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = AzulUsuarios.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = usuario.rol,
                                    color = AzulUsuarios,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        usuarioEditando = usuario
                                        nombre = usuario.nombre
                                        correo = usuario.correo
                                        contrasena = ""
                                        rol = usuario.rol
                                        activo = usuario.activo
                                        errorFormulario = ""
                                        mensaje = ""
                                        mostrarFormulario = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = !esUsuarioActual
                                ) {
                                    Text("Editar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        mensaje = "La eliminación de cuentas se habilitará cuando implementemos su proceso seguro en Authentication."
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = false,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFB3261E)
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }

                            if (esUsuarioActual) {
                                Text(
                                    text = "Esta es tu cuenta actual. Adminístrala desde Perfil.",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color(0xFFEEEEEE)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Estado de la cuenta",
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = when {
                                            esUsuarioActual ->
                                                "No puedes desactivar tu propia cuenta."

                                            usuario.rol == "Administrador" &&
                                                    usuario.activo &&
                                                    administradoresActivos <= 1 ->
                                                "Es el último administrador activo."

                                            else ->
                                                "Permitir o bloquear el acceso"
                                        },
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }

                                Switch(
                                    checked = usuario.activo,
                                    enabled = !esUsuarioActual &&
                                            !(
                                                    usuario.rol == "Administrador" &&
                                                            usuario.activo &&
                                                            administradoresActivos <= 1
                                                    ),
                                    onCheckedChange = { nuevoEstado ->
                                        scope.launch {
                                            try {
                                                repository.actualizarEstado(
                                                    uid = usuario.uid,
                                                    activo = nuevoEstado
                                                )

                                                val indice = usuarios.indexOfFirst {
                                                    it.uid == usuario.uid
                                                }

                                                if (indice != -1) {
                                                    usuarios[indice] =
                                                        usuario.copy(activo = nuevoEstado)
                                                }

                                                mensaje = ""
                                            } catch (e: Exception) {
                                                mensaje = "No se pudo actualizar el estado: ${e.localizedMessage}"
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { onNavigate("Más") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a Más opciones")
        }
    }

    // FORMULARIO DE REGISTRO Y EDICIÓN
    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                if (!guardando) {
                    mostrarFormulario = false
                    usuarioEditando = null
                    errorFormulario = ""
                }
            },
            title = {
                Text(
                    if (usuarioEditando == null) {
                        "Registrar usuario"
                    } else {
                        "Editar usuario"
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            errorFormulario = ""
                        },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            errorFormulario = ""
                        },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        readOnly = usuarioEditando != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            errorFormulario = ""
                        },
                        label = {
                            Text(
                                if (usuarioEditando == null) {
                                    "Contraseña"
                                } else {
                                    "La contraseña no se modifica aquí"
                                }
                            )
                        },
                        placeholder = {
                            Text("Mínimo 6 caracteres")
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = usuarioEditando == null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Rol del usuario",
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rol == "Administrador",
                            onClick = { rol = "Administrador" },
                            enabled = usuarioEditando?.uid != usuarioActual?.uid
                        )
                        Text("Administrador")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rol == "Empleado",
                            onClick = { rol = "Empleado" },
                            enabled = usuarioEditando?.uid != usuarioActual?.uid
                        )
                        Text("Empleado")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cuenta activa")

                        Switch(
                            checked = activo,
                            onCheckedChange = { activo = it },
                            enabled = usuarioEditando?.uid != usuarioActual?.uid
                        )
                    }

                    if (errorFormulario.isNotBlank()) {
                        Text(
                            text = errorFormulario,
                            color = Color(0xFFB3261E),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        val nombreValido = nombre.trim()
                        val correoValido = correo.trim()
                        val anterior = usuarioEditando

                        when {
                            nombreValido.isBlank() -> {
                                errorFormulario = "Ingresa el nombre del usuario."
                            }

                            !Patterns.EMAIL_ADDRESS.matcher(correoValido).matches() -> {
                                errorFormulario = "Ingresa un correo válido."
                            }

                            anterior == null && contrasena.length < 6 -> {
                                errorFormulario = "La contraseña debe tener mínimo 6 caracteres."
                            }

                            usuarios.any {
                                it.correo.equals(correoValido, true) &&
                                        it.uid != anterior?.uid
                            } -> {
                                errorFormulario = "Este correo ya está registrado."
                            }

                            else -> {
                                guardando = true
                                errorFormulario = ""

                                scope.launch {
                                    try {
                                        if (anterior == null) {
                                            val nuevoId =
                                                (usuarios.maxOfOrNull { it.id } ?: 0) + 1

                                            adminManager.crearUsuario(
                                                id = nuevoId,
                                                nombre = nombreValido,
                                                correo = correoValido,
                                                contrasena = contrasena,
                                                rol = rol,
                                                activo = activo
                                            )
                                        } else {
                                            if (
                                                anterior.rol == "Administrador" &&
                                                anterior.activo &&
                                                administradoresActivos <= 1 &&
                                                (rol != "Administrador" || !activo)
                                            ) {
                                                throw IllegalStateException(
                                                    "No puedes quitar el rol o desactivar al último administrador activo."
                                                )
                                            }

                                            repository.actualizarUsuario(
                                                uid = anterior.uid,
                                                nombre = nombreValido,
                                                correo = anterior.correo,
                                                rol = rol
                                            )

                                            if (anterior.activo != activo) {
                                                repository.actualizarEstado(
                                                    uid = anterior.uid,
                                                    activo = activo
                                                )
                                            }
                                        }

                                        cargarUsuarios()
                                        mostrarFormulario = false
                                        usuarioEditando = null
                                        mensaje = if (anterior == null) {
                                            "Usuario registrado correctamente."
                                        } else {
                                            "Usuario actualizado correctamente."
                                        }
                                    } catch (e: Exception) {
                                        errorFormulario =
                                            e.localizedMessage ?: "Ocurrió un error al guardar."
                                    } finally {
                                        guardando = false
                                    }
                                }
                            }
                        }
                    }
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        mostrarFormulario = false
                        usuarioEditando = null
                        errorFormulario = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
