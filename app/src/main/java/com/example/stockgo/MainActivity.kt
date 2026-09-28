
package com.example.stockgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.stockgo.ui.theme.StockGoTheme

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StockGoTheme {
                var mostrarDashboard by remember {
                    mutableStateOf(false)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoStock
                ) {
                    if (mostrarDashboard) {
                        DashboardScreen()
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                mostrarDashboard = true
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun LoginScreen(onLoginSuccess: () -> Unit) {
        var correo by remember { mutableStateOf("") }
        var contrasena by remember { mutableStateOf("") }
        var mostrarContrasena by remember { mutableStateOf(false) }
        var mensaje by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoStock)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado azul
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

            // Formulario de inicio de sesión
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

                    // Correo electrónico
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                Text("admin@empresa.com")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            )
                        )
                    }

                    // Contraseña
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                    // Recuperar contraseña
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = {
                                mensaje =
                                    "La recuperación de contraseña se implementará posteriormente."
                            }
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = AzulStock
                            )
                        }
                    }

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            if (correo.isBlank() || contrasena.isBlank()) {
                                mensaje = "Ingresa tu correo y contraseña."
                            } else {
                                mensaje = ""
                                onLoginSuccess()
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
                            color = AzulStock,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DashboardScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoStock)
        ) {
            // Encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        AzulStock,
                        RoundedCornerShape(
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = "Buenos días 👋",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Stock.Go",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Contenido del Dashboard
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjetas de estadísticas
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        titulo = "Total productos",
                        valor = "124",
                        color = AzulStock,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        titulo = "Disponibles",
                        valor = "98",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        titulo = "Bajo stock",
                        valor = "18",
                        color = Color(0xFFE5A323),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        titulo = "Agotados",
                        valor = "8",
                        color = Color(0xFFC33B3B),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Alertas
                Text(
                    text = "⚠ Atención requerida",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202536)
                )

                ProductAlert(
                    nombre = "Mouse inalámbrico Logitech",
                    detalle = "Stock: 3 · Mínimo: 10",
                    estado = "Bajo stock",
                    color = Color(0xFFE5A323)
                )

                ProductAlert(
                    nombre = "Resma papel A4 75g",
                    detalle = "Stock: 0 · Mínimo: 20",
                    estado = "Agotado",
                    color = Color(0xFFC33B3B)
                )

                ProductAlert(
                    nombre = "Tóner HP LaserJet 85A",
                    detalle = "Stock: 2 · Mínimo: 5",
                    estado = "Bajo stock",
                    color = Color(0xFFE5A323)
                )

                // Acciones rápidas
                Text(
                    text = "Acciones rápidas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202536)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE7F5E9),
                            contentColor = Color(0xFF388E3C)
                        )
                    ) {
                        Text("↑ Entrada")
                    }

                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFBE9E7),
                            contentColor = Color(0xFFC33B3B)
                        )
                    ) {
                        Text("↓ Salida")
                    }
                }
            }

            // Barra de navegación inferior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "⌂\nInicio",
                    "▣\nProductos",
                    "↑\nEntradas",
                    "↓\nSalidas",
                    "▥\nReportes",
                    "•••\nMás"
                ).forEachIndexed { index, item ->
                    Text(
                        text = item,
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        color = if (index == 0) AzulStock
                        else Color(0xFF858B98)
                    )
                }
            }
        }
    }

    @Composable
    fun StatCard(
        titulo: String,
        valor: String,
        color: Color,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(18.dp),
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
                    text = valor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    color = Color(0xFF596273)
                )
            }
        }
    }

    @Composable
    fun ProductAlert(
        nombre: String,
        detalle: String,
        estado: String,
        color: Color
    ) {
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = nombre,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF202536)
                    )

                    Text(
                        text = detalle,
                        fontSize = 13.sp,
                        color = Color(0xFF858B98)
                    )
                }

                Text(
                    text = estado,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}