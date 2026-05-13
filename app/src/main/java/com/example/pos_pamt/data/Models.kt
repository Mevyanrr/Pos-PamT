// ============================================================
// File: data/Models.kt
// Model data untuk tabel barang dan kas di Supabase
// ============================================================
package com.pos.pamt.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * @Serializable diperlukan agar Supabase bisa otomatis
 * mengubah JSON response menjadi object Kotlin (dan sebaliknya).
 *
 * @SerialName("nama_kolom") digunakan jika nama variabel Kotlin
 * berbeda dengan nama kolom di tabel Supabase.
 *
 * Sesuaikan nama kolom (@SerialName) dengan nama kolom
 * di tabel Supabase project Anda.
 */

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
