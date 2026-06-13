package com.example.pos_pamt.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.pos_pamt.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_pamt.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    email : String,
    password : String,
    uiState : AuthUiState,
    onEmailChange : (String) -> Unit,
    onPasswordChange : (String) -> Unit,
    onLoginClick : () -> Unit
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
                    brush = Brush.linearGradientBrush(
                        colors = listOf(TealPrimary, Teal2)
                    )
                )
                .padding(horizontal = 28.dp, vertical = 60.dp)
        ) {
            Column {
                Text(
                    text = "KASIRIN",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Aplikasi Pengelola Kasir dan Inventori",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Masuk ke Akun",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Masukkan email dan password Anda",
                fontSize = 13.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("email@gmail.com", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TealPrimary.copy(alpha = 0.3f),
                    focusedLabelColor = TealPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                placeholder = { Text("Password…", color = TextGray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = TealPrimary.copy(alpha = 0.3f),
                    focusedLabelColor = TealPrimary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState is AuthUiState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RedLight
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.message,
                        color = DangerDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = uiState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary
                )
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Masuk",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Fakultas Ilmu Komputer · Universitas Brawijaya · 2026",
                fontSize = 10.sp,
                color = TextGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private fun Brush.Companion.linearGradientBrush(colors: List<Color>) =
    linearGradient(colors = colors)
