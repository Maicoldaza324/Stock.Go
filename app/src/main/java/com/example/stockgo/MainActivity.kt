
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
import com.example.stockgo.Screens.ProductsScreen
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Screens.EntradasScreen

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
                val movimientos = remember {
                    mutableStateListOf<MovimientoInventario>()
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
                                productos = productos,
                                onNavigate = { pantalla ->
                                    pantallaActual = pantalla
                                }
                            )

                            "Inicio" -> DashboardScreen(
                                onNavigate = { pantalla ->
                                    pantallaActual = pantalla
                                }
                            )

                            "Entradas" -> EntradasScreen(
                                productos = productos,
                                movimientos = movimientos,
                                onNavigate = { pantalla -> pantallaActual = pantalla }
                            )

                            "Salidas", "Reportes", "Más" ->
                                ModulePlaceholderScreen(
                                    titulo = pantallaActual,
                                    onVolver = { pantallaActual = "Inicio" }
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