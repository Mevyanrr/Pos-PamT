package com.example.pos_pamt.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Barang(
    val id : String  = "",
    val nama : String  = "",
    val harga : Double  = 0.0,
    val stok : Double  = 0.0,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class Kas(
    val id : String  = "",
    @SerialName("nama_kas")
    val nama : String  = "",
    val saldo : Double  = 0.0,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class Pelanggan(
    val id: String = "",
    val nama: String = "",
    @SerialName("no_telp")
    val noTelp: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)
