package com.pos.pamt.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Barang(
    val id: Int = 0,
    val nama: String = "",
    val harga: Long = 0,
    val stok: Int = 0,
    val kategori: String = "",

    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class Kas(
    val id: Int = 0,
    val nama: String = "",
    val saldo: Long = 0,

    @SerialName("is_active")
    val isActive: Boolean = true
)
