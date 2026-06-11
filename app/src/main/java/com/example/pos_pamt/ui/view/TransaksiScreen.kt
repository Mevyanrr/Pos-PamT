package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.Penjualan
import com.example.pos_pamt.data.PenjualanDetail
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PenjualanViewModel

private val Teal  = Color(0xFF00B5A3)
private val Teal2 = Color(0xFF00CDB9)
private val Teal3 = Color(0xFFE0FAF7)
private val Admin = Color(0xFF6366F1)
private val TDark = Color(0xFF0D2B2A)
private val T2    = Color(0xFF3D6360)
private val T3    = Color(0xFF8AB5B1)
private val Danger= Color(0xFFEF4444)
private val Green = Color(0xFF14A97A)

@Composable
fun TransaksiScreen(viewModel: PenjualanViewModel, isAdmin: Boolean, onBackClick: () -> Unit) {
    val state    = viewModel.penjualanState.collectAsStateWithLifecycle()
    val detail   = viewModel.detailState.collectAsStateWithLifecycle()
    val selected = viewModel.selectedPenjualan.collectAsStateWithLifecycle()

    if (selected.value != null) {
        DetailTransaksiScreen(selected.value!!, detail.value, isAdmin) { viewModel.clearSelected() }
        return
    }

    val grad = if (isAdmin) listOf(Admin, Color(0xFF818CF8)) else listOf(Teal, Teal2)

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F6F8))) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isAdmin) "Admin" else "Kasir", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Penjualan", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                // Kasir bisa tambah transaksi
                if (!isAdmin) {
                    IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.AddShoppingCart, null, tint = Color.White)
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item { Spacer(Modifier.height(18.dp)) }
            item {
                InfoBox(
                    icon = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    iconTint = if (isAdmin) Admin else Teal,
                    bg = if (isAdmin) Color(0xFFEDE9FE) else Teal3,
                    text = if (isAdmin) "Admin melihat semua transaksi dari seluruh kasir. Bisa edit & hapus."
                    else "Menampilkan riwayat penjualan milik kamu saja (kasir_id = auth.uid()). Tidak bisa edit/hapus."
                )
            }
            item {
                when (val s = state.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> LoadingBox()
                    is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadPenjualan() }
                    is DataUiState.Success -> {
                        val list = s.data
                        if (list.isEmpty()) {
                            EmptyBox(if (isAdmin) "Belum ada transaksi." else "Kamu belum membuat transaksi.", Icons.Default.ReceiptLong)
                        } else {
                            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                                StatCard("Total Transaksi", "${list.size}", "tercatat", Teal3, Teal, "semua".takeIf { isAdmin } ?: "milik kamu", Modifier.weight(1f))
                                StatCard("Total Penjualan", rupiahD(list.sumOf { it.total }), if (isAdmin) "semua kasir" else "milik kamu", Color(0xFFDCFCE7), Green, "penjualan", Modifier.weight(1f))
                            }
                            SectionLabel(if (isAdmin) "Semua Transaksi" else "Riwayat Transaksi")
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                Column {
                                    list.forEachIndexed { idx, trx ->
                                        TrxListRow(trx, isAdmin) { viewModel.selectPenjualan(trx) }
                                        if (idx != list.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun DetailTransaksiScreen(
    penjualan : Penjualan,
    detailState: DataUiState<List<PenjualanDetail>>,
    isAdmin   : Boolean,
    onBack    : () -> Unit
) {
    val grad = if (isAdmin) listOf(Admin, Color(0xFF818CF8)) else listOf(Teal, Color(0xFF00CDB9))

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F6F8))) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column {
                    Text("Penjualan", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                    Text("Detail Transaksi", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.height(18.dp))
                // INFO TRANSAKSI
                Card(Modifier.fillMaxWidth().padding(bottom = 14.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailInfoRow(Icons.Default.AccessTime, "Waktu", formatWaktu(penjualan.waktuPenjualan))
                        HorizontalDivider(color = Teal.copy(alpha = 0.08f), thickness = 0.5.dp)
                        DetailInfoRow(Icons.Default.ShoppingCart, "Total", rupiahD(penjualan.total))
                        HorizontalDivider(color = Teal.copy(alpha = 0.08f), thickness = 0.5.dp)
                        DetailInfoRow(Icons.Default.Payments, "Jumlah Bayar", rupiahD(penjualan.jumlahBayar))
                        HorizontalDivider(color = Teal.copy(alpha = 0.08f), thickness = 0.5.dp)
                        DetailInfoRow(Icons.Default.CurrencyExchange, "Kembalian", rupiahD(penjualan.kembalian))
                        if (isAdmin) {
                            HorizontalDivider(color = Teal.copy(alpha = 0.08f), thickness = 0.5.dp)
                            DetailInfoRow(Icons.Default.Person, "Kasir ID", penjualan.kasirId.take(8) + "…")
                        }
                    }
                }
                SectionLabel("Item Dibeli")
            }
            item {
                when (detailState) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> SmallLoading()
                    is DataUiState.Error -> Text("Gagal memuat detail: ${detailState.message}", fontSize = 12.sp, color = Danger)
                    is DataUiState.Success -> {
                        val items = detailState.data
                        if (items.isEmpty()) EmptyBox("Tidak ada item", Icons.Default.Inventory)
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                items.forEachIndexed { idx, d ->
                                    DetailItemRow(d)
                                    if (idx != items.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                }
                                HorizontalDivider(color = Teal.copy(alpha = 0.15f))
                                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Total", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TDark, modifier = Modifier.weight(1f))
                                    Text(rupiahD(items.sumOf { it.subtotal }), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Teal)
                                }
                            }
                        }
                    }
                }
            }
            if (isAdmin) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Admin)) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Edit", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger)) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Hapus", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable private fun TrxListRow(trx: Penjualan, isAdmin: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Teal3), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Receipt, null, tint = Teal, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(rupiahD(trx.total), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TDark)
            Text(formatWaktu(trx.waktuPenjualan), fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
            if (isAdmin) Text("kasir: ${trx.kasirId.take(8)}…", fontSize = 10.sp, color = T3.copy(alpha = 0.7f))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Bayar: ${rupiahD(trx.jumlahBayar)}", fontSize = 11.sp, color = Green, fontWeight = FontWeight.SemiBold)
            Text("Kembali: ${rupiahD(trx.kembalian)}", fontSize = 10.sp, color = T3)
        }
        Icon(Icons.Default.ChevronRight, null, tint = T3, modifier = Modifier.size(20.dp))
    }
}

@Composable private fun DetailItemRow(d: PenjualanDetail) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Teal3), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Inventory, null, tint = Teal, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("produk_id: ${d.produkId.take(8)}…", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Text("${rupiahD(d.hargaSatuan)} × ${d.qty.toInt()} pcs", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
        }
        Text(rupiahD(d.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal)
    }
}

@Composable private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = Color(0xFF8AB5B1), modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF8AB5B1), modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D2B2A))
    }
}
