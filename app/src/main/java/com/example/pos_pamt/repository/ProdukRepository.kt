package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest

// RLS produk:
// - kasir : SELECT only
// - admin : full (SELECT, INSERT, UPDATE, DELETE)
class ProdukRepository {
    private val supabase = SupabaseClientProvider.client
    suspend fun getAll(): List<Produk> =
        supabase.postgrest["produk"].select().decodeList()
}
