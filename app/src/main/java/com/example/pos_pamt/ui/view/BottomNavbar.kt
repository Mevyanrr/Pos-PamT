package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_pamt.navigation.Screen


private data class NavItem(val route: String, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Screen.Dashboard.route,  Icons.Default.Home,          "Beranda"),
    NavItem(Screen.Transaksi.route,  Icons.Default.Receipt,       "Transaksi"),
    NavItem(Screen.Pelanggan.route,  Icons.Default.People,        "Pelanggan"),
    NavItem(Screen.Produk.route,     Icons.Default.Inventory,     "Produk"),
    NavItem(Screen.Profil.route,     Icons.Default.AccountCircle, "Profil")
)

@Composable
fun BottomNavBar(
    currentRoute : String,
    isAdmin      : Boolean,
    onBeranda    : () -> Unit,
    onTransaksi  : () -> Unit,
    onPelanggan  : () -> Unit,
    onProduk     : () -> Unit,
    onProfil     : () -> Unit
) {
    val activeColor = if (isAdmin) Admin else Teal
    val actions     = listOf(onBeranda, onTransaksi, onPelanggan, onProduk, onProfil)

    Column {
        HorizontalDivider(color = activeColor.copy(alpha = 0.1f), thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp)
                .padding(top = 8.dp, bottom = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { i, item ->
                val isActive = currentRoute == item.route
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) { actions[i]() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        tint               = if (isActive) activeColor else Gray,
                        modifier           = Modifier.size(if (isActive) 24.dp else 22.dp)
                    )
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isActive) activeColor else Gray
                    )
                    if (isActive)
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(activeColor))
                    else
                        Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
