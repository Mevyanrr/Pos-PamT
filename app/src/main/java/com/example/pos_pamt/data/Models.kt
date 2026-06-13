package com.example.pos_pamt.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Barang(
    val id : String  = "",
    val nama : String  = "",
    val harga : Double  = 0.0,
    val stok : Double  = 0.0,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class Kas(
    val id : String  = "",
    @SerialName("nama_kas")
    val nama : String  = "",
    val saldo : Double  = 0.0,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class Pelanggan(
    val id: String = "",
    val nama: String = "",
    @SerialName("no_telp")
    val noTelp: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
data class PelangganInsert(
    val nama: String,
    @SerialName("no_telp") val noTelp: String,
    @SerialName("is_active") val isActive: Boolean
)

@Serializable
data class ProfileRow(
    val id       : String = "",
    val username : String = "",
    val role     : String = "kasir"
)

@Serializable
data class Produk(
    val id                                          : String  = "",
    val nama                                        : String  = "",
    val harga                                       : Double  = 0.0,
    val stok                                        : Double  = 0.0,
    @SerialName("is_active") val isActive           : Boolean = true,
    @SerialName("created_at") val createdAt         : String  = ""
)

@Serializable
data class ProdukPayload(
    val nama   : String  = "",
    val harga  : Double  = 0.0,
    val stok   : Double  = 0.0,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class Penjualan(
    val id                                              : String = "",
    @SerialName("pelanggan_id")    val pelangganId      : String = "",
    @SerialName("kas_id")          val kasId            : String = "",
    @SerialName("kasir_id")        val kasirId          : String = "",
    @SerialName("waktu_penjualan") val waktuPenjualan   : String = "",
    val total                                           : Double = 0.0,
    @SerialName("jumlah_bayar")    val jumlahBayar      : Double = 0.0,
    val kembalian                                       : Double = 0.0,
    @SerialName("created_at")      val createdAt        : String = ""
)

@Serializable
data class PenjualanDetail(
    val id                                              : String = "",
    @SerialName("penjualan_id")  val penjualanId        : String = "",
    @SerialName("produk_id")     val produkId           : String = "",
    @SerialName("harga_satuan")  val hargaSatuan        : Double = 0.0,
    val qty                                             : Double = 0.0,
    val subtotal                                        : Double = 0.0,
    @SerialName("created_at")    val createdAt          : String = "",
    @SerialName("updated_at")    val updatedAt          : String = ""
)

@Serializable
data class Pengeluaran(
    val id                                          : String = "",
    @SerialName("kas_id")      val kasId            : String = "",
    val tanggal                                     : String = "",
    val deskripsi                                   : String = "",
    val total                                       : Double = 0.0,
    val status                                      : String = "",
    @SerialName("created_at") val createdAt         : String = "",
    @SerialName("updated_at") val updatedAt         : String = ""
)

@Serializable
data class LogKas(
    val id                                              : String = "",
    @SerialName("kas_id")       val kasId              : String = "",
    val tipe                                            : String = "",
    @SerialName("saldo_awal")   val saldoAwal          : Double = 0.0,
    @SerialName("saldo_akhir")  val saldoAkhir         : Double = 0.0,
    val perubahan                                       : Double = 0.0,
    val keterangan                                      : String = "",
    @SerialName("created_at")   val createdAt          : String = ""
)

@Serializable
data class LogPelanggan(
    val id                                                  : String = "",
    @SerialName("pelanggan_id") val pelangganId             : String = "",
    val aktivitas                                           : String = "",
    @SerialName("created_at")   val createdAt               : String = ""
)

@Serializable
data class LogProduk(
    val id                                          : String = "",
    @SerialName("produk_id")  val produkId          : String = "",
    val aktivitas                                   : String = "",
    @SerialName("created_at") val createdAt         : String = ""
)

@Serializable
data class LogPengeluaran(
    val id                                                  : String = "",
    @SerialName("pengeluaran_id") val pengeluaranId         : String = "",
    val aktivitas                                           : String = "",
    @SerialName("total_awal")     val totalAwal             : Double = 0.0,
    @SerialName("total_akhir")    val totalAkhir            : Double = 0.0,
    val perubahan                                           : Double = 0.0,
    val keterangan                                          : String = "",
    @SerialName("created_at")     val createdAt             : String = ""
)

@Serializable
data class ProdukUpdate(
    val stok: Double
)

@Serializable
data class KasUpdate(
    val saldo: Double
)

@Serializable
data class LogKasInsert(
    @SerialName("kas_id") val kasId: String,
    val tipe: String,
    @SerialName("saldo_awal") val saldoAwal: Double,
    @SerialName("saldo_akhir") val saldoAkhir: Double,
    val perubahan: Double,
    val keterangan: String
)

@Serializable
data class PenjualanInsert(
    @SerialName("pelanggan_id") val pelangganId: String,
    @SerialName("kas_id") val kasId: String,
    @SerialName("kasir_id") val kasirId: String,
    val total: Double,
    @SerialName("jumlah_bayar") val jumlahBayar: Double,
    val kembalian: Double,
    @SerialName("waktu_penjualan") val waktuPenjualan: String
)

@Serializable
data class PenjualanDetailInsert(
    @SerialName("penjualan_id") val penjualanId: String,
    @SerialName("produk_id") val produkId: String,
    @SerialName("harga_satuan") val hargaSatuan: Double,
    val qty: Double,
    val subtotal: Double
)