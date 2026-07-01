package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    ownerName: String,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    val context = LocalContext.current as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundNavy)
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = EmeraldMint,
                        ambientColor = EmeraldMint
                    )
                    .background(SurfaceDark, RoundedCornerShape(24.dp))
                    .border(2.dp, EmeraldMint, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_warungku_logo),
                    contentDescription = "Warungku Logo",
                    modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Warungku",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "OFFLINE MANAGEMENT",
                color = EmeraldMint,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Middle section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Selamat Datang",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Kelola stok, transaksi, hutang, dan keuntungan warung dengan mudah.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = ownerName,
                onValueChange = onNameChange,
                label = { Text("PEMILIK WARUNG", color = Slate500, fontSize = 10.sp, letterSpacing = 1.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldMint,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = {
                    if (ownerName.isBlank()) {
                        onNameChange("Bapak Agus")
                    }
                    onStart()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = EmeraldMint,
                        ambientColor = EmeraldMint
                    )
            ) {
                Text("▶ MULAI", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onSettings,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate800),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SurfaceDark,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("⚙ PENGATURAN")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(
                onClick = onAbout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ℹ Tentang Aplikasi", color = Slate500, fontSize = 14.sp)
            }
        }
        
        // Bottom section
        Text(
            text = "DEVELOPER: MASKAAV • v1.0.0",
            color = Slate500,
            fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
