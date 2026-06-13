package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.LogPengeluaran
import com.example.pos_pamt.data.Pengeluaran
import com.example.pos_pamt.ui.theme.*
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PengeluaranViewModel

private val tabs = listOf("Semua", "Aktif", "Batal")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranScreen(viewModel: PengeluaranViewModel, onBackClick: () -> Unit) {
    val state       = viewModel.pengeluaranState.collectAsStateWithLifecycle()
    val logState    = viewModel.logPengeluaranState.collectAsStateWithLifecycle()
    val filter      = viewModel.filterStatus.collectAsStateWithLifecycle()
    val showTambah  = viewModel.showTambahForm.collectAsStateWithLifecycle()
    val showEdit    = viewModel.showEditForm.collectAsStateWithLifecycle()
    val batalTarget = viewModel.batalTarget.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Admin, AdminPurple2)))
                .padding(horizontal = 20.dp)
                .padding(top = 44.dp, bottom = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick   = onBackClick,
                    modifier  = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                    Text("Pengeluaran Kas", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                IconButton(
                    onClick  = { viewModel.openTambahForm() },
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(18.dp))

            when (val s = state.value) {
                is DataUiState.Idle    -> {}
                is DataUiState.Loading -> LoadingBox()
                is DataUiState.Error   -> ErrorBox(s.message) { viewModel.loadPengeluaran() }
                is DataUiState.Success -> {
                    val all = s.data

                    // STAT CARDS
                    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), Arrangement.spacedBy(10.dp)) {
                        StatCard("Total", rupiahD(all.filter { !it.status.equals("batal", true) }.sumOf { it.total }), "bulan ini", RedLight, Danger, "pengeluaran", Modifier.weight(1f))
                        StatCard("Transaksi", "${all.size}", "${all.count { it.status.equals("batal", true) }} batal", YellowLight, Warn, "tercatat", Modifier.weight(1f))
                    }

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

                    // LIST
                    val filtered = when (filter.value) {
                        "Aktif" -> all.filter { it.status.equals("aktif", true) || it.status.equals("lunas", true) }
                        "Batal" -> all.filter { it.status.equals("batal", true) }
                        else    -> all
                    }
                    if (filtered.isEmpty()) {
                        EmptyBox("Tidak ada data pengeluaran", Icons.Default.TrendingDown)
                    } else {
                        Card(
                            Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column {
                                filtered.forEachIndexed { idx, pen ->
                                    PengeluaranRow(
                                        p        = pen,
                                        onEdit   = { viewModel.openEditForm(pen) },
                                        onBatal  = { viewModel.konfirmasiBatal(pen) }
                                    )
                                    if (idx != filtered.lastIndex) HorizontalDivider(color = Admin.copy(alpha = 0.07f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }

            SectionLabel("Log Pengeluaran", top = 18)

            when (val l = logState.value) {
                is DataUiState.Idle    -> {}
                is DataUiState.Loading -> SmallLoading()
                is DataUiState.Error   -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                is DataUiState.Success -> {
                    if (l.data.isEmpty()) {
                        Text("Belum ada log pengeluaran.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Card(
                            Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
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

            Spacer(Modifier.height(20.dp))
        }
    }

    // DIALOG KONFIRMASI BATALKAN
    batalTarget.value?.let { pen ->
        AlertDialog(
            onDismissRequest = { viewModel.tutupKonfirmasiBatal() },
            icon             = { Icon(Icons.Default.Block, null, tint = Warn) },
            title            = { Text("Batalkan Pengeluaran", fontWeight = FontWeight.Bold) },
            text             = {
                Text(
                    "Yakin ingin membatalkan \"${pen.deskripsi}\"? " +
                            "Saldo kas akan dikembalikan dan status berubah menjadi Batal.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.batalkanPengeluaran() },
                    colors  = ButtonDefaults.buttonColors(containerColor = Warn)
                ) { Text("Ya, Batalkan", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.tutupKonfirmasiBatal() }) {
                    Text("Tidak")
                }
            }
        )
    }

    // BOTTOM SHEET: TAMBAH
    if (showTambah.value) {
        PengeluaranFormSheet(
            title       = "Tambah Pengeluaran",
            viewModel   = viewModel,
            onDismiss   = { viewModel.closeTambahForm() },
            onSubmit    = { viewModel.submitTambah() },
            submitLabel = "Simpan Pengeluaran"
        )
    }

    // BOTTOM SHEET: EDIT
    if (showEdit.value) {
        PengeluaranFormSheet(
            title       = "Edit Pengeluaran",
            viewModel   = viewModel,
            onDismiss   = { viewModel.closeEditForm() },
            onSubmit    = { viewModel.submitEdit() },
            submitLabel = "Simpan Perubahan"
        )
    }
}

// FORM BOTTOM SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PengeluaranFormSheet(
    title       : String,
    viewModel   : PengeluaranViewModel,
    onDismiss   : () -> Unit,
    onSubmit    : () -> Unit,
    submitLabel : String
) {
    val sheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val deskripsi   = viewModel.formDeskripsi.collectAsStateWithLifecycle()
    val total       = viewModel.formTotal.collectAsStateWithLifecycle()
    val tanggal     = viewModel.formTanggal.collectAsStateWithLifecycle()
    val status      = viewModel.formStatus.collectAsStateWithLifecycle()
    val kasId       = viewModel.formKasId.collectAsStateWithLifecycle()
    val kasList     = viewModel.kasList.collectAsStateWithLifecycle()
    val formError   = viewModel.formError.collectAsStateWithLifecycle()
    val formLoading = viewModel.formLoading.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        sheetState        = sheetState,
        containerColor    = BgPage,
        shape             = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
        ) {
            Box(Modifier.fillMaxWidth().height(4.dp).width(38.dp).clip(RoundedCornerShape(2.dp)).background(Teal.copy(alpha = 0.25f)).align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TDark, modifier = Modifier.padding(vertical = 14.dp))
            HorizontalDivider(color = Admin.copy(alpha = 0.1f))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Admin2).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.Top
            ) {
                Icon(Icons.Default.Lock, null, tint = Admin, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                Text("Perubahan otomatis dicatat.", fontSize = 11.sp, color = T2, lineHeight = 16.sp)
            }
            Spacer(Modifier.height(14.dp))

            FormDropdown(
                label    = "KAS",
                options  = kasList.value.map { it.id to it.nama },
                value    = kasId.value,
                onChange = { viewModel.onKasIdChange(it) }
            )

            FormField(
                label        = "TANGGAL",
                value        = tanggal.value,
                onChange     = { viewModel.onTanggalChange(it) },
                placeholder  = "YYYY-MM-DD",
                keyboardType = KeyboardType.Text
            )

            FormField(
                label       = "DESKRIPSI",
                value       = deskripsi.value,
                onChange    = { viewModel.onDeskripsiChange(it) },
                placeholder = "misal: Bayar listrik bulan Mei…"
            )

            FormField(
                label        = "TOTAL (CHECK > 0)",
                value        = total.value,
                onChange     = { viewModel.onTotalChange(it) },
                placeholder  = "0",
                keyboardType = KeyboardType.Number
            )

            FormDropdown(
                label    = "STATUS (DEFAULT 'aktif')",
                options  = listOf("aktif" to "aktif", "batal" to "batal"),
                value    = status.value,
                onChange = { viewModel.onStatusChange(it) }
            )

            if (formError.value != null) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier  = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(RedLight).padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(15.dp))
                    Text(formError.value ?: "", fontSize = 12.sp, color = Danger, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick  = onSubmit,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled  = !formLoading.value,
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Admin)
            ) {
                if (formLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.TrendingDown, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(submitLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = T2),
                border   = androidx.compose.foundation.BorderStroke(1.5.dp, Teal.copy(alpha = 0.25f))
            ) {
                Text("Batal", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// HELPER: FORM FIELD
@Composable
private fun FormField(
    label        : String,
    value        : String,
    onChange     : (String) -> Unit,
    placeholder  : String  = "",
    keyboardType : KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value           = value,
            onValueChange   = onChange,
            placeholder     = { Text(placeholder, fontSize = 13.sp, color = T3) },
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(8.dp),
            singleLine      = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors          = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = Teal.copy(alpha = 0.2f),
                focusedBorderColor      = Teal,
                unfocusedContainerColor = Color.White,
                focusedContainerColor   = Color.White
            )
        )
    }
}

// HELPER: DROPDOWN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdown(
    label    : String,
    options  : List<Pair<String, String>>,
    value    : String,
    onChange : (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == value }?.second ?: value

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value         = selectedLabel,
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape         = RoundedCornerShape(8.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor    = Teal.copy(alpha = 0.2f),
                    focusedBorderColor      = Teal,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (v, lbl) ->
                    DropdownMenuItem(
                        text    = { Text(lbl, fontSize = 13.sp) },
                        onClick = { onChange(v); expanded = false }
                    )
                }
            }
        }
    }
}

// ROW ITEM
@Composable
private fun PengeluaranRow(p: Pengeluaran, onEdit: () -> Unit, onBatal: () -> Unit) {
    val isAktif = p.status.equals("aktif", true) || p.status.equals("lunas", true)
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(if (isAktif) RedLight else GrayNeutral),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isAktif) Icons.Default.FlashOn else Icons.Default.Archive,
                null,
                tint     = if (isAktif) Danger else T3,
                modifier = Modifier.size(20.dp)
            )
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
                SmallIconBtn(Icons.Default.Edit,  AdminLight, Admin, onClick = onEdit)
                // Tombol batal hanya muncul kalau statusnya masih aktif
                if (isAktif) {
                    SmallIconBtn(Icons.Default.Block, YellowLight, Warn, onClick = onBatal)
                }
            }
        }
    }
}

// LOG ROW
@Composable
private fun PengeluaranLogRow(log: LogPengeluaran) {
    val dot = when {
        log.aktivitas.contains("created",   true) -> Green
        log.aktivitas.contains("cancelled", true) -> Danger
        log.aktivitas.contains("deleted",   true) -> Danger
        else                                       -> Warn
    }
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text(
                text       = "pengeluaran_id: ${log.pengeluaranId.take(8)}… — aktivitas: ${log.aktivitas}${if (log.keterangan.isNotEmpty()) " · ${log.keterangan}" else ""}",
                fontSize   = 12.sp,
                color      = T2,
                lineHeight = 18.sp
            )
            Text(
                text       = "total_awal: ${rupiahD(log.totalAwal)} → total_akhir: ${rupiahD(log.totalAkhir)} · perubahan: ${if (log.perubahan >= 0) "+" else ""}${rupiahD(log.perubahan)} · ${log.createdAt.take(10)}",
                fontSize   = 10.sp,
                color      = T3,
                modifier   = Modifier.padding(top = 2.dp)
            )
        }
    }
}