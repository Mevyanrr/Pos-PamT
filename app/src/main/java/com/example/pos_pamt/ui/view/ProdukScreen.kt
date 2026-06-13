package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.LogProduk
import com.example.pos_pamt.data.Produk
import com.example.pos_pamt.ui.theme.*
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.ProdukViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukScreen(viewModel: ProdukViewModel, isAdmin: Boolean, onBackClick: () -> Unit) {
    val state         = viewModel.produkState.collectAsStateWithLifecycle()
    val logState      = viewModel.logProdukState.collectAsStateWithLifecycle()
    val query         = viewModel.searchQuery.collectAsStateWithLifecycle()
    val showTambah    = viewModel.showTambah.collectAsStateWithLifecycle()
    val editTarget    = viewModel.editTarget.collectAsStateWithLifecycle()
    val actionError   = viewModel.actionError.collectAsStateWithLifecycle()
    val actionSuccess = viewModel.actionSuccess.collectAsStateWithLifecycle()
    var deleteTarget  by remember { mutableStateOf<Produk?>(null) }
    val grad          = if (isAdmin) listOf(Admin, AdminPurple2) else listOf(Teal, Teal2)

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionError.value) {
        actionError.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(actionSuccess.value) {
        actionSuccess.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    LaunchedEffect(Unit) { if (isAdmin) viewModel.loadLogProduk() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
            // Header
            Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Inventori", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Produk", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    if (isAdmin) {
                        IconButton(onClick = { viewModel.openTambah() }, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                item {
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = query.value, onValueChange = viewModel::onSearchChange,
                        placeholder = { Text("Cari produk...", color = T3) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = T3) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                        shape = RoundedCornerShape(12.dp), singleLine = true
                    )
                }
                item {
                    when (val s = state.value) {
                        is DataUiState.Idle -> {}
                        is DataUiState.Loading -> LoadingBox()
                        is DataUiState.Error -> ErrorBox(s.message) { viewModel.loadProduk() }
                        is DataUiState.Success -> {
                            val filtered = s.data.filter {
                                it.nama.contains(query.value, ignoreCase = true)
                            }
                            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                                StatCard("Total SKU", "${filtered.size}", "Aktif: ${filtered.count { it.isActive }}", GreenLight, Green, "terdaftar", Modifier.weight(1f))
                                StatCard("Stok Menipis", "${filtered.count { it.stok < 10 }}", "Perlu restock", RedLight, Danger, "stok < 10", Modifier.weight(1f))
                            }
                            if (!isAdmin) {
                                InfoBox(
                                    icon = Icons.Default.Info,
                                    iconTint = Teal,
                                    bg = Teal3,
                                    text = "Kasir hanya bisa melihat produk."
                                )
                            }
                            if (filtered.isEmpty()) EmptyBox("Produk tidak ditemukan", Icons.Default.Inventory)
                            else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                Column {
                                    filtered.forEachIndexed { idx, p ->
                                        ProdukRow(
                                            p = p, isAdmin = isAdmin,
                                            onEdit = { viewModel.openEdit(p) },
                                            onDelete = { deleteTarget = p }
                                        )
                                        if (idx != filtered.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
                if (isAdmin) {
                    item { SectionLabel("Log Produk", top = 18) }
                    item {
                        when (val l = logState.value) {
                            is DataUiState.Idle -> {}
                            is DataUiState.Loading -> SmallLoading()
                            is DataUiState.Error -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                            is DataUiState.Success -> {
                                if (l.data.isEmpty()) Text("Belum ada log produk.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                                else Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                    Column {
                                        l.data.forEachIndexed { idx, log ->
                                            LogProdukRow(log)
                                            if (idx != l.data.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
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

    // Bottom Sheet Tambah
    if (showTambah.value) {
        ProdukFormSheet(
            title = "Tambah Produk Baru",
            initial = null,
            isEdit = false,
            onDismiss = { viewModel.closeTambah() },
            onSave = { nama, harga, stok, isActive -> viewModel.tambah(nama, harga, stok, isActive) }
        )
    }

    // Bottom Sheet Edit
    editTarget.value?.let { p ->
        ProdukFormSheet(
            title = "Edit Produk",
            initial = p,
            isEdit = true,
            onDismiss = { viewModel.closeEdit() },
            onSave = { nama, harga, stok, isActive -> viewModel.edit(p.id, nama, harga, stok, isActive) }
        )
    }

    // Dialog Hapus
    deleteTarget?.let { p ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Hapus Produk?", fontWeight = FontWeight.Bold, color = TDark) },
            text = { Text("\"${p.nama}\" akan dihapus permanen. Jika masih ada di transaksi, penghapusan akan gagal.", fontSize = 13.sp, color = TextMid) },
            confirmButton = {
                Button(onClick = { viewModel.hapus(p.id); deleteTarget = null }, colors = ButtonDefaults.buttonColors(containerColor = Danger), shape = RoundedCornerShape(8.dp)) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteTarget = null }, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
            },
            containerColor = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProdukFormSheet(
    title: String,
    initial: Produk?,
    isEdit: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Boolean) -> Unit
) {
    var nama     by remember { mutableStateOf(initial?.nama ?: "") }
    var harga    by remember { mutableStateOf(if (initial != null) initial.harga.toInt().toString() else "") }
    var stok     by remember { mutableStateOf(if (initial != null) initial.stok.toInt().toString() else "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFF2F6F8)) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TDark, modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 14.dp))
        HorizontalDivider(color = Teal.copy(alpha = 0.1f))

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text("NAMA PRODUK *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = nama, onValueChange = { nama = it },
                placeholder = { Text("misal: Obat Batuk…", color = T3, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
            )

            Text("HARGA (RP) *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = harga, onValueChange = { harga = it.filter { c -> c.isDigit() } },
                placeholder = { Text("CHECK: harga > 0", color = T3, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
            )

            Text("STOK *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = stok, onValueChange = { stok = it.filter { c -> c.isDigit() } },
                placeholder = { Text("CHECK: stok >= 0", color = T3, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
            )

            Text("IS_ACTIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                OutlinedTextField(
                    value = if (isActive) "Aktif" else "Nonaktif",
                    onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = Color.White) {
                    DropdownMenuItem(text = { Text("true (Aktif)", fontSize = 13.sp) }, onClick = { isActive = true; expanded = false })
                    DropdownMenuItem(text = { Text("false (Nonaktif)", fontSize = 13.sp) }, onClick = { isActive = false; expanded = false })
                }
            }

            Button(
                onClick = {
                    val h = harga.toDoubleOrNull() ?: 0.0
                    val s = stok.toDoubleOrNull() ?: 0.0
                    if (nama.isNotBlank() && h > 0 && s >= 0) onSave(nama, h, s, isActive)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Admin)
            ) {
                Icon(if (isEdit) Icons.Default.Check else Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Simpan Perubahan" else "Simpan Produk", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Teal.copy(alpha = 0.25f))
            ) { Text("Batal", color = TextMid, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProdukRow(p: Produk, isAdmin: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ikon produk
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Teal3),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Inventory, null, tint = Teal, modifier = Modifier.size(22.dp))
        }

        // Info produk
        Column(modifier = Modifier.weight(1f)) {
            Text(p.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (p.stok < 10) {
                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(12.dp))
                    Text("Stok: ${p.stok.toInt()} pcs · menipis!", fontSize = 11.sp, color = Danger, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Stok: ${p.stok.toInt()} pcs", fontSize = 11.sp, color = T3)
                }
            }
            Surface(
                modifier = Modifier.padding(top = 4.dp),
                shape = RoundedCornerShape(6.dp),
                color = if (p.isActive) GreenLight else RedLight
            ) {
                Text(
                    if (p.isActive) "Aktif" else "Nonaktif",
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                    color = if (p.isActive) Green else Danger,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        // Harga + tombol aksi
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "Rp ${"%,.0f".format(p.harga).replace(',', '.')}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Teal
            )
            if (isAdmin) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Edit — icon kecil, tanpa background kotak besar
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Teal,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    // Divider
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(1.dp)
                            .background(Teal.copy(alpha = 0.15f))
                    )
                    // Tombol Hapus
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Danger,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogProdukRow(log: LogProduk) {
    val dot = when {
        log.aktivitas.contains("tambah", true) -> Green
        log.aktivitas.contains("hapus", true) || log.aktivitas.contains("terjual", true) -> Danger
        else -> Warn
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("produk_id: ${log.produkId.take(8)}… — aktivitas: ${log.aktivitas}", fontSize = 12.sp, color = TextMid, lineHeight = 18.sp)
            Text(log.createdAt.take(16).replace("T", " "), fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}