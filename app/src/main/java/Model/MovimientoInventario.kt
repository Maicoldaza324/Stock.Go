package com.example.stockgo.Model

data class MovimientoInventario(
    val id: String,
    val codigoProducto: String,
    val nombreProducto: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val fecha: Long,
    val observaciones: String = ""
)

enum class TipoMovimiento {
    ENTRADA,
    SALIDA
}