
package com.example.stockgo.Data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CategoriaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("categorias")

    suspend fun obtenerCategorias(): List<String> {
        val resultado = coleccion.get().await()

        return resultado.documents.mapNotNull {
            it.getString("nombre")
        }.sortedBy { it.lowercase() }
    }

    suspend fun crearCategoria(nombre: String) {
        val existente = coleccion
            .whereEqualTo("nombreNormalizado", nombre.trim().lowercase())
            .get()
            .await()

        if (!existente.isEmpty) {
            throw Exception("Ya existe una categoría con ese nombre.")
        }

        val id = UUID.randomUUID().toString()

        coleccion.document(id).set(
            mapOf(
                "id" to id,
                "nombre" to nombre.trim(),
                "nombreNormalizado" to nombre.trim().lowercase()
            )
        ).await()
    }

    suspend fun actualizarCategoria(
        nombreAnterior: String,
        nombreNuevo: String
    ) {
        val nombreNormalizado = nombreNuevo.trim().lowercase()

        val duplicada = coleccion
            .whereEqualTo("nombreNormalizado", nombreNormalizado)
            .get()
            .await()

        if (duplicada.documents.any {
                it.getString("nombre")?.equals(
                    nombreAnterior,
                    ignoreCase = true
                ) != true
            }) {
            throw Exception("Ya existe una categoría con ese nombre.")
        }

        val categoriaAnterior = coleccion
            .whereEqualTo("nombre", nombreAnterior)
            .get()
            .await()

        val productos = db.collection("productos")
            .whereEqualTo("categoria", nombreAnterior)
            .get()
            .await()

        val batch = db.batch()

        categoriaAnterior.documents.forEach { documento ->
            batch.update(
                documento.reference,
                mapOf(
                    "nombre" to nombreNuevo.trim(),
                    "nombreNormalizado" to nombreNormalizado
                )
            )
        }

        productos.documents.forEach { documento ->
            batch.update(
                documento.reference,
                "categoria",
                nombreNuevo.trim()
            )
        }

        batch.commit().await()
    }

    suspend fun eliminarCategoria(nombre: String) {
        val resultado = coleccion
            .whereEqualTo("nombre", nombre)
            .get()
            .await()

        val batch = db.batch()

        resultado.documents.forEach { documento ->
            batch.delete(documento.reference)
        }

        batch.commit().await()
    }
}
