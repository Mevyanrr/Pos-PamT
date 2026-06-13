package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class ProdukRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getAll(): List<Produk> =
        supabase.postgrest["produk"]
            .select { order("created_at", Order.DESCENDING) }
            .decodeList()

    suspend fun tambah(nama: String, harga: Double, stok: Double, isActive: Boolean) {
        supabase.postgrest["produk"].insert(
            ProdukPayload(nama, harga, stok, isActive)
        )
    }

    suspend fun edit(id: String, nama: String, harga: Double, stok: Double, isActive: Boolean) {
        supabase.postgrest["produk"].update(
            ProdukPayload(nama, harga, stok, isActive)
        ) { filter { eq("id", id) } }
    }

    suspend fun hapus(id: String) {
        supabase.postgrest["produk"].delete { filter { eq("id", id) } }
    }

    suspend fun getLogProduk(): List<LogProduk> =
        supabase.postgrest["log_produk"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()
}