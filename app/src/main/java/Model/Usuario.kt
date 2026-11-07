
package com.example.stockgo.Model

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val activo: Boolean = true
)
