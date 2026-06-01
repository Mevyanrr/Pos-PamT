package com.example.pos_pamt.repository

import com.example.pos_pamt.data.SupabaseClientProvider
import com.example.pos_pamt.data.Kas
import io.github.jan.supabase.postgrest.postgrest

class KasRepository {
    private val supabase = SupabaseClientProvider.client
    suspend fun getSemuaKas(): List<Kas> {
        return supabase.postgrest["kas"]
            .select()
            .decodeList<Kas>()
    }
}
