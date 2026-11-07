
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Usuario

private val AzulUsuarios = Color(0xFF3044B5)
private val FondoUsuarios = Color(0xFFF5F7FB)

@Composable
fun UsersScreen(
    usuarios: SnapshotStateList<Usuario>,
    usuarioActual: Usuario?,
    onNavigate: (String) -> Unit
) {
    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioEditando by remember { mutableStateOf<Usuario?>(null) }
    var usuarioEliminar by remember { mutableStateOf<Usuario?>(null) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Empleado") }
    var activo by remember { mutableStateOf(true) }
    var errorFormulario by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }

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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(usuarios, key = { it.id }) { usuario ->

                val esUsuarioActual = usuarioActual?.id == usuario.id

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
                            Column(modifier = Modifier.weight(1f)) {
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }

                            OutlinedButton(
                                onClick = {
                                    mensaje = ""
                                    usuarioEliminar = usuario
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !esUsuarioActual,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFB3261E)
                                )
                            ) {
                                Text("Eliminar")
                            }
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Estado de la cuenta",
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (esUsuarioActual) {
                                        "No puedes desactivar tu propia cuenta"
                                    } else {
                                        "Permitir o bloquear el acceso"
                                    },
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }

                            Switch(
                                checked = usuario.activo,
                                onCheckedChange = { nuevoEstado ->
                                    if (esUsuarioActual) {
                                        mensaje = "No puedes desactivar tu propia cuenta."
                                    } else {
                                        val indice = usuarios.indexOfFirst {
                                            it.id == usuario.id
                                        }

                                        if (indice != -1) {
                                            usuarios[indice] = usuario.copy(
                                                activo = nuevoEstado
                                            )
                                            mensaje = ""
                                        }
                                    }
                                },
                                enabled = !esUsuarioActual
                            )
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
                mostrarFormulario = false
                usuarioEditando = null
                errorFormulario = ""
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
                                    "Nueva contraseña (opcional)"
                                }
                            )
                        },
                        placeholder = {
                            Text(
                                if (usuarioEditando == null) {
                                    "Mínimo 6 caracteres"
                                } else {
                                    "Dejar vacío para conservarla"
                                }
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Rol del usuario",
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rol == "Administrador",
                            onClick = { rol = "Administrador" }
                        )
                        Text("Administrador")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = rol == "Empleado",
                            onClick = { rol = "Empleado" }
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
                            enabled = usuarioEditando?.id != usuarioActual?.id
                        )
                    }

                    if (usuarioEditando?.id == usuarioActual?.id) {
                        Text(
                            text = "No puedes cambiar el estado de tu propia cuenta.",
                            fontSize = 11.sp,
                            color = Color.Gray
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
                    onClick = {
                        val nombreValido = nombre.trim()
                        val correoValido = correo.trim()
                        val contrasenaValida = contrasena

                        errorFormulario = ""

                        when {
                            nombreValido.isBlank() -> {
                                errorFormulario = "Ingresa el nombre del usuario."
                            }

                            !Patterns.EMAIL_ADDRESS.matcher(correoValido).matches() -> {
                                errorFormulario = "Ingresa un correo válido."
                            }

                            usuarios.any {
                                it.correo.equals(correoValido, true) &&
                                        it.id != usuarioEditando?.id
                            } -> {
                                errorFormulario = "Este correo ya está registrado."
                            }

                            usuarioEditando == null && contrasenaValida.length < 6 -> {
                                errorFormulario = "La contraseña debe tener mínimo 6 caracteres."
                            }

                            contrasenaValida.isNotEmpty() &&
                                    contrasenaValida.length < 6 -> {
                                errorFormulario = "La contraseña debe tener mínimo 6 caracteres."
                            }

                            usuarioEditando?.id == usuarioActual?.id &&
                                    !activo -> {
                                errorFormulario = "No puedes desactivar tu propia cuenta."
                            }

                            else -> {
                                if (usuarioEditando == null) {
                                    val nuevoId =
                                        (usuarios.maxOfOrNull { it.id } ?: 0) + 1

                                    usuarios.add(
                                        Usuario(
                                            id = nuevoId,
                                            nombre = nombreValido,
                                            correo = correoValido,
                                            contrasena = contrasenaValida,
                                            rol = rol,
                                            activo = activo
                                        )
                                    )
                                } else {
                                    val anterior = usuarioEditando!!
                                    val indice = usuarios.indexOfFirst {
                                        it.id == anterior.id
                                    }

                                    if (indice != -1) {
                                        usuarios[indice] = anterior.copy(
                                            nombre = nombreValido,
                                            correo = correoValido,
                                            contrasena = if (contrasenaValida.isNotEmpty()) {
                                                contrasenaValida
                                            } else {
                                                anterior.contrasena
                                            },
                                            rol = rol,
                                            activo = if (anterior.id == usuarioActual?.id) {
                                                anterior.activo
                                            } else {
                                                activo
                                            }
                                        )
                                    }
                                }

                                mostrarFormulario = false
                                usuarioEditando = null
                                mensaje = ""
                            }
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
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

    // CONFIRMACIÓN DE ELIMINACIÓN
    usuarioEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = {
                usuarioEliminar = null
            },
            title = {
                Text("Eliminar usuario")
            },
            text = {
                Text("¿Deseas eliminar a ${usuario.nombre}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (usuario.id != usuarioActual?.id) {
                            usuarios.removeAll { it.id == usuario.id }
                            mensaje = ""
                        } else {
                            mensaje = "No puedes eliminar tu propia cuenta."
                        }
                        usuarioEliminar = null
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = Color(0xFFB3261E)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
