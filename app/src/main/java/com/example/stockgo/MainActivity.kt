package com.example.stockgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Screens.DashboardScreen
import com.example.stockgo.Screens.EntradasScreen
import com.example.stockgo.Screens.LoginScreen
import com.example.stockgo.Screens.ProductsScreen
import com.example.stockgo.Screens.ReportsScreen
import com.example.stockgo.Screens.SalidasScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var pantallaActual by remember {
                mutableStateOf("Login")
            }

            val productos = remember {
                mutableStateListOf<Producto>()
            }

            val movimientos = remember {
                mutableStateListOf<MovimientoInventario>()
            }

            when (pantallaActual) {

                "Login" -> {
                    LoginScreen(
                        onLoginSuccess = {
                            pantallaActual = "Inicio"
                        }
                    )
                }

                "Inicio" -> {
                    DashboardScreen(
                        onNavigate = { destino ->
                            pantallaActual = destino
                        }
                    )
                }

                "Productos" -> {
                    ProductsScreen(
                        productos = productos,
                        onNavigate = { destino ->
                            pantallaActual = destino
                        }
                    )
                }

                "Entradas" -> {
                    EntradasScreen(
                        productos = productos,
                        movimientos = movimientos,
                        onNavigate = { destino ->
                            pantallaActual = destino
                        }
                    )
                }

                "Salidas" -> {
                    SalidasScreen(
                        productos = productos,
                        movimientos = movimientos,
                        onNavigate = { destino ->
                            pantallaActual = destino
                        }
                    )
                }

                "Reportes" -> {
                    ReportsScreen(
                        productos = productos,
                        movimientos = movimientos,
                        onNavigate = { destino ->
                            pantallaActual = destino
                        }
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Pantalla: $pantallaActual")

                        Button(
                            onClick = {
                                pantallaActual = "Inicio"
                            }
                        ) {
                            Text("Volver al inicio")
                        }
                    }
                }
            }
        }
    }
}