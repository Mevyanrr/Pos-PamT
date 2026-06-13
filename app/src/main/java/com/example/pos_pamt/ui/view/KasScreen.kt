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

    if (showTambah.value) {
        DialogTambahKas(
            onDismiss = { viewModel.closeTambah() },
            onSimpan  = { nama, saldo -> viewModel.tambahKas(nama, saldo) }
        )
    }

    showEdit.value?.let { kas ->
        DialogEditKas(
            kas       = kas,
            onDismiss = { viewModel.closeEdit() },
            onSimpan  = { nama, saldo, aktif -> viewModel.editKas(kas.id, nama, saldo, aktif) }
        )
    }

    showHapus.value?.let { kas ->
        AlertDialog(
            onDismissRequest = { viewModel.closeHapus() },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Hapus Kas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TDark) },
            text  = { Text("Yakin ingin menghapus kas '${kas.nama}'?\nTindakan ini tidak dapat dibatalkan.", fontSize = 13.sp, color = T3, lineHeight = 20.sp) },
            confirmButton = {
                Button(onClick = { viewModel.hapusKas(kas) }, colors = ButtonDefaults.buttonColors(containerColor = Danger), shape = RoundedCornerShape(10.dp), modifier = Modifier.height(40.dp)) {
                    Text("Hapus", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.closeHapus() }, shape = RoundedCornerShape(10.dp), modifier = Modifier.height(40.dp)) {
                    Text("Batal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        )
    }

    // ← FIX 1: Hapus padding dari Scaffold, pakai Box saja
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(BgPage)) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Admin, AdminPurple2)))
                    .padding(horizontal = 20.dp)
                    .padding(top = 44.dp, bottom = 18.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick  = onBackClick,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Manajemen Kas", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    IconButton(
                        onClick  = { viewModel.openTambah() },
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.Add, null, tint = Color.White) }
                }
            }

            // CONTENT
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)
            ) {
                item {
                    when (val s = kasState.value) {
                        is DataUiState.Idle    -> {}
                        is DataUiState.Loading -> LoadingBox()
                        is DataUiState.Error   -> ErrorBox(s.message) { viewModel.loadKas() }
                        is DataUiState.Success -> {
                            val list = s.data

                            // ← FIX 3: StatCard inline supaya Rp dan nominal tidak kepisah
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                KasStatCard(
                                    label    = "Jumlah Kas",
                                    value    = "${list.size}",
                                    sub1     = "${list.count { it.isActive }} aktif",
                                    sub2     = "terdaftar",
                                    bgIcon   = GreenLight,
                                    color    = Green,
                                    modifier = Modifier.weight(1f)
                                )
                                KasStatCard(
                                    label    = "Total Saldo",
                                    value    = rupiahD(list.sumOf { it.saldo }),
                                    sub1     = "semua kas",
                                    sub2     = "gabungan",
                                    bgIcon   = AdminLight,
                                    color    = Admin,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(12.dp))
                            InfoBox(Icons.Default.Lock, Admin, Admin2, "Halaman ini hanya dapat diakses oleh Admin (RLS: admin full akses kas). Kasir mendapat 403 jika mencoba akses.")
                            Spacer(Modifier.height(18.dp))
                            SectionLabel("Daftar Kas")
                            Spacer(Modifier.height(10.dp))

                            if (list.isEmpty()) {
                                EmptyBox("Belum ada data kas", Icons.Default.AccountBalance)
                            } else {
                                Card(
                                    modifier  = Modifier.fillMaxWidth(),
                                    shape     = RoundedCornerShape(16.dp),
                                    colors    = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column {
                                        list.forEachIndexed { idx, kas ->
                                            KasRow(kas = kas, onEdit = { viewModel.openEdit(kas) }, onHapus = { viewModel.openHapus(kas) })
                                            if (idx != list.lastIndex)
                                                HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(18.dp)) }
                item { SectionLabel("Log Kas") }
                item { Spacer(Modifier.height(10.dp)) }

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
                                    modifier  = Modifier.fillMaxWidth(),
                                    shape     = RoundedCornerShape(16.dp),
                                    colors    = CardDefaults.cardColors(containerColor = Color.White),
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

                item { Spacer(Modifier.height(24.dp)) }
            }
        }

        // Snackbar di pojok bawah
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}

// ─── KasStatCard — FIX nominal tidak kepisah ─────────────────────────────────
@Composable
private fun KasStatCard(label: String, value: String, sub1: String, sub2: String, bgIcon: Color, color: Color, modifier: Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                label.uppercase(),
                fontSize      = 10.sp,
                color         = T3,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = TDark,
                maxLines   = 1,          // ← tidak wrap ke baris baru
                softWrap   = false       // ← paksa 1 baris
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(6.dp), color = bgIcon) {
                    Text(sub1, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text(sub2, fontSize = 10.sp, color = T3)
            }
        }
    }
}

// ─── KasRow — FIX button edit delete tidak nyambung ──────────────────────────
@Composable
private fun KasRow(kas: Kas, onEdit: () -> Unit, onHapus: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)   // ← naik dari 12 ke 10, jelas terpisah
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(if (kas.isActive) GreenLight else YellowLight),
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
                color    = if (kas.isActive) Green else Danger,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        // ← FIX 2: button edit & delete pakai spacedBy(6.dp) supaya tidak nyambung
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SmallIconBtn(Icons.Default.Edit,   AdminLight, Admin,  onClick = onEdit)
            SmallIconBtn(Icons.Default.Delete, RedLight,   Danger, onClick = onHapus)
        }
    }
}

// ─── KasLogRow ────────────────────────────────────────────────────────────────
@Composable
private fun KasLogRow(log: LogKas) {
    val isMasuk = log.tipe.equals("masuk", true)
    val dot     = if (isMasuk) Green else Danger
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top
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

// ─── Dialog Tambah ────────────────────────────────────────────────────────────
@Composable
fun DialogTambahKas(onDismiss: () -> Unit, onSimpan: (String, Double) -> Unit) {
    var nama      by remember { mutableStateOf("") }
    var saldo     by remember { mutableStateOf("") }
    var namaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Tambah Kas", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TDark) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value          = nama,
                    onValueChange  = { nama = it; namaError = false },
                    label          = { Text("Nama Kas") },
                    modifier       = Modifier.fillMaxWidth(),
                    singleLine     = true,
                    isError        = namaError,
                    supportingText = if (namaError) {{ Text("Nama tidak boleh kosong", color = Danger, fontSize = 11.sp) }} else null,
                    shape          = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value           = saldo,
                    onValueChange   = { saldo = it },
                    label           = { Text("Saldo Awal") },
                    modifier        = Modifier.fillMaxWidth(),
                    singleLine      = true,
                    shape           = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix          = { Text("Rp ", color = T3, fontSize = 13.sp) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isBlank()) { namaError = true; return@Button }
                    val s = saldo.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    onSimpan(nama.trim(), s)
                },
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(42.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Admin)
            ) { Text("Simpan", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp), modifier = Modifier.height(42.dp)) {
                Text("Batal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = T3)
            }
        }
    )
}

// ─── Dialog Edit ──────────────────────────────────────────────────────────────
@Composable
fun DialogEditKas(kas: Kas, onDismiss: () -> Unit, onSimpan: (String, Double, Boolean) -> Unit) {
    var nama      by remember { mutableStateOf(kas.nama) }
    var saldo     by remember { mutableStateOf(kas.saldo.toLong().toString()) }
    var aktif     by remember { mutableStateOf(kas.isActive) }
    var namaError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Edit Kas", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TDark) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value          = nama,
                    onValueChange  = { nama = it; namaError = false },
                    label          = { Text("Nama Kas") },
                    modifier       = Modifier.fillMaxWidth(),
                    singleLine     = true,
                    isError        = namaError,
                    supportingText = if (namaError) {{ Text("Nama tidak boleh kosong", color = Danger, fontSize = 11.sp) }} else null,
                    shape          = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value           = saldo,
                    onValueChange   = { saldo = it },
                    label           = { Text("Saldo") },
                    modifier        = Modifier.fillMaxWidth(),
                    singleLine      = true,
                    shape           = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix          = { Text("Rp ", color = T3, fontSize = 13.sp) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (aktif) GreenLight else RedLight).padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Status Kas", fontSize = 11.sp, color = if (aktif) Green else Danger, fontWeight = FontWeight.SemiBold)
                        Text(if (aktif) "Aktif" else "Nonaktif", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (aktif) Green else Danger)
                    }
                    Switch(
                        checked         = aktif,
                        onCheckedChange = { aktif = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            checkedTrackColor   = Green,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Danger.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nama.isBlank()) { namaError = true; return@Button }
                    val s = saldo.replace(".", "").replace(",", "").toDoubleOrNull() ?: kas.saldo
                    onSimpan(nama.trim(), s, aktif)
                },
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(42.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Admin)
            ) { Text("Simpan", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp), modifier = Modifier.height(42.dp)) {
                Text("Batal", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = T3)
            }
        }
    )
}