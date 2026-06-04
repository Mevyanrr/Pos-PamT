package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_pamt.data.UserRole
import com.example.pos_pamt.data.UserSession

private val Teal  = Color(0xFF00B5A3)
private val Teal2 = Color(0xFF00CDB9)
private val Teal3 = Color(0xFFE0FAF7)
private val Admin = Color(0xFF6366F1)
private val TDark = Color(0xFF0D2B2A)
private val T2    = Color(0xFF3D6360)
private val T3    = Color(0xFF8AB5B1)
private val Green = Color(0xFF14A97A)
private val Danger= Color(0xFFEF4444)
private val Warn  = Color(0xFFF59E0B)
private val White = Color(0xFFFFFFFF)
private val Bg    = Color(0xFFF2F6F8)

data class QItem(val icon: ImageVector, val bg: Color, val tint: Color, val label: String, val onClick: () -> Unit)

@Composable
fun DashboardScreen(
    userSession             : UserSession,
    onLogoutClick           : () -> Unit,
    onNavigateToProduk      : () -> Unit,
    onNavigateToKas         : () -> Unit,
    onNavigateToPelanggan   : () -> Unit,
    onNavigateToPengeluaran : () -> Unit,
    onNavigateToTransaksi   : () -> Unit,
    onNavigateToProfil      : () -> Unit
) {
    val isAdmin    = userSession.role is UserRole.Admin
    val grad       = if (isAdmin) listOf(Admin, Color(0xFF818CF8)) else listOf(Teal, Teal2)
    val roleName   = if (isAdmin) "Admin" else "Kasir"
    val inisial    = userSession.nama.take(2).uppercase().ifEmpty { roleName.take(2) }

    Column(modifier = Modifier.fillMaxSize().background(Bg)) {
        // HEADER
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(grad)).padding(horizontal = 20.dp).padding(top = 44.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text("Selamat datang", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        Text(userSession.nama.ifEmpty { "PAMT Kasir" }, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Surface(modifier = Modifier.padding(top = 6.dp), shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                            Text(roleName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)), contentAlignment = Alignment.Center) {
                            Text(inisial, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.18f)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFA7F3D0)))
                                Text("Online", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFA7F3D0))
                            }
                        }
                    }
                }
                // KAS CARD
                Surface(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 20.dp), shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.18f)) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                        Text("Saldo Kas Utama", fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f), fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
                        Text("Rp 5.098.000", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = (-1).sp, modifier = Modifier.padding(top = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            KStat("12", "Transaksi")
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color.White.copy(alpha = 0.2f)))
                            KStat("Rp 830K", "Hari ini")
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color.White.copy(alpha = 0.2f)))
                            KStat("3", "Pelanggan")
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // QUICK MENU
            SectionLabel(if (isAdmin) "Menu Admin" else "Menu Kasir", top = 20)
            val kasirItems = listOf(
                QItem(Icons.Default.Receipt, Teal3, Teal, "Transaksi\nBaru", onNavigateToTransaksi),
                QItem(Icons.Default.History, Color(0xFFEDE9FE), Color(0xFF7C3AED), "Riwayat", onNavigateToTransaksi),
                QItem(Icons.Default.People, Color(0xFFFEF3C7), Color(0xFFD97706), "Pelanggan", onNavigateToPelanggan),
                QItem(Icons.Default.Inventory, Color(0xFFDCFCE7), Color(0xFF16A34A), "Produk", onNavigateToProduk)
            )
            val adminItems = listOf(
                QItem(Icons.Default.AccountBalance, Color(0xFFDBEAFE), Color(0xFF2563EB), "Kas", onNavigateToKas),
                QItem(Icons.Default.TrendingDown, Color(0xFFFEE2E2), Color(0xFFDC2626), "Pengeluaran", onNavigateToPengeluaran),
                QItem(Icons.Default.Inventory, Color(0xFFDCFCE7), Color(0xFF16A34A), "Produk", onNavigateToProduk),
                QItem(Icons.Default.ReceiptLong, Color(0xFFEDE9FE), Color(0xFF7C3AED), "Semua\nTransaksi", onNavigateToTransaksi),
                QItem(Icons.Default.People, Color(0xFFFEF3C7), Color(0xFFD97706), "Pelanggan", onNavigateToPelanggan),
                QItem(Icons.Default.ManageAccounts, Color(0xFFE0F2FE), Color(0xFF0284C7), "Profiles", onNavigateToProfil)
            )
            QuickGrid(if (isAdmin) adminItems else kasirItems)

            // STATISTIK
            SectionLabel("Statistik Hari Ini", top = 18)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Penjualan", "Rp 830K", "↑ 14%", Color(0xFFDCFCE7), Green, "vs kemarin", Modifier.weight(1f))
                StatCard("Pengeluaran", "Rp 400K", "↓ 5%", Color(0xFFFEE2E2), Danger, "vs kemarin", Modifier.weight(1f))
            }

            // TRANSAKSI TERAKHIR
            SectionLabel("Transaksi Terakhir", top = 18)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hari ini", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TDark)
                        TextButton(onClick = onNavigateToTransaksi) { Text("Lihat semua", fontSize = 12.sp, color = Teal, fontWeight = FontWeight.SemiBold) }
                    }
                    HorizontalDivider(color = Teal.copy(alpha = 0.08f))
                    TrxRow(Icons.Default.ArrowDownward, Color(0xFFDCFCE7), Green, "Budi Santoso", "Paracetamol×2, Vitamin C×1 · 09:12", "+Rp 83K", Green)
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    TrxRow(Icons.Default.ArrowDownward, Color(0xFFDCFCE7), Green, "Siti Rahayu", "Minyak Goreng 2L×3 · 08:40", "+Rp 144K", Green)
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    TrxRow(Icons.Default.ArrowUpward, Color(0xFFFEE2E2), Danger, "Bayar Listrik April", "Pengeluaran · 08:00", "−Rp 400K", Danger)
                }
            }

            // LOG AKTIVITAS
            SectionLabel("Log Aktivitas Terbaru", top = 18)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column {
                    LogRow(Green, "Pelanggan baru Budi Santoso ditambahkan", "09:12 · log_pelanggan")
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    LogRow(Warn, "Data Siti Rahayu diperbarui oleh kasir", "08:40 · log_pelanggan")
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    LogRow(Danger, "Pengeluaran Bayar Listrik April dicatat", "08:00 · log_pengeluaran")
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable private fun QuickGrid(items: List<QItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { item ->
                    Card(modifier = Modifier.weight(1f).clickable { item.onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(vertical = 13.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(item.bg), contentAlignment = Alignment.Center) {
                                Icon(item.icon, null, tint = item.tint, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.height(7.dp))
                            Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3D6360), lineHeight = 13.sp, modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
                        }
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable internal fun SectionLabel(text: String, top: Int = 0) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF8AB5B1), letterSpacing = 0.8.sp, modifier = Modifier.padding(top = top.dp, bottom = 12.dp))
}

@Composable private fun KStat(v: String, l: String) = Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(v, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White); Text(l, fontSize = 10.sp, color = Color.White.copy(alpha = 0.65f)) }
@Composable internal fun StatCard(label: String, value: String, badge: String, bc: Color, btc: Color, sub: String, modifier: Modifier) { Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) { Column(modifier = Modifier.padding(14.dp)) { Text(label, fontSize = 10.sp, color = Color(0xFF8AB5B1), letterSpacing = 0.6.sp, fontWeight = FontWeight.SemiBold); Text(value, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D2B2A), modifier = Modifier.padding(top = 5.dp)); Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) { Surface(shape = RoundedCornerShape(10.dp), color = bc) { Text(badge, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = btc, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) }; Text(sub, fontSize = 10.sp, color = Color(0xFF8AB5B1)) } } } }
@Composable internal fun TrxRow(icon: ImageVector, iconBg: Color, iconTint: Color, name: String, sub: String, amt: String, amtColor: Color) { Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp)) }; Column(modifier = Modifier.weight(1f)) { Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D2B2A), maxLines = 1); Text(sub, fontSize = 11.sp, color = Color(0xFF8AB5B1), maxLines = 1) }; Text(amt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = amtColor) } }
@Composable internal fun LogRow(dot: Color, text: String, time: String) { Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) { Box(modifier = Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(dot)); Column { Text(text, fontSize = 12.sp, color = Color(0xFF3D6360), lineHeight = 18.sp); Text(time, fontSize = 10.sp, color = Color(0xFF8AB5B1), modifier = Modifier.padding(top = 2.dp)) } } }
