package com.example.pos_pamt.repository

import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class PelangganRepository {
    private val supabase = SupabaseClientProvider.client
    suspend fun getSemuaPelanggan(): List<Pelanggan> {
        return supabase.postgrest["pelanggan"]
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Pelanggan>()
    }
}
