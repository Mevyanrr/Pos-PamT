package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class LogRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getLogKas(): List<LogKas> =
        supabase.postgrest["log_kas"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()

    suspend fun getLogPelanggan(): List<LogPelanggan> =
        supabase.postgrest["log_pelanggan"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()

    suspend fun insertLogPelanggan(log: LogPelanggan) {
        supabase.postgrest["log_pelanggan"].insert(log)
    }

    suspend fun getLogProduk(): List<LogProduk> =
        supabase.postgrest["log_produk"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()

    suspend fun getLogPengeluaran(): List<LogPengeluaran> =
        supabase.postgrest["log_pengeluaran"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()
}
