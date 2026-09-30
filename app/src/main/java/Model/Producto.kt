package com.example.stockgo.Model

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int
)