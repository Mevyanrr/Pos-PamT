package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pos.pamt.viewmodel.BarangViewModel
import com.pos.pamt.viewmodel.DataUiState

private val TealPrimary = Color(0xFF00B5A3)
private val TealLight   = Color(0xFFE0FAF7)
private val TextDark    = Color(0xFF0D2B2A)
private val TextGray    = Color(0xFF8AB5B1)
private val BgPage      = Color(0xFFF2F6F8)
private val DangerRed   = Color(0xFFEF4444)

@Composable
fun ListBarangScreen(
    viewModel   : BarangViewModel,
    onBackClick : () -> Unit
) {
    // Collect state dari ViewModel
    val barangState  = viewModel.barangState.collectAsStateWithLifecycle()
    val searchQuery  = viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPage)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(TealPrimary, Color(0xFF00CDB9))
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick  = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Text(text = "←", fontSize = 18.sp, color = Color.White)
                }

                Column {
                    Text(
                        text     = "Inventori",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.7.sp
                    )
                    Text(
                        text       = "Produk",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp)
        ) {

            OutlinedTextField(
                value         = searchQuery.value,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder   = { Text("Cari produk…", color = TextGray) },
                leadingIcon   = { Text("🔍", fontSize = 16.sp) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = TealPrimary,
                    unfocusedBorderColor = TealPrimary.copy(alpha = 0.3f),
                    containerColor       = Color.White
                )
            )

            when (val state = barangState.value) {

                is DataUiState.Idle -> {
                }

                is DataUiState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = TealPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text     = "Memuat data barang…",
                                fontSize = 13.sp,
                                color    = TextGray
                            )
                        }
                    }
                }

                is DataUiState.Error -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(24.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text       = state.message,
                                fontSize   = 13.sp,
                                color      = DangerRed,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadBarang() },
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = TealPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                is DataUiState.Success -> {
                    val filtered = state.data.filter { barang ->
                        barang.nama.contains(searchQuery.value, ignoreCase = true) ||
                                barang.kategori.contains(searchQuery.value, ignoreCase = true)
                    }

                    val stokMenipis = filtered.count { it.stok < 10 }

                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMiniCard(
                            label = "Total SKU",
                            value = "${filtered.size}",
                            badge = "Aktif: ${filtered.count { it.isActive }}",
                            badgeColor = Color(0xFFDCFCE7),
                            badgeTextColor = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                        StatMiniCard(
                            label = "Stok Menipis",
                            value = "$stokMenipis",
                            badge = "Perlu restock",
                            badgeColor = Color(0xFFFEE2E2),
                            badgeTextColor = DangerRed,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = TealLight)
                    ) {
                        Row(
                            modifier          = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "ℹ️", fontSize = 14.sp)
                            Text(
                                text     = "Kasir hanya bisa melihat produk (read-only). Tambah/Edit/Hapus hanya untuk Admin.",
                                fontSize = 11.sp,
                                color    = Color(0xFF3D6360),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "📦", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text     = "Produk tidak ditemukan",
                                    fontSize = 13.sp,
                                    color    = TextGray
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            LazyColumn {
                                items(filtered) { barang ->
                                    BarangRow(barang = barang)
                                    if (filtered.last() != barang) {
                                        HorizontalDivider(
                                            color     = TealPrimary.copy(alpha = 0.07f),
                                            thickness = 0.5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarangRow(barang: Barang) {
    Row(
        modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TealLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = emojiKategori(barang.kategori),
                fontSize = 20.sp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = barang.nama,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark
            )
            if (barang.stok < 10) {
                Row(
                    verticalAlignment  = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(text = "⚠️", fontSize = 10.sp)
                    Text(
                        text       = "Stok: ${barang.stok} pcs · menipis!",
                        fontSize   = 11.sp,
                        color      = DangerRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text     = "Stok: ${barang.stok} pcs · is_active: ${barang.isActive}",
                    fontSize = 11.sp,
                    color    = TextGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Text(
            text       = "Rp ${"%,d".format(barang.harga).replace(',', '.')}",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = TealPrimary
        )
    }
}

private fun emojiKategori(kategori: String): String {
    return when (kategori.lowercase()) {
        "obat"       -> "💊"
        "makanan"    -> "🫙"
        "kebersihan" -> "🧴"
        "alat tulis" -> "✏️"
        else         -> "📦"
    }
}

@Composable
private fun StatMiniCard(
    label          : String,
    value          : String,
    badge          : String,
    badgeColor     : Color,
    badgeTextColor : Color,
    modifier       : Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text     = label,
                fontSize = 10.sp,
                color    = TextGray,
                letterSpacing = 0.6.sp
            )
            Text(
                text       = value,
                fontSize   = 21.sp,
                fontWeight = FontWeight.Bold,
                color      = TextDark,
                modifier   = Modifier.padding(top = 4.dp)
            )
            Surface(
                modifier = Modifier.padding(top = 4.dp),
                shape    = RoundedCornerShape(10.dp),
                color    = badgeColor
            ) {
                Text(
                    text       = badge,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = badgeTextColor,
                    modifier   = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}