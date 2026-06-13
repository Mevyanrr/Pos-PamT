package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
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
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_pamt.data.UserRole
import com.example.pos_pamt.data.UserSession


@Composable
fun ProfilScreen(
    userSession  : UserSession,
    onLogoutClick: () -> Unit,
    onBackClick  : () -> Unit
) {
    val isAdmin  = userSession.role is UserRole.Admin
    val grad     = if (isAdmin) listOf(Admin, AdminPurple2) else listOf(Teal, Teal2)
    val roleName = if (isAdmin) "Admin" else "Kasir"
    val inisial  = userSession.nama.take(2).uppercase().ifEmpty { roleName.take(2) }

    Column(modifier = Modifier.fillMaxSize().background(BgPage)) {
        // HEADER
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(grad))
                .padding(horizontal = 20.dp)
                .padding(top = 50.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(76.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)), contentAlignment = Alignment.Center) {
                    Text(inisial, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Text(userSession.nama.ifEmpty { "PAMT Kasir" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(userSession.email.ifEmpty { "-" }, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(top = 3.dp))
                Surface(modifier = Modifier.padding(top = 10.dp), shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                    Text(roleName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            // STAT
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfilStatCard("47", "Transaksi\nBulan Ini", Teal3, Teal, Modifier.weight(1f))
                ProfilStatCard("Rp 3.8M", "Total\nPenjualan", GreenLight, Green, Modifier.weight(1f))
            }

            // INFO AKUN
            SectionLabel("Info Akun", top = 18)
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column {
                    InfoAkunRow(Icons.Default.Person, "Username", userSession.nama.ifEmpty { "-" })
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    InfoAkunRow(Icons.Default.Email, "Email", userSession.email.ifEmpty { "-" })
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    InfoAkunRow(Icons.Default.Badge, "Role", roleName)
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    InfoAkunRow(Icons.Default.Circle, "Status", "Aktif")
                }
            }

            // PENGATURAN
            SectionLabel("Pengaturan", top = 18)
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column {
                    PengaturanRow(Icons.Default.Lock, BlueLight, BluePrimary, "Ganti Password", "Keamanan akun")
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    PengaturanRow(Icons.Default.Edit, Teal3, Teal, "Edit Profil", "Ubah username")
                    HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                    PengaturanRow(Icons.Default.History, GreenLight, Green, "Riwayat Aktivitas", "log_pelanggan, log_produk")
                    if (isAdmin) {
                        HorizontalDivider(color = Teal.copy(alpha = 0.07f), thickness = 0.5.dp)
                        PengaturanRow(Icons.Default.ManageAccounts, Admin2, Admin, "Kelola Pengguna", "Manajemen user")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Danger)
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar dari Akun", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            // FOOTER
            Spacer(Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                Text("PAMT", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Teal)
                Text("Pengembangan Aplikasi Mobile Terapan", fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
                Text("Fakultas Ilmu Komputer · Universitas Brawijaya · 2026", fontSize = 10.sp, color = T3, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable private fun ProfilStatCard(value: String, label: String, bg: Color, textColor: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, color = TextLight, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp)
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(top = 5.dp))
            Text("bulan ini", fontSize = 10.sp, color = TextLight, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable private fun ProfilesRow(av: String, avBg: Color, avColor: Color, nama: String, email: String, role: String, roleColor: Color) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(avBg), contentAlignment = Alignment.Center) {
            Text(av, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = avColor)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Text(email, fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
        }
        Text(role, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = roleColor)
    }
}

@Composable private fun InfoAkunRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = T3, modifier = Modifier.size(18.dp))
        Text(label, fontSize = 13.sp, color = T3, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
    }
}

@Composable private fun PengaturanRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, sub: String) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(iconBg), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TDark)
            Text(sub, fontSize = 11.sp, color = T3, modifier = Modifier.padding(top = 1.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = T3, modifier = Modifier.size(20.dp))
    }
}