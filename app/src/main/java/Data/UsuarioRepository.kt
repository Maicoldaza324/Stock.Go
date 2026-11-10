
package com.example.stockgo.Data

import com.example.stockgo.Model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("usuarios")

    // Consultar el perfil de un usuario autenticado
    suspend fun obtenerUsuario(uid: String): Usuario? {

        val documento = coleccion
            .document(uid)
            .get()
            .await()

        if (!documento.exists()) return null

        val idValor = documento.get("id")

        val idUsuario = when (idValor) {
            is Number -> idValor.toInt()
            is String -> idValor.toIntOrNull() ?: 0
            else -> 0
        }

        return Usuario(
            id = idUsuario,
            uid = uid,
            nombre = documento.getString("nombre") ?: "",
            correo = documento.getString("correo") ?: "",
            contrasena = "",
            rol = documento.getString("rol") ?: "Empleado",
            activo = documento.getBoolean("activo") ?: true
        )
    }

    // Consultar todos los perfiles
    suspend fun obtenerUsuarios(): List<Usuario> {

        val resultado = coleccion.get().await()

        return resultado.documents.mapNotNull { documento ->

            val idValor = documento.get("id")

            val idUsuario = when (idValor) {
                is Number -> idValor.toInt()
                is String -> idValor.toIntOrNull() ?: 0
                else -> 0
            }

            Usuario(
                id = idUsuario,
                uid = documento.id,
                nombre = documento.getString("nombre") ?: "",
                correo = documento.getString("correo") ?: "",
                contrasena = "",
                rol = documento.getString("rol") ?: "Empleado",
                activo = documento.getBoolean("activo") ?: true
            )
        }
    }

    // Guardar un perfil en Firestore
    suspend fun guardarUsuario(usuario: Usuario) {

        require(usuario.uid.isNotBlank()) {
            "El UID del usuario es obligatorio."
        }

        val datos = hashMapOf(
            "uid" to usuario.uid,
            "id" to usuario.id,
            "nombre" to usuario.nombre,
            "correo" to usuario.correo,
            "rol" to usuario.rol,
            "activo" to usuario.activo
        )

        coleccion.document(usuario.uid)
            .set(datos)
            .await()
    }

    // Actualizar nombre y correo
    suspend fun actualizarPerfil(
        uid: String,
        nombre: String,
        correo: String
    ) {

        coleccion.document(uid).update(
            mapOf(
                "nombre" to nombre,
                "correo" to correo
            )
        ).await()
    }

    // Actualizar los datos administrativos
    suspend fun actualizarUsuario(
        uid: String,
        nombre: String,
        correo: String,
        rol: String
    ) {

        coleccion.document(uid).update(
            mapOf(
                "nombre" to nombre,
                "correo" to correo,
                "rol" to rol
            )
        ).await()
    }

    // Activar o desactivar una cuenta
    suspend fun actualizarEstado(
        uid: String,
        activo: Boolean
    ) {

        coleccion.document(uid).update(
            "activo",
            activo
        ).await()
    }
}
