package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.data.LogKas
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.KasViewModel
import kotlin.math.abs

private val Admin = Color(0xFF6366F1)
private val Admin2= Color(0xFFEDE9FE)
private val TDark = Color(0xFF0D2B2A)
private val T3    = Color(0xFF8AB5B1)
private val Danger= Color(0xFFEF4444)
private val Green = Color(0xFF14A97A)

@Composable
fun KasScreen(viewModel: KasViewModel, onBackClick: () -> Unit) {
    val kasState    = viewModel.kasState.collectAsStateWithLifecycle()
    val logKasState = viewModel.logKasState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F6F8))) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Admin, Color(0xFF818CF8)))).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Manajemen Kas", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) { Icon(Icons.Default.Add, null, tint = Color.White) }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item { Spacer(Modifier.height(18.dp)) }
            item {
                when (val s = kasState.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> LoadingBox()
                    is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadKas() }
                    is DataUiState.Success -> {
                        val list = s.data
                        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                            StatCard("Jumlah Kas", "${list.size}", "${list.count { it.isActive }} aktif", Color(0xFFDCFCE7), Green, "terdaftar", Modifier.weight(1f))
                            StatCard("Total Saldo", rupiahD(list.sumOf { it.saldo }), "semua kas", Color(0xFFEDE9FE), Admin, "gabungan", Modifier.weight(1f))
                        }
                        InfoBox(Icons.Default.Lock, Admin, Admin2, "Halaman ini hanya dapat diakses oleh Admin (RLS: admin full akses kas). Kasir mendapat 403 jika mencoba akses.")
                        SectionLabel("Daftar Kas")
                        if (list.isEmpty()) EmptyBox("Belum ada data kas", Icons.Default.AccountBalance)
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column { list.forEachIndexed { idx, kas -> KasRow(kas); if (idx != list.lastIndex) HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp) } }
                        }
                    }
                }
            }
            item { SectionLabel("Log Kas", top = 18) }
            item {
                when (val l = logKasState.value) {
                    is DataUiState.Idle    -> {}
                    is DataUiState.Loading -> SmallLoading()
                    is DataUiState.Error   -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                    is DataUiState.Success -> {
                        if (l.data.isEmpty()) Text("Belum ada log kas.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column { l.data.forEachIndexed { idx, log -> KasLogRow(log); if (idx != l.data.lastIndex) HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp) } }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable private fun KasRow(kas: Kas) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(if (kas.isActive) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.AccountBalance, null, tint = if (kas.isActive) Green else Color(0xFFD97706), modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(kas.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TDark)
            Text("Saldo: ${rupiahD(kas.saldo)}", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
        Surface(shape = RoundedCornerShape(8.dp), color = if (kas.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)) {
            Text(if (kas.isActive) "Aktif" else "Nonaktif", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (kas.isActive) Green else Danger, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SmallIconBtn(Icons.Default.Edit, Color(0xFFEDE9FE), Admin)
            SmallIconBtn(Icons.Default.Delete, Color(0xFFFEE2E2), Danger)
        }
    }
}

@Composable private fun KasLogRow(log: LogKas) {
    val isMasuk = log.tipe.equals("masuk", true)
    val dot = if (isMasuk) Green else Danger
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("${log.keterangan.ifEmpty { "Kas" }} — ${if (isMasuk) "saldo masuk" else "saldo keluar"} (tipe: ${log.tipe})", fontSize = 12.sp, color = Color(0xFF3D6360), lineHeight = 18.sp)
            Text("saldo_awal: ${rupiahD(log.saldoAwal)} → saldo_akhir: ${rupiahD(log.saldoAkhir)} · perubahan: ${if (isMasuk) "+" else "−"}${rupiahD(abs(log.perubahan))} · ${log.createdAt.take(10)}", fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
