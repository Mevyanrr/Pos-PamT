package com.example.pos_pamt.repository

import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.data.KasInsert
import com.example.pos_pamt.data.KasUpdate
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
        supabase.postgrest["kas"].insert(KasInsert(nama = nama, saldo = saldo))
    }

    suspend fun editKas(id: String, nama: String, saldo: Double, isActive: Boolean) {
        supabase.postgrest["kas"].update(KasUpdate(nama = nama, saldo = saldo, is_active = isActive)) {
            filter { eq("id", id) }
        }
    }

    suspend fun hapusKas(id: String) {
        supabase.postgrest["kas"].delete { filter { eq("id", id) } }
    }
}