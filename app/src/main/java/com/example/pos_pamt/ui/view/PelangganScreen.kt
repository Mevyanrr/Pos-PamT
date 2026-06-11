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
import com.example.pos_pamt.data.LogPelanggan
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PelangganViewModel

private val Teal  = Color(0xFF00B5A3)
private val Teal3 = Color(0xFFE0FAF7)
private val Admin = Color(0xFF6366F1)
private val TDark = Color(0xFF0D2B2A)
private val T3    = Color(0xFF8AB5B1)
private val Danger= Color(0xFFEF4444)
private val Green = Color(0xFF16A34A)
private val Warn  = Color(0xFFF59E0B)

@Composable
fun PelangganScreen(viewModel: PelangganViewModel, isAdmin: Boolean, onBackClick: () -> Unit) {
    val state    = viewModel.pelangganState.collectAsStateWithLifecycle()
    val logState = viewModel.logPelangganState.collectAsStateWithLifecycle()
    val query    = viewModel.searchQuery.collectAsStateWithLifecycle()
    val grad     = if (isAdmin) listOf(Admin, Color(0xFF818CF8)) else listOf(Teal, Color(0xFF00CDB9))

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F6F8))) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isAdmin) "Admin" else "Kasir", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Pelanggan", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                // Kasir + Admin bisa tambah pelanggan
                IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.PersonAdd, null, tint = Color.White)
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(
                    value = query.value, onValueChange = viewModel::onSearchChange,
                    placeholder = { Text("Cari nama atau no. telp...", color = T3) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = T3) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
            }
            item {
                when (val s = state.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> LoadingBox()
                    is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadPelanggan() }
                    is DataUiState.Success -> {
                        val filtered = s.data.filter {
                            it.nama.contains(query.value, ignoreCase = true) ||
                                    it.noTelp.contains(query.value, ignoreCase = true)
                        }
                        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                            StatCard("Total", "${filtered.size}", "terdaftar", Teal3, Teal, "pelanggan", Modifier.weight(1f))
                            StatCard("Aktif", "${filtered.count { it.isActive }}", "${filtered.count { !it.isActive }} nonaktif", Color(0xFFDCFCE7), Green, "status aktif", Modifier.weight(1f))
                        }
                        InfoBox(
                            icon = if (isAdmin) Icons.Default.Lock else Icons.Default.Info,
                            iconTint = if (isAdmin) Admin else Teal,
                            bg = if (isAdmin) Color(0xFFEDE9FE) else Teal3,
                            text = if (isAdmin) "Admin: tambah, ubah, hapus pelanggan. Setiap aksi tercatat di log_pelanggan."
                            else "Kasir: tambah & update pelanggan (INSERT+UPDATE). Hapus hanya Admin. Log pelanggan bisa dilihat kasir."
                        )
                        if (filtered.isEmpty()) EmptyBox("Pelanggan tidak ditemukan", Icons.Default.People)
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                filtered.forEachIndexed { idx, pel ->
                                    PelangganRow(pel, isAdmin)
                                    if (idx != filtered.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
            // log_pelanggan: kasir + admin bisa SELECT
            item { SectionLabel("Log Pelanggan", top = 18) }
            item {
                when (val l = logState.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> SmallLoading()
                    is DataUiState.Error -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                    is DataUiState.Success -> {
                        if (l.data.isEmpty()) Text("Belum ada log pelanggan.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                l.data.forEachIndexed { idx, log ->
                                    PelangganLogRow(log)
                                    if (idx != l.data.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
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

@Composable private fun PelangganRow(pel: Pelanggan, isAdmin: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Teal3), contentAlignment = Alignment.Center) {
            Text(pel.nama.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(pel.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Text(if (pel.noTelp.isNotEmpty()) pel.noTelp else "Tidak ada no. telp", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
        }
        Surface(shape = RoundedCornerShape(8.dp), color = if (pel.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)) {
            Text(if (pel.isActive) "Aktif" else "Nonaktif", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (pel.isActive) Green else Danger, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
        }
        // Edit: kasir + admin | Delete: admin only
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SmallIconBtn(Icons.Default.Edit, Teal3, Teal)
            if (isAdmin) SmallIconBtn(Icons.Default.Delete, Color(0xFFFEE2E2), Danger)
        }
    }
}

@Composable private fun PelangganLogRow(log: LogPelanggan) {
    val dot = when {
        log.aktivitas.contains("baru", true) || log.aktivitas.contains("tambah", true) -> Green
        log.aktivitas.contains("hapus", true) -> Danger
        else -> Warn
    }
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("aktivitas: ${log.aktivitas}", fontSize = 12.sp, color = Color(0xFF3D6360), lineHeight = 18.sp)
            Text("pelanggan_id: ${log.pelangganId.take(8)}… · ${log.createdAt.take(10)}", fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
