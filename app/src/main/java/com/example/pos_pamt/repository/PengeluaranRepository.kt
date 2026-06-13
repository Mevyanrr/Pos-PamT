package com.example.pos_pamt.repository

import com.example.pos_pamt.data.*
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PengeluaranRepository {
    private val supabase = SupabaseClientProvider.client

    suspend fun getAll(): List<Pengeluaran> =
        supabase.postgrest["pengeluaran"]
            .select { order("tanggal", Order.DESCENDING) }
            .decodeList()

    suspend fun getKasList(): List<Kas> =
        supabase.postgrest["kas"]
            .select { filter { eq("is_active", true) } }
            .decodeList()

    suspend fun tambah(kasId: String, deskripsi: String, total: Double, tanggal: String, status: String) {
        supabase.postgrest["pengeluaran"].insert(
            buildJsonObject {
                put("kas_id",    kasId)
                put("deskripsi", deskripsi)
                put("total",     total)
                put("tanggal",   tanggal)
                put("status",    status)
            }
        )
    }

    suspend fun update(id: String, kasId: String, deskripsi: String, total: Double, tanggal: String, status: String) {
        supabase.postgrest["pengeluaran"].update(
            buildJsonObject {
                put("kas_id",    kasId)
                put("deskripsi", deskripsi)
                put("total",     total)
                put("tanggal",   tanggal)
                put("status",    status)
            }
        ) { filter { eq("id", id) } }
    }

    suspend fun batal(id: String) {
        supabase.postgrest["pengeluaran"].update(
            buildJsonObject { put("status", "batal") }
        ) { filter { eq("id", id) } }
    }

    suspend fun hapus(id: String) {
        supabase.postgrest["pengeluaran"]
            .delete { filter { eq("id", id) } }
    }

    suspend fun getLogPengeluaran(): List<LogPengeluaran> =
        supabase.postgrest["log_pengeluaran"]
            .select { order("created_at", Order.DESCENDING); limit(15) }
            .decodeList()
}