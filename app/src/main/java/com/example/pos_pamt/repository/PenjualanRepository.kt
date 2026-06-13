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
            PenjualanInsert(
                pelangganId = pelangganId,
                kasId = kasId,
                kasirId = kasirId,
                total = subtotal,
                jumlahBayar = jumlahBayar,
                kembalian = kembalian,
                waktuPenjualan = "now()"
            )
        ) { select() }.decodeSingle<Penjualan>()

        supabase.postgrest["penjualan_detail"].insert(
            PenjualanDetailInsert(
                penjualanId = penjualan.id,
                produkId = produkId,
                hargaSatuan = hargaSatuan,
                qty = qty,
                subtotal = subtotal
            )
        )

        val produk = supabase.postgrest["produk"]
            .select { filter { eq("id", produkId) } }
            .decodeSingle<Produk>()
        supabase.postgrest["produk"].update(
            ProdukUpdate(stok = produk.stok - qty)
        ) { filter { eq("id", produkId) } }

        val kas = supabase.postgrest["kas"]
            .select { filter { eq("id", kasId) } }
            .decodeSingle<Kas>()
        val saldoBaru = kas.saldo + subtotal
        supabase.postgrest["kas"].update(
            KasUpdate(saldo = saldoBaru)
        ) { filter { eq("id", kasId) } }

        supabase.postgrest["log_kas"].insert(
            LogKasInsert(
                kasId = kasId,
                tipe = "masuk",
                saldoAwal = kas.saldo,
                saldoAkhir = saldoBaru,
                perubahan = subtotal,
                keterangan = "Penjualan #${penjualan.id.take(8)}"
            )
        )
    }

    suspend fun hapusPenjualan(id: String) {
        supabase.postgrest["penjualan_detail"].delete { filter { eq("penjualan_id", id) } }
        supabase.postgrest["penjualan"].delete { filter { eq("id", id) } }
    }
}