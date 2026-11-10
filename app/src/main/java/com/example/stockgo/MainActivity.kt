
package com.example.stockgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.Usuario
import com.example.stockgo.Screens.CategoriesScreen
import com.example.stockgo.Screens.DashboardScreen
import com.example.stockgo.Screens.EntradasScreen
import com.example.stockgo.Screens.HistorialScreen
import com.example.stockgo.Screens.LoginScreen
import com.example.stockgo.Screens.MoreOptionsScreen
import com.example.stockgo.Screens.ProductsScreen
import com.example.stockgo.Screens.ProfileScreen
import com.example.stockgo.Screens.ReportsScreen
import com.example.stockgo.Screens.SalidasScreen
import com.example.stockgo.Screens.UsersScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var pantallaActual by remember {
                mutableStateOf("Login")
            }

            // Preferencia global de notificaciones
            var notificacionesActivadas by remember {
                mutableStateOf(true)
            }

            val productos = remember {
                mutableStateListOf<Producto>()
            }

            val movimientos = remember {
                mutableStateListOf<MovimientoInventario>()
            }

            val categorias = remember {
                mutableStateListOf("General")
            }

            val usuarios = remember {
                mutableStateListOf(
                    Usuario(
                        id = 1,
                        nombre = "Administrador",
                        correo = "admin@stockgo.com",
                        contrasena = "Admin123*",
                        rol = "Administrador",
                        activo = true
                    ),
                    Usuario(
                        id = 2,
                        nombre = "Empleado",
                        correo = "empleado@stockgo.com",
                        contrasena = "Empleado123*",
                        rol = "Empleado",
                        activo = true
                    )
                )
            }

            var usuarioActual by remember {
                mutableStateOf<Usuario?>(null)
            }

            val esAdministrador = usuarioActual?.rol
                ?.equals("Administrador", ignoreCase = true) == true

            MaterialTheme(
                colorScheme = lightColorScheme()
            ) {

                // Navegación centralizada
                val navegarA: (String) -> Unit = { destino ->

                    val pantallasRestringidas = listOf(
                        "Categorias",
                        "Usuarios"
                    )

                    if (destino in pantallasRestringidas && !esAdministrador) {
                        pantallaActual = "Inicio"
                    } else {
                        pantallaActual = destino
                    }
                }

                when (pantallaActual) {

                    "Login" -> {
                        LoginScreen(
                            onLoginSuccess = { usuario ->
                                usuarioActual = usuario
                                pantallaActual = "Inicio"
                            }
                        )
                    }

                    "Inicio" -> {
                        DashboardScreen(
                            productos = productos,
                            onNavigate = navegarA
                        )
                    }

                    "Productos" -> {
                        ProductsScreen(
                            productos = productos,
                            categorias = categorias,
                            esAdministrador = esAdministrador,
                            onNavigate = navegarA
                        )
                    }

                    "Categorias" -> {
                        if (esAdministrador) {
                            CategoriesScreen(
                                categorias = categorias,
                                productos = productos,
                                onNavigate = navegarA
                            )
                        } else {
                            pantallaActual = "Inicio"
                        }
                    }

                    "Entradas" -> {
                        EntradasScreen(
                            productos = productos,
                            movimientos = movimientos,
                            onNavigate = navegarA
                        )
                    }

                    "Salidas" -> {
                        SalidasScreen(
                            productos = productos,
                            movimientos = movimientos,
                            onNavigate = navegarA
                        )
                    }

                    "Historial" -> {
                        HistorialScreen(
                            movimientos = movimientos,
                            onNavigate = navegarA
                        )
                    }

                    "Reportes" -> {
                        ReportsScreen(
                            productos = productos,
                            movimientos = movimientos,
                            onNavigate = navegarA
                        )
                    }

                    "Más" -> {
                        MoreOptionsScreen(
                            rol = usuarioActual?.rol ?: "Empleado",
                            onNavigate = navegarA
                        )
                    }

                    "Perfil", "Configuracion" -> {

                        val usuario = usuarioActual

                        if (usuario != null) {

                            ProfileScreen(
                                usuario = usuario,

                                onActualizarPerfil = { nombre, correo ->

                                    val indice = usuarios.indexOfFirst {
                                        it.id == usuario.id
                                    }

                                    if (indice != -1) {
                                        val actualizado = usuarios[indice].copy(
                                            nombre = nombre,
                                            correo = correo
                                        )

                                        usuarios[indice] = actualizado
                                        usuarioActual = actualizado
                                    }
                                },

                                onLogout = {
                                    usuarioActual = null
                                    pantallaActual = "Login"
                                },

                                onNavigate = navegarA,

                                notificaciones = notificacionesActivadas,

                                onNotificacionesChange = {
                                    notificacionesActivadas = it
                                },

                                correoDisponible = { correo ->

                                    usuarios.none {
                                        it.id != usuario.id &&
                                                it.correo.equals(
                                                    correo.trim(),
                                                    ignoreCase = true
                                                )
                                    }
                                }
                            )

                        } else {
                            pantallaActual = "Login"
                        }
                    }

                    "Usuarios" -> {
                        if (esAdministrador) {
                            UsersScreen(
                                usuarios = usuarios,
                                usuarioActual = usuarioActual,
                                onNavigate = navegarA
                            )
                        } else {
                            pantallaActual = "Inicio"
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Pantalla: $pantallaActual",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Button(
                                onClick = {
                                    pantallaActual = "Inicio"
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Volver al inicio")
                            }
                        }
                    }
                }
            }
        }
    }
}
