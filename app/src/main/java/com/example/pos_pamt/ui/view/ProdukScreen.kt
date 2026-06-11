package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.LogProduk
import com.example.pos_pamt.data.Produk
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.ProdukViewModel

private val Teal  = Color(0xFF00B5A3)
private val Teal3 = Color(0xFFE0FAF7)
private val Admin = Color(0xFF6366F1)
private val TDark = Color(0xFF0D2B2A)
private val T2    = Color(0xFF3D6360)
private val T3    = Color(0xFF8AB5B1)
private val Danger= Color(0xFFEF4444)
private val Warn  = Color(0xFFF59E0B)
private val Green = Color(0xFF14A97A)
private val pills = listOf("Semua", "Obat", "Makanan", "Kebersihan", "Lainnya")

@Composable
fun ProdukScreen(viewModel: ProdukViewModel, isAdmin: Boolean, onBackClick: () -> Unit) {
    val state    = viewModel.produkState.collectAsStateWithLifecycle()
    val logState = viewModel.logProdukState.collectAsStateWithLifecycle()
    val query    = viewModel.searchQuery.collectAsStateWithLifecycle()
    var pill     by remember { mutableStateOf("Semua") }
    val grad     = if (isAdmin) listOf(Admin, Color(0xFF818CF8)) else listOf(Teal, Color(0xFF00CDB9))

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F6F8))) {
        // HEADER
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Inventori", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Produk", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                // Tambah: admin only
                if (isAdmin) {
                    IconButton(onClick = {}, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.height(18.dp))
                // SEARCH
                OutlinedTextField(
                    value = query.value, onValueChange = viewModel::onSearchChange,
                    placeholder = { Text("Cari produk...", color = T3) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = T3) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
                // PILL FILTER
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pills.forEach { p ->
                        Surface(shape = RoundedCornerShape(20.dp), color = if (pill == p) Teal else Color.White, modifier = Modifier.clip(RoundedCornerShape(20.dp))) {
                            Text(p, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (pill == p) Color.White else T2, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                        }
                    }
                }
            }

            item {
                when (val s = state.value) {
                    is DataUiState.Idle -> {}
                    is DataUiState.Loading -> LoadingBox()
                    is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadProduk() }
                    is DataUiState.Success -> {
                        val filtered = s.data.filter {
                            (pill == "Semua" || it.nama.contains(pill, ignoreCase = true)) &&
                                    (it.nama.contains(query.value, ignoreCase = true))
                        }
                        // STAT
                        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                            StatCard("Total SKU", "${filtered.size}", "Aktif: ${filtered.count { it.isActive }}", Color(0xFFDCFCE7), Green, "terdaftar", Modifier.weight(1f))
                            StatCard("Stok Menipis", "${filtered.count { it.stok < 10 }}", "Perlu restock", Color(0xFFFEE2E2), Danger, "stok < 10", Modifier.weight(1f))
                        }
                        // INFO BOX
                        InfoBox(
                            icon = if (isAdmin) Icons.Default.Lock else Icons.Default.Info,
                            iconTint = if (isAdmin) Admin else Teal,
                            bg = if (isAdmin) Color(0xFFEDE9FE) else Teal3,
                            text = if (isAdmin) "Admin: tambah, edit, hapus produk. Setiap perubahan stok tercatat di log_produk."
                            else "Kasir hanya bisa melihat produk (read-only). Sesuai RLS: kasir select produk."
                        )
                        // LIST
                        if (filtered.isEmpty()) EmptyBox("Produk tidak ditemukan", Icons.Default.Inventory)
                        else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column {
                                filtered.forEachIndexed { idx, p ->
                                    ProdukRow(p, isAdmin)
                                    if (idx != filtered.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }

            // LOG PRODUK admin only, kasir tidak punya akses
            if (isAdmin) {
                item { SectionLabel("Log Produk", top = 18) }
                item {
                    when (val l = logState.value) {
                        is DataUiState.Idle    -> {}
                        is DataUiState.Loading -> SmallLoading()
                        is DataUiState.Error   -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                        is DataUiState.Success -> {
                            if (l.data.isEmpty()) Text("Belum ada log produk.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                            else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                Column { l.data.forEachIndexed { idx, log -> LogProdukRow(log); if (idx != l.data.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp) } }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable private fun ProdukRow(p: Produk, isAdmin: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(Teal3), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Inventory, null, tint = Teal, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(p.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (p.stok < 10) {
                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(12.dp))
                    Text("Stok: ${p.stok.toInt()} pcs · menipis!", fontSize = 11.sp, color = Danger, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Stok: ${p.stok.toInt()} pcs", fontSize = 11.sp, color = T3)
                }
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Rp ${"%,.0f".format(p.harga).replace(',', '.')}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal)
            if (isAdmin) Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallIconBtn(Icons.Default.Edit, Teal3, Teal)
                SmallIconBtn(Icons.Default.Delete, Color(0xFFFEE2E2), Danger)
            }
        }
    }
}

@Composable private fun LogProdukRow(log: LogProduk) {
    val dot = when { log.aktivitas.contains("tambah", true) -> Green; log.aktivitas.contains("hapus", true) || log.aktivitas.contains("terjual", true) -> Danger; else -> Warn }
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("produk_id: ${log.produkId.take(8)}… — aktivitas: ${log.aktivitas}", fontSize = 12.sp, color = Color(0xFF3D6360), lineHeight = 18.sp)
            Text(log.createdAt.take(16).replace("T", " "), fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
