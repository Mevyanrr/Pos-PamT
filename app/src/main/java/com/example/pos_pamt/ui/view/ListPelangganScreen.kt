package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
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
import com.example.pos_pamt.data.Pelanggan
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.PelangganViewModel

private val BluePrimary  = Color(0xFF3B82F6)
private val BlueLight    = Color(0xFFEFF6FF)
private val TextDark     = Color(0xFF0D2B2A)
private val TextGray     = Color(0xFF8AB5B1)
private val BgPage       = Color(0xFFF2F6F8)
private val SuccessGreen = Color(0xFF16A34A)
private val DangerRed    = Color(0xFFEF4444)

@Composable
fun ListPelangganScreen(
    viewModel   : PelangganViewModel,
    onBackClick : () -> Unit
) {
    val pelangganState = viewModel.pelangganState.collectAsStateWithLifecycle()
    val searchQuery    = viewModel.searchQuery.collectAsStateWithLifecycle()

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
                        colors = listOf(BluePrimary, Color(0xFF60A5FA))
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
                        text          = "Master Data",
                        fontSize      = 11.sp,
                        color         = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.7.sp
                    )
                    Text(
                        text       = "Pelanggan",
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
                placeholder   = { Text("Cari nama / no. telp…", color = TextGray) },
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
                    focusedBorderColor      = BluePrimary,
                    unfocusedBorderColor    = BluePrimary.copy(alpha = 0.3f),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            when (val state = pelangganState.value) {
                is DataUiState.Idle -> {}
                is DataUiState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = BluePrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text     = "Memuat data pelanggan…",
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
                                onClick = { viewModel.loadPelanggan() },
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                is DataUiState.Success -> {
                    val filtered = state
                        .data.filter { p ->
                        p.nama.contains(searchQuery.value, ignoreCase = true) ||
                                p.noTelp.contains(searchQuery.value, ignoreCase = true)
                    }
                    val totalAktif = filtered.count { it.isActive }

                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatPelangganCard(
                            label    = "Total Pelanggan",
                            value    = "${filtered.size}",
                            subLabel = "terdaftar",
                            subColor = TextGray,
                            modifier = Modifier.weight(1f)
                        )
                        StatPelangganCard(
                            label    = "Aktif",
                            value    = "$totalAktif",
                            subLabel = "pelanggan aktif",
                            subColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector        = Icons.Outlined.PersonOff,
                                    contentDescription = null,
                                    tint               = TextGray,
                                    modifier           = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text     = "Pelanggan tidak ditemukan",
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
                                items(filtered) { pelanggan ->
                                    PelangganRow(pelanggan = pelanggan)
                                    if (filtered.last() != pelanggan) {
                                        HorizontalDivider(
                                            color     = BluePrimary.copy(alpha = 0.07f),
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
private fun PelangganRow(pelanggan: Pelanggan) {
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = pelanggan.nama.take(1).uppercase(),
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = BluePrimary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = pelanggan.nama,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark
            )
            Text(
                text     = if (pelanggan.noTelp.isNotEmpty()) pelanggan.noTelp else "Tidak ada no. telp",
                fontSize = 11.sp,
                color    = TextGray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (pelanggan.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
        ) {
            Text(
                text       = if (pelanggan.isActive) "Aktif" else "Nonaktif",
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = if (pelanggan.isActive) SuccessGreen else DangerRed,
                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun StatPelangganCard(
    label    : String,
    value    : String,
    subLabel : String,
    subColor : Color,
    modifier : Modifier = Modifier
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
            Text(
                text     = subLabel,
                fontSize = 10.sp,
                color    = subColor,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}