
package com.example.stockgo.Auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun obtenerUsuarioActual() = auth.currentUser

    suspend fun iniciarSesionCorreo(
        correo: String,
        contrasena: String
    ) = auth.signInWithEmailAndPassword(
        correo.trim(),
        contrasena
    ).await()

    suspend fun registrarUsuario(
        correo: String,
        contrasena: String
    ) = auth.createUserWithEmailAndPassword(
        correo.trim(),
        contrasena
    ).await()

    suspend fun iniciarSesionGoogle(
        idToken: String
    ) = auth.signInWithCredential(
        GoogleAuthProvider.getCredential(idToken, null)
    ).await()

    fun cerrarSesion() {
        auth.signOut()
    }
}
