package com.pos.pamt.repository

import com.pos.pamt.data.Kas
import com.pos.pamt.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

class KasRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSemuaKas(): List<Kas> {
        return supabase.postgrest["kas"]
            .select()
            .decodeList<Kas>()
    }
}
