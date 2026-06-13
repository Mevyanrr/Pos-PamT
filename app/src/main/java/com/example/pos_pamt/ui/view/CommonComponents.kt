package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable


@Composable
fun LoadingBox() {
    Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) {
        CircularProgressIndicator(color = Teal)
    }
}

@Composable
fun SmallLoading() {
    Box(Modifier.fillMaxWidth().height(60.dp), Alignment.Center) {
        CircularProgressIndicator(color = Teal, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
    }
}

@Composable
fun ErrorBox(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Icon(Icons.Default.Info, null, tint = Danger, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(8.dp))
        Text(message, fontSize = 13.sp, color = Danger, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Teal), shape = RoundedCornerShape(10.dp)) {
            Text("Coba Lagi")
        }
    }
}

@Composable
fun EmptyBox(text: String, icon: ImageVector) {
    Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = T3, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(10.dp))
            Text(text, fontSize = 13.sp, color = T3)
        }
    }
}

@Composable
fun InfoBox(icon: ImageVector, iconTint: Color, bg: Color, text: String) {
    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), shape = RoundedCornerShape(8.dp), color = bg) {
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp).padding(top = 1.dp))
            Text(text, fontSize = 11.sp, color = TextMid, lineHeight = 16.sp)
        }
    }
}

@Composable
fun SmallIconBtn(icon: ImageVector, bg: Color, tint: Color, onClick: () -> Unit = {}) {
    Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)).background(bg).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(15.dp))
    }
}

fun rupiahD(v: Double) = "Rp " + "%,.0f".format(v).replace(',', '.')
fun formatWaktu(t: String) = try { t.take(16).replace("T", " ") } catch (e: Exception) { t }