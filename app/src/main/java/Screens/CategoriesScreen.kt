
package com.example.stockgo.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockgo.Model.Producto

private val AzulCategorias = Color(0xFF3044B5)
private val FondoCategorias = Color(0xFFF5F7FB)

@Composable
fun CategoriesScreen(
    categorias: SnapshotStateList<String>,
    productos: SnapshotStateList<Producto>,
    onNavigate: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var categoriaEditando by remember { mutableStateOf<String?>(null) }
    var nombreCategoria by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCategorias)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Categorías",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzulCategorias
            )

            Button(
                onClick = {
                    categoriaEditando = null
                    nombreCategoria = ""
                    error = ""
                    mostrarDialogo = true
                }
            ) {
                Text("+ Crear")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${categorias.size} categorías registradas",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (categorias.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Todavía no hay categorías. Crea la primera para comenzar.",
                    modifier = Modifier.padding(20.dp),
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categorias.toList()) { categoria ->
                    val cantidadProductos = productos.count {
                        it.categoria.equals(categoria, ignoreCase = true)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = categoria,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulCategorias
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$cantidadProductos productos asociados",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        categoriaEditando = categoria
                                        nombreCategoria = categoria
                                        error = ""
                                        mostrarDialogo = true
                                    }
                                ) {
                                    Text("Editar")
                                }

                                TextButton(
                                    onClick = {
                                        val tieneProductos = productos.any {
                                            it.categoria.equals(
                                                categoria,
                                                ignoreCase = true
                                            )
                                        }

                                        if (tieneProductos) {
                                            error = "No puedes eliminar una categoría que tiene productos asociados."
                                        } else {
                                            categorias.remove(categoria)
                                            error = ""
                                        }
                                    }
                                ) {
                                    Text(
                                        "Eliminar",
                                        color = Color(0xFFB3261E)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (error.isNotBlank() && !mostrarDialogo) {
            Text(
                text = error,
                color = Color(0xFFB3261E),
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onNavigate("Productos") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a productos")
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text(
                    if (categoriaEditando == null) {
                        "Crear categoría"
                    } else {
                        "Editar categoría"
                    }
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = nombreCategoria,
                        onValueChange = {
                            nombreCategoria = it
                            error = ""
                        },
                        label = { Text("Nombre de la categoría") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = Color(0xFFB3261E),
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val nombre = nombreCategoria.trim()

                        when {
                            nombre.isBlank() -> {
                                error = "Escribe un nombre para la categoría."
                            }

                            categorias.any {
                                it.equals(nombre, ignoreCase = true) &&
                                        !it.equals(categoriaEditando, ignoreCase = true)
                            } -> {
                                error = "Ya existe una categoría con ese nombre."
                            }

                            else -> {
                                if (categoriaEditando == null) {
                                    categorias.add(nombre)
                                } else {
                                    val indice = categorias.indexOfFirst {
                                        it.equals(
                                            categoriaEditando,
                                            ignoreCase = true
                                        )
                                    }

                                    if (indice != -1) {
                                        categorias[indice] = nombre

                                        productos.indices.forEach { i ->
                                            if (productos[i].categoria.equals(
                                                    categoriaEditando,
                                                    ignoreCase = true
                                                )
                                            ) {
                                                productos[i] = productos[i].copy(
                                                    categoria = nombre
                                                )
                                            }
                                        }
                                    }
                                }

                                mostrarDialogo = false
                                error = ""
                            }
                        }
                    }
                ) {
                    Text(
                        if (categoriaEditando == null) "Crear" else "Guardar"
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
