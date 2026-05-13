package com.pos.pamt.repository

import com.pos.pamt.data.Barang
import com.pos.pamt.data.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

class BarangRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSemuaBarang(): List<Barang> {
        return supabase.postgrest["barang"]
            .select()
            .decodeList<Barang>()
    }
}
