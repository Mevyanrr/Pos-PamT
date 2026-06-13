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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.data.LogKas
import com.example.pos_pamt.ui.theme.*
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.KasViewModel
import kotlin.math.abs

@Composable
fun KasScreen(viewModel: KasViewModel, onBackClick: () -> Unit) {
    val kasState      = viewModel.kasState.collectAsStateWithLifecycle()
    val logKasState   = viewModel.logKasState.collectAsStateWithLifecycle()
    val showTambah    = viewModel.showTambahDialog.collectAsStateWithLifecycle()
    val showEdit      = viewModel.showEditDialog.collectAsStateWithLifecycle()
    val showHapus     = viewModel.showHapusDialog.collectAsStateWithLifecycle()
    val actionSuccess = viewModel.actionSuccess.collectAsStateWithLifecycle()
    val actionError   = viewModel.actionError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionSuccess.value) {
        actionSuccess.value?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(actionError.value) {
        actionError.value?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Dialog Tambah
    if (showTambah.value) {
        DialogTambahKas(
            onDismiss = { viewModel.closeTambah() },
            onSimpan  = { nama, saldo -> viewModel.tambahKas(nama, saldo) }
        )
    }

    // Dialog Edit
    showEdit.value?.let { kas ->
        DialogEditKas(
            kas       = kas,
            onDismiss = { viewModel.closeEdit() },
            onSimpan  = { nama, saldo, aktif -> viewModel.editKas(kas.id, nama, saldo, aktif) }
        )
    }

    // Dialog Hapus
    showHapus.value?.let { kas ->
        AlertDialog(
            onDismissRequest = { viewModel.closeHapus() },
            title = { Text("Hapus Kas", fontWeight = FontWeight.Bold) },
            text  = { Text("Yakin ingin menghapus kas '${kas.nama}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.hapusKas(kas) },
                    colors  = ButtonDefaults.buttonColors(containerColor = Danger)
                ) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.closeHapus() }) { Text("Batal") }
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(BgPage).padding(padding)) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Admin, AdminPurple2)))
                    .padding(horizontal = 20.dp)
                    .padding(top = 44.dp, bottom = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Manajemen Kas", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.openTambah() },
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.Add, null, tint = Color.White) }
                }
            }

            // CONTENT
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                item { Spacer(Modifier.height(18.dp)) }

                item {
                    when (val s = kasState.value) {
                        is DataUiState.Idle    -> {}
                        is DataUiState.Loading -> LoadingBox()
                        is DataUiState.Error   -> ErrorBox(s.message) { viewModel.loadKas() }
                        is DataUiState.Success -> {
                            val list = s.data
                            Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                                StatCard("Jumlah Kas", "${list.size}", "${list.count { it.isActive }} aktif", GreenLight, Green, "terdaftar", Modifier.weight(1f))
                                StatCard("Total Saldo", rupiahD(list.sumOf { it.saldo }), "semua kas", AdminLight, Admin, "gabungan", Modifier.weight(1f))
                            }
                            InfoBox(Icons.Default.Lock, Admin, Admin2, "Halaman ini hanya dapat diakses oleh Admin (RLS: admin full akses kas). Kasir mendapat 403 jika mencoba akses.")
                            SectionLabel("Daftar Kas")
                            if (list.isEmpty()) {
                                EmptyBox("Belum ada data kas", Icons.Default.AccountBalance)
                            } else {
                                Card(
                                    Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column {
                                        list.forEachIndexed { idx, kas ->
                                            KasRow(
                                                kas     = kas,
                                                onEdit  = { viewModel.openEdit(kas) },
                                                onHapus = { viewModel.openHapus(kas) }
                                            )
                                            if (idx != list.lastIndex)
                                                HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
                                        }
                                    }
                                }
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
                            if (l.data.isEmpty()) {
                                Text("Belum ada log kas.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                            } else {
                                Card(
                                    Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column {
                                        l.data.forEachIndexed { idx, log ->
                                            KasLogRow(log)
                                            if (idx != l.data.lastIndex)
                                                HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
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
}

// ─── KasRow ───────────────────────────────────────────────────────────────────
@Composable
private fun KasRow(kas: Kas, onEdit: () -> Unit, onHapus: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                .background(if (kas.isActive) GreenLight else YellowLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountBalance, null, tint = if (kas.isActive) Green else WarnDark, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(kas.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TDark)
            Text("Saldo: ${rupiahD(kas.saldo)}", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
        Surface(shape = RoundedCornerShape(8.dp), color = if (kas.isActive) GreenLight else RedLight) {
            Text(
                if (kas.isActive) "Aktif" else "Nonaktif",
                fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                color = if (kas.isActive) Green else Danger,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SmallIconBtn(Icons.Default.Edit,   AdminLight, Admin,  onClick = onEdit)
            SmallIconBtn(Icons.Default.Delete, RedLight,   Danger, onClick = onHapus)
        }
    }
}

// ─── KasLogRow ────────────────────────────────────────────────────────────────
@Composable
private fun KasLogRow(log: LogKas) {
    val isMasuk = log.tipe.equals("masuk", true)
    val dot = if (isMasuk) Green else Danger
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text(
                "${log.keterangan.ifEmpty { "Kas" }} — ${if (isMasuk) "saldo masuk" else "saldo keluar"} (tipe: ${log.tipe})",
                fontSize = 12.sp, color = TextMid, lineHeight = 18.sp
            )
            Text(
                "saldo_awal: ${rupiahD(log.saldoAwal)} → saldo_akhir: ${rupiahD(log.saldoAkhir)} · perubahan: ${if (isMasuk) "+" else "−"}${rupiahD(abs(log.perubahan))} · ${log.createdAt.take(10)}",
                fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ─── SmallIconBtn ─────────────────────────────────────────────────────────────
@Composable
fun SmallIconBtn(icon: ImageVector, bg: Color, tint: Color, onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(bg)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
    }
}

// ─── Dialog Tambah ────────────────────────────────────────────────────────────
@Composable
fun DialogTambahKas(onDismiss: () -> Unit, onSimpan: (String, Double) -> Unit) {
    var nama  by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Kas", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nama, onValueChange = { nama = it },
                    label = { Text("Nama Kas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = saldo, onValueChange = { saldo = it },
                    label = { Text("Saldo Awal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val s = saldo.toDoubleOrNull() ?: 0.0
                if (nama.isNotBlank()) onSimpan(nama.trim(), s)
            }) { Text("Simpan") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// ─── Dialog Edit ──────────────────────────────────────────────────────────────
@Composable
fun DialogEditKas(kas: Kas, onDismiss: () -> Unit, onSimpan: (String, Double, Boolean) -> Unit) {
    var nama  by remember { mutableStateOf(kas.nama) }
    var saldo by remember { mutableStateOf(kas.saldo.toInt().toString()) }
    var aktif by remember { mutableStateOf(kas.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Kas", fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nama, onValueChange = { nama = it },
                    label = { Text("Nama Kas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = saldo, onValueChange = { saldo = it },
                    label = { Text("Saldo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = aktif, onCheckedChange = { aktif = it })
                    Text("Aktif", fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val s = saldo.toDoubleOrNull() ?: kas.saldo
                if (nama.isNotBlank()) onSimpan(nama.trim(), s, aktif)
            }) { Text("Simpan") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}