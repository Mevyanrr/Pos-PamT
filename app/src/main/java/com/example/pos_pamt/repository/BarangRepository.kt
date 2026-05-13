package com.example.pos_pamt.repository

import com.example.pos_pamt.data.SupabaseClientProvider
import com.example.pos_pamt.data.Barang
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class BarangRepository {
    private val supabase = SupabaseClientProvider.client
    suspend fun getSemuaBarang(): List<Barang> {
        return supabase
            .postgrest["barang"]
            .select(
                Columns.raw("*")
            )
            .decodeList<Barang>()
    }
}