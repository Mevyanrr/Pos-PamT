package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.LogPengeluaran
import com.example.pos_pamt.data.Pengeluaran
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PengeluaranViewModel

private val tabs  = listOf("Semua", "Aktif", "Batal")

@Composable
fun PengeluaranScreen(viewModel: PengeluaranViewModel, onBackClick: () -> Unit) {
    val state    = viewModel.pengeluaranState.collectAsStateWithLifecycle()
    val logState = viewModel.logPengeluaranState.collectAsStateWithLifecycle()
    val filter   = viewModel.filterStatus.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Admin, AdminPurple2))).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Pengeluaran Kas", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item { Spacer(Modifier.height(18.dp)) }
            item {
                when (val s = state.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> LoadingBox()
                    is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadPengeluaran() }
                    is DataUiState.Success -> {
                        val all = s.data
                        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                            StatCard("Total", rupiahD(all.sumOf { it.total }), "bulan ini", RedLight, Danger, "pengeluaran", Modifier.weight(1f))
                            StatCard("Transaksi", "${all.size}", "${all.count { it.status.equals("batal", true) }} batal", YellowLight, Warn, "tercatat", Modifier.weight(1f))
                        }
                        InfoBox(Icons.Default.Lock, Admin, Admin2, "Hanya Admin (RLS: admin full akses pengeluaran). Kasir mendapat 403 jika mencoba akses.")
                        // TABS
                        Row(modifier = Modifier.fillMaxWidth()) {
                            tabs.forEach { tab ->
                                val active = filter.value == tab
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    TextButton(onClick = { viewModel.onFilterChange(tab) }, modifier = Modifier.fillMaxWidth()) {
                                        Text(tab, fontSize = 12.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, color = if (active) Teal else T3)
                                    }
                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(if (active) Teal else Color.Transparent))
                                }
                            }
                        }
                        HorizontalDivider(color = Teal.copy(alpha = 0.1f), modifier = Modifier.padding(bottom = 14.dp))
                        val filtered = when (filter.value) {
                            "Aktif" -> all.filter { it.status.equals("aktif", true) || it.status.equals("lunas", true) }
                            "Batal" -> all.filter { it.status.equals("batal", true) }
                            else -> all
                        }
                        if (filtered.isEmpty()) EmptyBox("Tidak ada data pengeluaran", Icons.Default.TrendingDown)
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                filtered.forEachIndexed { idx, pen ->
                                    PengeluaranRow(pen)
                                    if (idx != filtered.lastIndex) HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
            item { SectionLabel("Log Pengeluaran", top = 18) }
            item {
                when (val l = logState.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> SmallLoading()
                    is DataUiState.Error -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                    is DataUiState.Success -> {
                        if (l.data.isEmpty()) Text("Belum ada log pengeluaran.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                l.data.forEachIndexed { idx, log ->
                                    PengeluaranLogRow(log)
                                    if (idx != l.data.lastIndex) HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
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

@Composable private fun PengeluaranRow(p: Pengeluaran) {
    val isAktif = p.status.equals("aktif", true) || p.status.equals("lunas", true)
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(if (isAktif) RedLight else GrayNeutral), contentAlignment = Alignment.Center) {
            Icon(if (isAktif) Icons.Default.FlashOn else Icons.Default.Archive, null, tint = if (isAktif) Danger else T3, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(p.deskripsi.ifEmpty { "Tanpa deskripsi" }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark, maxLines = 1)
            Row(modifier = Modifier.padding(top = 1.dp)) {
                Text("${p.tanggal.take(10)} · ", fontSize = 11.sp, color = T3)
                Text(p.status.replaceFirstChar { it.uppercase() }, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isAktif) Green else Danger)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("−${rupiahD(p.total)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isAktif) Danger else T3)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallIconBtn(Icons.Default.Edit, AdminLight, Admin)
                SmallIconBtn(Icons.Default.Delete, RedLight, Danger)
            }
        }
    }
}

@Composable private fun PengeluaranLogRow(log: LogPengeluaran) {
    val dot = when { log.aktivitas.contains("created", true) -> Green; log.aktivitas.contains("cancelled", true) -> Danger; else -> Warn }
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("pengeluaran_id: ${log.pengeluaranId.take(8)}… — aktivitas: ${log.aktivitas}${if (log.keterangan.isNotEmpty()) " · ${log.keterangan}" else ""}", fontSize = 12.sp, color = T2, lineHeight = 18.sp)
            Text("total_awal: ${rupiahD(log.totalAwal)} → total_akhir: ${rupiahD(log.totalAkhir)} · perubahan: ${if (log.perubahan >= 0) "+" else ""}${rupiahD(log.perubahan)} · ${log.createdAt.take(10)}", fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
