
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
import com.example.stockgo.Model.Producto
import com.example.stockgo.Screens.LoginScreen
import com.example.stockgo.Screens.DashboardScreen

private val AzulStock = Color(0xFF3044B5)
private val FondoStock = Color(0xFFF5F7FB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StockGoTheme {

                val productos = remember {
                    mutableStateListOf(
                        Producto(
                            "PROD-001",
                            "Mouse inalámbrico Logitech",
                            "Accesorios",
                            3,
                            10
                        ),
                        Producto(
                            "PROD-002",
                            "Resma papel A4 75g",
                            "Papelería",
                            0,
                            20
                        ),
                        Producto(
                            "PROD-003",
                            "Tóner HP LaserJet 85A",
                            "Impresión",
                            2,
                            5
                        ),
                        Producto(
                            "PROD-004",
                            "Teclado USB Logitech",
                            "Accesorios",
                            25,
                            5
                        ),
                        Producto(
                            "PROD-005",
                            "Cuaderno universitario",
                            "Papelería",
                            40,
                            10
                        )
                    )
                }

                var mostrarDashboard by remember {
                    mutableStateOf(false)
                }

                var pantallaActual by remember {
                    mutableStateOf("Inicio")
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoStock
                ) {
                    if (mostrarDashboard) {
                        when (pantallaActual) {
                            "Productos" -> ProductsScreen(
                                productos = productos
                            )

                            "Inicio" -> DashboardScreen(
                                onNavigate = { pantalla ->
                                    pantallaActual = pantalla
                                }
                            )

                            "Entradas", "Salidas", "Reportes", "Más" ->
                                ModulePlaceholderScreen(
                                    titulo = pantallaActual,
                                    onVolver = {
                                        pantallaActual = "Inicio"
                                    }
                                )

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
    }

    @Composable
    fun ProductsScreen(
        productos: androidx.compose.runtime.snapshots.SnapshotStateList<Producto>
    ) {


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
@Composable
fun ModulePlaceholderScreen(
    titulo: String,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoStock)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AzulStock
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Este módulo se implementará próximamente.",
            fontSize = 16.sp,
            color = Color(0xFF596273),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulStock
            )
        ) {
            Text("Volver al inicio")
        }
    }
}