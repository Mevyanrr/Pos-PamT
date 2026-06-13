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

    suspend fun getPelanggan(): List<Pelanggan> =
        supabase.postgrest["pelanggan"]
            .select { filter { eq("is_active", true) } }
            .decodeList()

    suspend fun getKas(): List<Kas> =
        supabase.postgrest["kas"]
            .select { filter { eq("is_active", true) } }
            .decodeList()

    suspend fun getProduk(): List<Produk> =
        supabase.postgrest["produk"]
            .select { filter { eq("is_active", true) } }
            .decodeList()

    suspend fun simpanTransaksi(
        pelangganId: String,
        kasId: String,
        kasirId: String,
        produkId: String,
        hargaSatuan: Double,
        qty: Double,
        jumlahBayar: Double
    ) {
        val subtotal = hargaSatuan * qty
        val kembalian = jumlahBayar - subtotal

        val penjualan = supabase.postgrest["penjualan"].insert(
            mapOf(
                "pelanggan_id" to pelangganId,
                "kas_id" to kasId,
                "kasir_id" to kasirId,
                "total" to subtotal,
                "jumlah_bayar" to jumlahBayar,
                "kembalian" to kembalian,
                "waktu_penjualan" to "now()"
            )
        ) { select() }.decodeSingle<Penjualan>()

        supabase.postgrest["penjualan_detail"].insert(
            mapOf(
                "penjualan_id" to penjualan.id,
                "produk_id" to produkId,
                "harga_satuan" to hargaSatuan,
                "qty" to qty,
                "subtotal" to subtotal
            )
        )
    }

    suspend fun hapusPenjualan(id: String) {
        supabase.postgrest["penjualan_detail"].delete { filter { eq("penjualan_id", id) } }
        supabase.postgrest["penjualan"].delete { filter { eq("id", id) } }
    }
}