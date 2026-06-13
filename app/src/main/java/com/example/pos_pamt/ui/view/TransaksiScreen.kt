package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.pos_pamt.data.*
import com.example.pos_pamt.ui.theme.*
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PenjualanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiScreen(viewModel: PenjualanViewModel, isAdmin: Boolean, kasirId: String, onBackClick: () -> Unit) {
    val state         = viewModel.penjualanState.collectAsStateWithLifecycle()
    val detail        = viewModel.detailState.collectAsStateWithLifecycle()
    val selected      = viewModel.selectedPenjualan.collectAsStateWithLifecycle()
    val showTambah    = viewModel.showTambah.collectAsStateWithLifecycle()
    val actionError   = viewModel.actionError.collectAsStateWithLifecycle()
    val actionSuccess = viewModel.actionSuccess.collectAsStateWithLifecycle()
    val pelangganList = viewModel.pelangganList.collectAsStateWithLifecycle()
    val kasList       = viewModel.kasList.collectAsStateWithLifecycle()
    val produkList    = viewModel.produkList.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionError.value) {
        actionError.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(actionSuccess.value) {
        actionSuccess.value?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    if (selected.value != null) {
        DetailTransaksiScreen(
            penjualan = selected.value!!,
            detailState = detail.value,
            isAdmin = isAdmin,
            onBack = { viewModel.clearSelected() },
            onHapus = { viewModel.hapusPenjualan(selected.value!!.id) },
            actionError = actionError.value,
            actionSuccess = actionSuccess.value,
            clearMessages = { viewModel.clearMessages() }
        )
        return
    }

    val grad = if (isAdmin) listOf(Admin, AdminPurple2) else listOf(Teal, Teal2)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp, bottom = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isAdmin) "Admin" else "Kasir", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Penjualan", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    if (!isAdmin) {
                        IconButton(onClick = { viewModel.openTambah() }, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.2f))) {
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
                        bg = if (isAdmin) AdminLight else Teal3,
                        text = if (isAdmin) "Admin melihat semua transaksi dari seluruh kasir. Bisa hapus."
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
                                    StatCard("Total Transaksi", "${list.size}", "tercatat", Teal3, Teal, if (isAdmin) "semua" else "milik kamu", Modifier.weight(1f))
                                    StatCard("Total Penjualan", rupiahD(list.sumOf { it.total }), if (isAdmin) "semua kasir" else "milik kamu", GreenLight, Green, "penjualan", Modifier.weight(1f))
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

    // Bottom Sheet Tambah Transaksi
    if (showTambah.value) {
        TransaksiFormSheet(
            pelangganList = pelangganList.value,
            kasList = kasList.value,
            produkList = produkList.value,
            onDismiss = { viewModel.closeTambah() },
            onSave = { pelId, kasId, prodId, harga, qty, bayar ->
                viewModel.simpanTransaksi(kasirId, pelId, kasId, prodId, harga, qty, bayar)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransaksiFormSheet(
    pelangganList: List<Pelanggan>,
    kasList: List<Kas>,
    produkList: List<Produk>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double, Double) -> Unit
) {
    var selectedPelanggan by remember { mutableStateOf<Pelanggan?>(null) }
    var selectedKas       by remember { mutableStateOf<Kas?>(null) }
    var selectedProduk    by remember { mutableStateOf<Produk?>(null) }
    var qty               by remember { mutableStateOf("") }
    var jumlahBayar       by remember { mutableStateOf("") }

    var expandPel  by remember { mutableStateOf(false) }
    var expandKas  by remember { mutableStateOf(false) }
    var expandProd by remember { mutableStateOf(false) }

    val qtyNum    = qty.toDoubleOrNull() ?: 0.0
    val harga     = selectedProduk?.harga ?: 0.0
    val subtotal  = harga * qtyNum
    val bayarNum  = jumlahBayar.toDoubleOrNull() ?: 0.0
    val kembalian = bayarNum - subtotal

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFF2F6F8)) {
        Text("Transaksi Penjualan Baru", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TDark, modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 14.dp))
        HorizontalDivider(color = Teal.copy(alpha = 0.1f))

        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            item {
                // Info box
                Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), shape = RoundedCornerShape(8.dp), color = Teal3) {
                    Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, null, tint = Teal, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                        Text("Data tersimpan ke tabel penjualan + penjualan_detail. kasir_id otomatis terisi dari user yang login.", fontSize = 11.sp, color = TextMid, lineHeight = 16.sp)
                    }
                }

                // Pelanggan
                Text("PELANGGAN *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
                ExposedDropdownMenuBox(expanded = expandPel, onExpandedChange = { expandPel = !expandPel }, modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    OutlinedTextField(
                        value = selectedPelanggan?.nama ?: "Pilih pelanggan…",
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandPel) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                    )
                    ExposedDropdownMenu(expanded = expandPel, onDismissRequest = { expandPel = false }, containerColor = Color.White) {
                        pelangganList.forEach { p ->
                            DropdownMenuItem(text = { Text(p.nama, fontSize = 13.sp) }, onClick = { selectedPelanggan = p; expandPel = false })
                        }
                    }
                }

                // Kas
                Text("KAS *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
                ExposedDropdownMenuBox(expanded = expandKas, onExpandedChange = { expandKas = !expandKas }, modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    OutlinedTextField(
                        value = selectedKas?.nama ?: "Pilih kas…",
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandKas) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                    )
                    ExposedDropdownMenu(expanded = expandKas, onDismissRequest = { expandKas = false }, containerColor = Color.White) {
                        kasList.forEach { k ->
                            DropdownMenuItem(text = { Text(k.nama, fontSize = 13.sp) }, onClick = { selectedKas = k; expandKas = false })
                        }
                    }
                }

                // Produk
                Text("PRODUK *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
                ExposedDropdownMenuBox(expanded = expandProd, onExpandedChange = { expandProd = !expandProd }, modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    OutlinedTextField(
                        value = selectedProduk?.let { "${it.nama} — Rp ${"%,.0f".format(it.harga).replace(',', '.')}" } ?: "Pilih produk…",
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandProd) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                    )
                    ExposedDropdownMenu(expanded = expandProd, onDismissRequest = { expandProd = false }, containerColor = Color.White) {
                        produkList.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("${p.nama} — Rp ${"%,.0f".format(p.harga).replace(',', '.')}", fontSize = 13.sp) },
                                onClick = { selectedProduk = p; expandProd = false }
                            )
                        }
                    }
                }

                // Qty
                Text("QTY *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = qty, onValueChange = { qty = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("1", color = T3, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(8.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                )

                // Kalkulasi box
                Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), shape = RoundedCornerShape(8.dp), color = Teal3) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("harga_satuan", fontSize = 13.sp, color = TextMid)
                            Text(rupiahD(harga), fontSize = 13.sp, color = TextMid)
                        }
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("subtotal (harga × qty)", fontSize = 13.sp, color = TextMid)
                            Text(rupiahD(subtotal), fontSize = 13.sp, color = TextMid)
                        }
                        HorizontalDivider(color = Teal.copy(alpha = 0.2f))

                        // Jumlah bayar
                        Text("JUMLAH_BAYAR *", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = T3, letterSpacing = 0.6.sp)
                        OutlinedTextField(
                            value = jumlahBayar, onValueChange = { jumlahBayar = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("0", color = T3, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp), singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal, unfocusedBorderColor = Teal.copy(alpha = 0.2f))
                        )
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("kembalian", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TDark)
                            Text(rupiahD(kembalian), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (kembalian >= 0) Green else Danger)
                        }
                    }
                }

                // Tombol simpan
                val valid = selectedPelanggan != null && selectedKas != null && selectedProduk != null && qtyNum > 0 && bayarNum >= subtotal
                Button(
                    onClick = {
                        if (valid) onSave(selectedPelanggan!!.id, selectedKas!!.id, selectedProduk!!.id, harga, qtyNum, bayarNum)
                    },
                    enabled = valid,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Simpan Penjualan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTransaksiScreen(
    penjualan: Penjualan,
    detailState: DataUiState<List<PenjualanDetail>>,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onHapus: () -> Unit,
    actionError: String?,
    actionSuccess: String?,
    clearMessages: () -> Unit
) {
    val grad = if (isAdmin) listOf(Admin, AdminPurple2) else listOf(Teal, Teal2)
    var showHapusDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionError) { actionError?.let { snackbarHostState.showSnackbar(it); clearMessages() } }
    LaunchedEffect(actionSuccess) { actionSuccess?.let { snackbarHostState.showSnackbar(it); clearMessages() } }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
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
                        InfoBox(icon = Icons.Default.Lock, iconTint = Admin, bg = AdminLight, text = "Admin dapat menghapus transaksi ini.")
                        OutlinedButton(
                            onClick = { showHapusDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Hapus Transaksi", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    if (showHapusDialog) {
        AlertDialog(
            onDismissRequest = { showHapusDialog = false },
            title = { Text("Hapus Transaksi?", fontWeight = FontWeight.Bold, color = TDark) },
            text = { Text("Transaksi dan semua item detail-nya akan dihapus permanen.", fontSize = 13.sp, color = TextMid) },
            confirmButton = {
                Button(onClick = { onHapus(); showHapusDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Danger), shape = RoundedCornerShape(8.dp)) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showHapusDialog = false }, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun TrxListRow(trx: Penjualan, isAdmin: Boolean, onClick: () -> Unit) {
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

@Composable
private fun DetailItemRow(d: PenjualanDetail) {
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

@Composable
private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, tint = TextLight, modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, color = TextLight, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}