package com.example.pos_pamt.ui.view

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
import com.example.pos_pamt.data.Kas
import com.example.pos_pamt.viewmodel.DataUiState
import com.example.pos_pamt.viewmodel.KasViewModel
import androidx.compose.foundation.background

private val AdminPurple = Color(0xFF6366F1)
private val AdminLight  = Color(0xFFEDE9FE)
private val TextDark    = Color(0xFF0D2B2A)
private val TextGray    = Color(0xFF8AB5B1)
private val BgPage      = Color(0xFFF2F6F8)
private val DangerRed   = Color(0xFFEF4444)
private val SuccessGreen = Color(0xFF14A97A)

@Composable
fun ListKasScreen(
    viewModel   : KasViewModel,
    onBackClick : () -> Unit
) {
    val kasState = viewModel.kasState.collectAsStateWithLifecycle()

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
                        colors = listOf(AdminPurple, Color(0xFF818CF8))
                    )
                )
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Back
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
                        text     = "Admin",
                        fontSize = 11.sp,
                        color    = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.7.sp
                    )
                    Text(
                        text       = "Manajemen Kas",
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

            when (val state = kasState.value) {

                is DataUiState.Idle -> {
                }

                is DataUiState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AdminPurple)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text     = "Memuat data kas…",
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
                                onClick = { viewModel.loadKas() },
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = AdminPurple
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                is DataUiState.Success -> {
                    val kasList    = state.data
                    val totalSaldo = kasList.sumOf { it.saldo }
                    val jumlahKas  = kasList.size
                    val aktif      = kasList.count { it.isActive }

                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatKasCard(
                            label    = "Jumlah Kas",
                            value    = "$jumlahKas",
                            subLabel = "$aktif aktif",
                            subColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatKasCard(
                            label    = "Total Saldo",
                            value    = formatRupiah(totalSaldo),
                            subLabel = "semua kas",
                            subColor = TextGray,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape  = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = AdminLight)
                    ) {
                        Row(
                            modifier              = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.Top
                        ) {
                            Text(text = "🔒", fontSize = 14.sp)
                            Text(
                                text     = "Halaman ini hanya dapat diakses oleh Admin. Setiap perubahan saldo otomatis tercatat di log_kas.",
                                fontSize = 11.sp,
                                color    = Color(0xFF3D6360),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Text(
                        text     = "DAFTAR KAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color    = TextGray,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (kasList.isEmpty()) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "💰", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text     = "Belum ada data kas",
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
                                items(kasList) { kas ->
                                    KasRow(kas = kas)
                                    if (kasList.last() != kas) {
                                        HorizontalDivider(
                                            color     = AdminPurple.copy(alpha = 0.07f),
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
private fun KasRow(kas: Kas) {
    Row(
        modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (kas.isActive) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "💵", fontSize = 18.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = kas.nama,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark
            )
            Text(
                text     = "Saldo: ${formatRupiah(kas.saldo)} · is_active: ${kas.isActive}",
                fontSize = 11.sp,
                color    = TextGray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (kas.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
        ) {
            Text(
                text       = if (kas.isActive) "Aktif" else "Nonaktif",
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = if (kas.isActive) Color(0xFF16A34A) else DangerRed,
                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun StatKasCard(
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
                text     = label,
                fontSize = 10.sp,
                color    = TextGray,
                letterSpacing = 0.6.sp
            )
            Text(
                text       = value,
                fontSize   = 20.sp,
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

private fun formatRupiah(amount: Double): String {
    return "Rp " + "%,.0f".format(amount).replace(',', '.')
}