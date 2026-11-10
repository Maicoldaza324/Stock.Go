
package com.example.stockgo.Data

import com.example.stockgo.Model.MovimientoInventario
import com.example.stockgo.Model.Producto
import com.example.stockgo.Model.TipoMovimiento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MovimientoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun registrarMovimiento(
        producto: Producto,
        tipo: TipoMovimiento,
        cantidad: Int,
        observaciones: String = "",
        proveedor: String = ""
    ): MovimientoInventario {

        require(cantidad > 0) {
            "La cantidad debe ser mayor que cero."
        }

        val usuario = auth.currentUser
            ?: throw Exception("No hay un usuario autenticado.")

        val idMovimiento = "MOV-${UUID.randomUUID()}"
        val fechaActual = System.currentTimeMillis()

        val nombreUsuario = usuario.displayName
            ?: usuario.email
            ?: usuario.uid

        val productoRef = db.collection("productos")
            .document(producto.codigo)

        val movimientoRef = db.collection("movimientos")
            .document(idMovimiento)

        val movimiento = db.runTransaction { transaction ->

            val documento = transaction.get(productoRef)

            if (!documento.exists()) {
                throw Exception("El producto no existe.")
            }

            val stockActual =
                (documento.get("stock") as? Number)?.toInt() ?: 0

            val nuevoStock = when (tipo) {
                TipoMovimiento.ENTRADA -> stockActual + cantidad

                TipoMovimiento.SALIDA -> {
                    if (cantidad > stockActual) {
                        throw Exception("No hay existencias suficientes.")
                    }
                    stockActual - cantidad
                }
            }

            val nuevoMovimiento = MovimientoInventario(
                id = idMovimiento,
                codigoProducto = producto.codigo,
                nombreProducto = producto.nombre,
                tipo = tipo,
                cantidad = cantidad,
                fecha = fechaActual,
                observaciones = observaciones,
                proveedor = proveedor,
                usuarioUid = usuario.uid,
                usuarioNombre = nombreUsuario
            )

            transaction.update(productoRef, "stock", nuevoStock)

            transaction.set(
                movimientoRef,
                mapOf(
                    "id" to nuevoMovimiento.id,
                    "codigoProducto" to nuevoMovimiento.codigoProducto,
                    "nombreProducto" to nuevoMovimiento.nombreProducto,
                    "tipo" to nuevoMovimiento.tipo.name,
                    "cantidad" to nuevoMovimiento.cantidad,
                    "fecha" to nuevoMovimiento.fecha,
                    "observaciones" to nuevoMovimiento.observaciones,
                    "proveedor" to nuevoMovimiento.proveedor,
                    "usuarioUid" to nuevoMovimiento.usuarioUid,
                    "usuarioNombre" to nuevoMovimiento.usuarioNombre
                )
            )

            nuevoMovimiento
        }.await()

        return movimiento
    }

    suspend fun obtenerMovimientos(): List<MovimientoInventario> {
        val resultado = db.collection("movimientos")
            .orderBy("fecha")
            .get()
            .await()

        return resultado.documents.mapNotNull { documento ->

            val tipoTexto = documento.getString("tipo")
                ?: return@mapNotNull null

            val tipo = try {
                TipoMovimiento.valueOf(tipoTexto)
            } catch (e: IllegalArgumentException) {
                return@mapNotNull null
            }

            MovimientoInventario(
                id = documento.getString("id") ?: documento.id,
                codigoProducto = documento.getString("codigoProducto") ?: "",
                nombreProducto = documento.getString("nombreProducto") ?: "",
                tipo = tipo,
                cantidad = (documento.get("cantidad") as? Number)?.toInt() ?: 0,
                fecha = (documento.get("fecha") as? Number)?.toLong() ?: 0L,
                observaciones = documento.getString("observaciones") ?: "",
                proveedor = documento.getString("proveedor") ?: "",
                usuarioUid = documento.getString("usuarioUid") ?: "",
                usuarioNombre = documento.getString("usuarioNombre") ?: ""
            )
        }
    }
}
