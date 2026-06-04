package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class PenjualanRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getAll(): List<Penjualan> =
        supabase.postgrest["penjualan"]
            .select { order("waktu_penjualan", Order.DESCENDING) }
            .decodeList()

    suspend fun getDetail(penjualanId: String): List<PenjualanDetail> =
        supabase.postgrest["penjualan_detail"]
            .select { filter { eq("penjualan_id", penjualanId) } }
            .decodeList()

    suspend fun insertPenjualan(p: Penjualan): Penjualan =
        supabase.postgrest["penjualan"].insert(p) { select() }.decodeSingle()

    suspend fun insertDetail(d: PenjualanDetail) {
        supabase.postgrest["penjualan_detail"].insert(d)
    }
}
