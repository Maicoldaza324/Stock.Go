
package com.example.stockgo.Data

import com.example.stockgo.Model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("productos")

    suspend fun obtenerProductos(): List<Producto> {
        val resultado = coleccion.get().await()

        return resultado.documents.mapNotNull { documento ->
            try {
                Producto(
                    codigo = documento.getString("codigo") ?: documento.id,
                    nombre = documento.getString("nombre") ?: "",
                    categoria = documento.getString("categoria") ?: "General",
                    stock = (documento.get("stock") as? Number)?.toInt() ?: 0,
                    stockMinimo = (documento.get("stockMinimo") as? Number)?.toInt() ?: 0,
                    precio = (documento.get("precio") as? Number)?.toDouble() ?: 0.0
                )
            } catch (error: Exception) {
                null
            }
        }
    }

    suspend fun guardarProducto(producto: Producto) {
        val datos = hashMapOf(
            "codigo" to producto.codigo,
            "nombre" to producto.nombre,
            "categoria" to producto.categoria,
            "stock" to producto.stock,
            "stockMinimo" to producto.stockMinimo,
            "precio" to producto.precio
        )

        coleccion.document(producto.codigo).set(datos).await()
    }

    suspend fun actualizarProducto(producto: Producto) {
        guardarProducto(producto)
    }

    suspend fun eliminarProducto(codigo: String) {
        coleccion.document(codigo).delete().await()
    }
}
