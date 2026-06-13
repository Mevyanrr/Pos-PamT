package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pos_pamt.data.Barang
import com.example.pos_pamt.viewmodel.BarangViewModel
import com.example.pos_pamt.viewmodel.DataUiState


@Composable
fun ListBarangScreen(
    viewModel   : BarangViewModel,
    onBackClick : () -> Unit
) {
    val barangState = viewModel.barangState.collectAsStateWithLifecycle()
    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()

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
                        colors = listOf(TealPrimary, Teal2)
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick  = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Kembali",
                        tint               = Color.White,
                        modifier           = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text          = "Inventori",
                        fontSize      = 11.sp,
                        color         = Color.White.copy(alpha = 0.7f),
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
                leadingIcon   = {
                    Icon(
                        imageVector        = Icons.Outlined.Search,
                        contentDescription = null,
                        tint               = TextGray,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape      = RoundedCornerShape(12.dp),
                singleLine = true,
                colors     = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = TealPrimary,
                    unfocusedBorderColor    = TealPrimary.copy(alpha = 0.3f),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor  = Color.White
                )
            )

            when (val state = barangState.value) {
                is DataUiState.Idle -> {}
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
                            Icon(
                                imageVector        = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint               = DangerRed,
                                modifier           = Modifier.size(48.dp)
                            )
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
                        barang.nama.contains(searchQuery.value, ignoreCase = true)
                    }

                    val stokMenipis = filtered.count { it.stok < 10.0 }

                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMiniCard(
                            label          = "Total SKU",
                            value          = "${filtered.size}",
                            badge          = "Aktif: ${filtered.count { it.isActive }}",
                            badgeColor     = GreenLight,
                            badgeTextColor = SuccessAlt,
                            modifier       = Modifier.weight(1f)
                        )
                        StatMiniCard(
                            label          = "Stok Menipis",
                            value          = "$stokMenipis",
                            badge          = "Perlu restock",
                            badgeColor     = RedLight,
                            badgeTextColor = DangerRed,
                            modifier       = Modifier.weight(1f)
                        )
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier         = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector        = Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    tint               = TextGray,
                                    modifier           = Modifier.size(48.dp)
                                )
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
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TealLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint               = TealPrimary,
                modifier           = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = barang.nama,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark
            )
            if (barang.stok < 10.0) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier              = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint               = DangerRed,
                        modifier           = Modifier.size(12.dp)
                    )
                    Text(
                        text       = "Stok: ${barang.stok.toInt()} pcs · menipis!",
                        fontSize   = 11.sp,
                        color      = DangerRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text     = "Stok: ${barang.stok.toInt()} pcs · ${if (barang.isActive) "Aktif" else "Nonaktif"}",
                    fontSize = 11.sp,
                    color    = TextGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Text(
            text       = "Rp ${"%,.0f".format(barang.harga).replace(',', '.')}",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = TealPrimary
        )
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
                text          = label,
                fontSize      = 10.sp,
                color         = TextGray,
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