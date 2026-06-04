package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class PengeluaranRepository {
    private val supabase = SupabaseClientProvider.client
    suspend fun getAll(): List<Pengeluaran> =
        supabase.postgrest["pengeluaran"]
            .select { order("tanggal", Order.DESCENDING) }
            .decodeList()
}
