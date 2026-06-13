package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.pos_pamt.data.LogPelanggan
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.ui.theme.*
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PelangganViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganScreen(viewModel: PelangganViewModel, isAdmin: Boolean, onBackClick: () -> Unit) {
    val state         = viewModel.pelangganState.collectAsStateWithLifecycle()
    val logState      = viewModel.logPelangganState.collectAsStateWithLifecycle()
    val query         = viewModel.searchQuery.collectAsStateWithLifecycle()
    val showTambah    = viewModel.showTambah.collectAsStateWithLifecycle()
    val editTarget    = viewModel.editTarget.collectAsStateWithLifecycle()
    val actionError   = viewModel.actionError.collectAsStateWithLifecycle()
    val actionSuccess = viewModel.actionSuccess.collectAsStateWithLifecycle()
    val grad          = if (isAdmin) listOf(Admin, AdminPurple2) else listOf(Teal, Teal2)

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionError.value) {
        actionError.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(actionSuccess.value) {
        actionSuccess.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(grad))
                    .padding(horizontal = 20.dp)
                    .padding(top = 44.dp, bottom = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isAdmin) "Admin" else "Kasir", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Pelanggan", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.openTambah() },
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))
                    ) { Icon(Icons.Default.PersonAdd, null, tint = Color.White) }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                item {
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = query.value, onValueChange = viewModel::onSearchQueryChange,
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
                                StatCard("Aktif", "${filtered.count { it.isActive }}", "${filtered.count { !it.isActive }} nonaktif", GreenLight, Green, "status aktif", Modifier.weight(1f))
                            }
                            if (filtered.isEmpty()) EmptyBox("Pelanggan tidak ditemukan", Icons.Default.People)
                            else Card(
                                Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column {
                                    filtered.forEachIndexed { idx, pel ->
                                        PelangganRow(
                                            pel = pel,
                                            onEdit = { viewModel.openEdit(pel) }
                                        )
                                        if (idx != filtered.lastIndex) HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
                item { SectionLabel("Log Pelanggan", top = 18) }
                item {
                    when (val l = logState.value) {
                        is DataUiState.Idle -> {}
                        is DataUiState.Loading -> SmallLoading()
                        is DataUiState.Error -> Text("Gagal memuat log: ${l.message}", fontSize = 11.sp, color = Danger)
                        is DataUiState.Success -> {
                            if (l.data.isEmpty()) Text("Belum ada log.", fontSize = 12.sp, color = T3, modifier = Modifier.padding(vertical = 8.dp))
                            else Card(
                                Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
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

    if (showTambah.value) {
        PelangganFormSheet(
            title = "Tambah Pelanggan Baru",
            initial = null,
            isEdit = false,
            onDismiss = { viewModel.closeTambah() },
            onSave = { nama, noTelp, isActive -> viewModel.tambahPelanggan(nama, noTelp, isActive) }
        )
    }

    editTarget.value?.let { pel ->
        key(pel.id) {
            PelangganFormSheet(
                title = "Edit Data Pelanggan",
                initial = pel,
                isEdit = true,
                onDismiss = { viewModel.closeEdit() },
                onSave = { nama, noTelp, isActive -> viewModel.editPelanggan(pel.id, nama, noTelp, isActive) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PelangganFormSheet(
    title: String,
    initial: Pelanggan?,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var nama     by remember { mutableStateOf(initial?.nama ?: "") }
    var noTelp   by remember { mutableStateOf(initial?.noTelp ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFF2F6F8)) {
        Text(
            title,
            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TDark,
            modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 14.dp)
        )
        HorizontalDivider(color = Teal.copy(alpha = 0.1f))

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text("NAMA *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = nama, onValueChange = { nama = it },
                placeholder = { Text("Nama lengkap pelanggan…", color = T3, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
            )

            Text("NO. TELEPON *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            OutlinedTextField(
                value = noTelp, onValueChange = { noTelp = it },
                placeholder = { Text("08xx-xxxx-xxxx", color = T3, fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
            )

            Text("IS_ACTIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                OutlinedTextField(
                    value = if (isActive) "true (Aktif)" else "false (Nonaktif)",
                    onValueChange = {},
                    readOnly = true,
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
                onClick = { if (nama.isNotBlank()) onSave(nama, noTelp, isActive) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Icon(if (isEdit) Icons.Default.Check else Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Simpan Perubahan" else "Simpan Pelanggan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Teal.copy(alpha = 0.25f))
            ) {
                Text("Batal", color = TextMid, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PelangganRow(pel: Pelanggan, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Teal3), contentAlignment = Alignment.Center) {
            Text(pel.nama.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(pel.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Text(if (pel.noTelp.isNotEmpty()) pel.noTelp else "Tidak ada no. telp", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
        }
        Surface(shape = RoundedCornerShape(8.dp), color = if (pel.isActive) GreenLight else RedLight) {
            Text(
                if (pel.isActive) "Aktif" else "Nonaktif",
                fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                color = if (pel.isActive) Green else Danger,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        SmallIconBtn(Icons.Default.Edit, Teal3, Teal, onClick = onEdit)
    }
}

@Composable
private fun PelangganLogRow(log: LogPelanggan) {
    val dot = when {
        log.aktivitas.contains("baru", true) || log.aktivitas.contains("tambah", true) -> Green
        log.aktivitas.contains("nonaktif", true) -> Danger
        else -> Warn
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot))
        Column {
            Text("aktivitas: ${log.aktivitas}", fontSize = 12.sp, color = TextMid, lineHeight = 18.sp)
            Text("pelanggan_id: ${log.pelangganId.take(8)}… · ${log.createdAt.take(10)}", fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
        }
    }
}