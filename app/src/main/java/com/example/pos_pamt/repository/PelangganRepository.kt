package com.example.pos_pamt.repository

import com.example.pos_pamt.data.LogPelanggan
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class PelangganRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getSemuaPelanggan(): List<Pelanggan> =
        supabase.postgrest["pelanggan"]
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<Pelanggan>()

    suspend fun getLogPelanggan(): List<LogPelanggan> =
        supabase.postgrest["log_pelanggan"]
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<LogPelanggan>()

    suspend fun tambahPelanggan(nama: String, noTelp: String, isActive: Boolean) {
        supabase.postgrest["pelanggan"].insert(
            mapOf("nama" to nama, "no_telp" to noTelp, "is_active" to isActive)
        )
    }

    suspend fun editPelanggan(id: String, nama: String, noTelp: String, isActive: Boolean) {
        supabase.postgrest["pelanggan"].update(
            mapOf("nama" to nama, "no_telp" to noTelp, "is_active" to isActive)
        ) { filter { eq("id", id) } }
    }

    suspend fun hapusPelanggan(id: String) {
        supabase.postgrest["pelanggan"].delete { filter { eq("id", id) } }
    }
}