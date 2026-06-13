package com.example.pos_pamt.repository

import com.example.pos_pamt.data.LogPelanggan
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.data.PelangganInsert
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
            PelangganInsert(nama = nama, noTelp = noTelp, isActive = isActive)
        )
    }

    suspend fun editPelanggan(id: String, nama: String, noTelp: String, isActive: Boolean) {
        supabase.postgrest["pelanggan"].update(
            PelangganInsert(nama = nama, noTelp = noTelp, isActive = isActive)
        ) {
            filter {
                eq("id", id)
            }
        }
    }
}