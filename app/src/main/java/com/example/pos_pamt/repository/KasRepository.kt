package com.example.pos_pamt.repository

import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.data.LogKas
import com.example.pos_pamt.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class KasRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getSemuaKas(): List<Kas> {
        return supabase.postgrest["kas"]
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<Kas>()
    }

    suspend fun getLogKas(): List<LogKas> {
        return supabase.postgrest["log_kas"]
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<LogKas>()
    }
}