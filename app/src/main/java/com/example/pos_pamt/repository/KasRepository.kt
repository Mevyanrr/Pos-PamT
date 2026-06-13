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

    suspend fun tambahKas(nama: String, saldo: Double) {
        supabase.postgrest["kas"].insert(
            mapOf(
                "nama"      to nama,
                "saldo"     to saldo,
                "is_active" to true
            )
        )
    }

    suspend fun editKas(id: String, nama: String, saldo: Double, isActive: Boolean) {
        supabase.postgrest["kas"].update(
            mapOf(
                "nama"      to nama,
                "saldo"     to saldo,
                "is_active" to isActive
            )
        ) { filter { eq("id", id) } }
    }

    suspend fun hapusKas(id: String) {
        supabase.postgrest["kas"].delete { filter { eq("id", id) } }
    }
}