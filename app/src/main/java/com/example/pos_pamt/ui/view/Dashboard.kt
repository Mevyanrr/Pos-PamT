package com.example.pos_pamt.ui.view


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

private val TealPrimary = Color(0xFF00B5A3)
private val TealLight = Color(0xFFE0FAF7)
private val TextDark = Color(0xFF0D2B2A)
private val TextGray = Color(0xFF8AB5B1)
private val BgPage = Color(0xFFF2F6F8)

@Composable
fun DashboardScreen(
    onLogoutClick : () -> Unit,
    onNavigateToBarang : () -> Unit,
    onNavigateToKas : () -> Unit,
    onNavigateToPelanggan : () -> Unit,
    ) {
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
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Selamat datang",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "PAMT Kasir",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Surface(
                        modifier = Modifier.padding(top = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Kasir",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {

            Text(
                text = "MENU UTAMA",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MenuCard(
                icon = "📦",
                iconBg = TealLight,
                title = "List Barang",
                subtitle = "Lihat daftar produk & stok",
                onClick = onNavigateToBarang
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                icon = "💰",
                iconBg = Color(0xFFFEF3C7),
                title = "List Kas",
                subtitle = "Lihat daftar kas & saldo",
                onClick = onNavigateToKas
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                icon = "👤",
                iconBg = Color(0xFFEFF6FF),
                title = "List Pelanggan",
                subtitle = "Lihat daftar pelanggan terdaftar",
                onClick = onNavigateToPelanggan
            )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Keluar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    icon : String,
    iconBg : Color,
    title : String,
    subtitle : String,
    onClick : () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(text = "›", fontSize = 22.sp, color = TextGray)
        }
    }
}
