
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
import androidx.compose.material3.AlertDialog

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
                        var pantallaActual by remember {
                            mutableStateOf("Inicio")
                        }

                        when (pantallaActual) {
                            "Productos" -> ProductsScreen()
                            else -> DashboardScreen(
                                onNavigate = { pantalla ->
                                    pantallaActual = pantalla
                                }
                            )
                        }
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
    fun DashboardScreen(
        onNavigate: (String) -> Unit
    ) {
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
                val opciones = listOf(
                    "Inicio",
                    "Productos",
                    "Entradas",
                    "Salidas",
                    "Reportes",
                    "Más"
                )

                opciones.forEach { opcion ->
                    TextButton(
                        onClick = {
                            onNavigate(opcion)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = 2.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text(
                            text = opcion,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            color = if (opcion == "Inicio") {
                                AzulStock
                            } else {
                                Color(0xFF858B98)
                            }
                        )
                    }
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

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int
)

@Composable
fun ProductsScreen() {
    val productos = remember {
        mutableStateListOf(
            Producto("PROD-001", "Mouse inalámbrico Logitech", "Accesorios", 3, 10),
            Producto("PROD-002", "Resma papel A4 75g", "Papelería", 0, 20),
            Producto("PROD-003", "Tóner HP LaserJet 85A", "Impresión", 2, 5),
            Producto("PROD-004", "Teclado USB Logitech", "Accesorios", 25, 5),
            Producto("PROD-005", "Cuaderno universitario", "Papelería", 40, 10)
        )
    }

    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val productosFiltrados = productos.filter { producto ->
        val coincideBusqueda =
            producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true)

        val coincideFiltro = when (filtro) {
            "Bajo stock" -> producto.stock > 0 &&
                    producto.stock <= producto.stockMinimo
            "Agotados" -> producto.stock == 0
            else -> true
        }

        coincideBusqueda && coincideFiltro
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AzulStock)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Productos",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Button(
                onClick = { mostrarDialogo = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AzulStock
                )
            ) {
                Text("+ Agregar")
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar producto...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todos", "Bajo stock", "Agotados").forEach { opcion ->
                    Button(
                        onClick = { filtro = opcion },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (filtro == opcion) {
                                AzulStock
                            } else {
                                Color.White
                            },
                            contentColor = if (filtro == opcion) {
                                Color.White
                            } else {
                                AzulStock
                            }
                        )
                    ) {
                        Text(
                            text = opcion,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Text(
                text = "${productosFiltrados.size} productos encontrados",
                color = Color(0xFF858B98),
                fontSize = 14.sp
            )

            // Lista de productos
            if (productosFiltrados.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "No se encontraron productos.",
                        modifier = Modifier.padding(20.dp),
                        color = Color(0xFF596273)
                    )
                }
            } else {
                productosFiltrados.forEach { producto ->
                    ProductItem(
                        producto = producto,
                        onEliminar = {
                            productos.remove(producto)
                        }
                    )
                }
            }
        }
    }

    // Formulario para agregar productos
    if (mostrarDialogo) {
        AddProductDialog(
            onDismiss = { mostrarDialogo = false },
            onAgregar = { nuevoProducto ->
                productos.add(nuevoProducto)
                mostrarDialogo = false
            },
            siguienteCodigo = "PROD-${(productos.size + 1).toString().padStart(3, '0')}"
        )
    }
}

@Composable
fun ProductItem(
    producto: Producto,
    onEliminar: () -> Unit
) {
    val estado = when {
        producto.stock == 0 -> "Agotado"
        producto.stock <= producto.stockMinimo -> "Bajo stock"
        else -> "Disponible"
    }

    val colorEstado = when (estado) {
        "Agotado" -> Color(0xFFC33B3B)
        "Bajo stock" -> Color(0xFFE5A323)
        else -> Color(0xFF4CAF50)
    }

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = producto.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF202536)
            )

            Text(
                text = "${producto.codigo} · ${producto.categoria}",
                fontSize = 13.sp,
                color = Color(0xFF858B98)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Stock: ${producto.stock}",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF202536)
                    )
                    Text(
                        text = "Mínimo: ${producto.stockMinimo}",
                        fontSize = 12.sp,
                        color = Color(0xFF858B98)
                    )
                }

                Text(
                    text = estado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )
            }

            TextButton(
                onClick = onEliminar,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Eliminar",
                    color = Color(0xFFC33B3B)
                )
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAgregar: (Producto) -> Unit,
    siguienteCodigo: String
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Agregar producto")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Cantidad disponible") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stockMinimo,
                    onValueChange = { stockMinimo = it },
                    label = { Text("Stock mínimo") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        color = Color(0xFFC33B3B),
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cantidad = stock.toIntOrNull()
                    val minimo = stockMinimo.toIntOrNull()

                    if (nombre.isBlank() || categoria.isBlank()) {
                        error = "Completa el nombre y la categoría."
                    } else if (
                        cantidad == null || minimo == null ||
                        cantidad < 0 || minimo < 0
                    ) {
                        error = "Ingresa cantidades válidas."
                    } else {
                        onAgregar(
                            Producto(
                                codigo = siguienteCodigo,
                                nombre = nombre.trim(),
                                categoria = categoria.trim(),
                                stock = cantidad,
                                stockMinimo = minimo
                            )
                        )
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}