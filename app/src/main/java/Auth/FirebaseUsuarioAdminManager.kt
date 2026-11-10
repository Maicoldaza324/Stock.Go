
package com.example.stockgo.Auth

import android.content.Context
import com.example.stockgo.Data.UsuarioRepository
import com.example.stockgo.Model.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import android.util.Log


class FirebaseUsuarioAdminManager(context: Context) {

    private val appSecundaria: FirebaseApp

    private val authSecundario: FirebaseAuth

    private val usuarioRepository = UsuarioRepository()

    init {
        val contexto = context.applicationContext
        val nombreApp = "StockGoSecondary"

        appSecundaria = FirebaseApp.getApps(contexto)
            .firstOrNull { it.name == nombreApp }
            ?: FirebaseApp.initializeApp(
                contexto,
                FirebaseApp.getInstance().options,
                nombreApp
            )!!

        authSecundario = FirebaseAuth.getInstance(appSecundaria)
    }

    suspend fun crearUsuario(
        id: Int,
        nombre: String,
        correo: String,
        contrasena: String,
        rol: String,
        activo: Boolean
    ): Usuario {

        require(nombre.isNotBlank()) {
            "El nombre es obligatorio."
        }

        require(correo.isNotBlank()) {
            "El correo es obligatorio."
        }

        require(contrasena.length >= 6) {
            "La contraseña debe tener mínimo 6 caracteres."
        }

        require(rol == "Administrador" || rol == "Empleado") {
            "El rol seleccionado no es válido."
        }

        authSecundario.signOut()

        var uidCreado: String? = null

        try {
            val resultado = authSecundario
                .createUserWithEmailAndPassword(
                    correo.trim(),
                    contrasena
                )
                .await()

            val usuarioFirebase = resultado.user
                ?: throw IllegalStateException(
                    "No se pudo obtener el usuario creado."
                )

            uidCreado = usuarioFirebase.uid

            val nuevoUsuario = Usuario(
                id = id,
                uid = usuarioFirebase.uid,
                nombre = nombre.trim(),
                correo = correo.trim(),
                contrasena = "",
                rol = rol,
                activo = activo
            )

            val usuarioPrincipal = FirebaseAuth.getInstance().currentUser

            Log.d(
                "STOCKGO_AUTH",
                "Usuario principal: ${usuarioPrincipal?.uid} | Correo: ${usuarioPrincipal?.email}"
            )

            Log.d(
                "STOCKGO_AUTH",
                "UID empleado creado: ${usuarioFirebase.uid}"
            )

            usuarioRepository.guardarUsuario(nuevoUsuario)

            return nuevoUsuario

        } catch (error: Exception) {

            // Si falla Firestore, intentamos eliminar
            // la cuenta recién creada para evitar inconsistencias.
            if (uidCreado != null) {
                runCatching {
                    authSecundario.currentUser
                        ?.takeIf { it.uid == uidCreado }
                        ?.delete()
                        ?.await()
                }
            }

            throw error

        } finally {
            authSecundario.signOut()
        }
    }
}
