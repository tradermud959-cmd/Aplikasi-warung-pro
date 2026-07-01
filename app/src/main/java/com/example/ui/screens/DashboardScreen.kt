package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Debt
import com.example.data.Item
import com.example.data.Transaction
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    ownerName: String,
    items: List<Item>,
    debts: List<Debt>,
    transactions: List<Transaction>,
    onNavigate: (String) -> Unit
) {
    val analyses = listOf(
        "Analisa data stock hari ini",
        "Cek barang yang hampir habis",
        "Barang paling laris minggu ini",
        "Performa penjualan"
    )
    var currentAnalysisIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            delay(3000)
            currentAnalysisIndex = (currentAnalysisIndex + 1) % analyses.size
        }
    }

    val lowStockCount = items.count { it.stock <= 5 }
    val nearExpiryCount = items.count { it.expiryDate != null && (it.expiryDate - System.currentTimeMillis() < 7L * 24 * 60 * 60 * 1000) }
    val hasWarning = lowStockCount > 0 || nearExpiryCount > 0
    
    val totalProfit = transactions.sumOf { it.total - it.capital }
    val totalDebtAmount = debts.filter { !it.isPaid }.sumOf { it.amount }
    val activeDebtorsCount = debts.filter { !it.isPaid }.distinctBy { it.customerName }.size
    
    // Sort items by least stock first to show in 'Inventaris Terkini'
    val recentItems = items.sortedBy { it.stock }.take(5)

    Scaffold(
        containerColor = BackgroundNavy,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Routes.TRANSACTION) },
                containerColor = EmeraldMint,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .size(64.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = EmeraldMint,
                        ambientColor = EmeraldMint
                    )
            ) {
                Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = (-2).dp))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BottomNavBtn("🏠", "DASHBOARD") { }
                BottomNavBtn("📜", "RIWAYAT") { onNavigate(Routes.HISTORY) }
                BottomNavBtn("📦", "STOK") { onNavigate(Routes.INVENTORY) }
                BottomNavBtn("📉", "LAPORAN") { onNavigate(Routes.PROFIT) }
                BottomNavBtn("👥", "PELANGGAN") { onNavigate(Routes.CUSTOMER) }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SurfaceDark, RoundedCornerShape(12.dp))
                            .border(1.dp, EmeraldMint, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("W", color = EmeraldMint, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(analyses[currentAnalysisIndex].uppercase(), color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Halo, $ownerName", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
                
                Box(
                    modifier = Modifier.size(40.dp).background(Slate800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(8.dp).background(EmeraldMint, CircleShape).shadow(8.dp, spotColor = EmeraldMint, ambientColor = EmeraldMint))
                }
            }
            
            // Warning banner
            if (hasWarning) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(DangerBg.copy(alpha = alpha * 0.2f), RoundedCornerShape(16.dp))
                        .border(1.dp, NeonRed.copy(alpha = alpha), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(NeonRed, CircleShape)
                            .shadow(8.dp, spotColor = NeonRed, ambientColor = NeonRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("PERINGATAN STOK & KADALUARSA", color = NeonRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val msg = mutableListOf<String>()
                        if (lowStockCount > 0) msg.add("$lowStockCount Hampir Habis")
                        if (nearExpiryCount > 0) msg.add("$nearExpiryCount Mendekati Expired")
                        Text(msg.joinToString(" & "), color = Color.White, fontSize = 11.sp)
                    }
                }
            }
            
            // Bento Grid Stats
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .background(SurfaceDark, RoundedCornerShape(24.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                        .clickable { onNavigate(Routes.PROFIT) }
                ) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Text("PROFIT", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (totalProfit >= 0) "+${formatShortNum(totalProfit)}" else formatShortNum(totalProfit),
                            color = if (totalProfit >= 0) EmeraldMint else NeonRed,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Bulan ini", color = Slate400, fontSize = 10.sp)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(128.dp)
                        .background(SurfaceDark, RoundedCornerShape(24.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                        .padding(16.dp)
                        .clickable { onNavigate(Routes.DEBT) }
                ) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                        Text("HUTANG", color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(formatShortNum(totalDebtAmount), color = NeonRed, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("$activeDebtorsCount Pelanggan", color = Slate400, fontSize = 10.sp)
                    }
                }
            }
            
            // Inventory List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(24.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("INVENTARIS TERKINI", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("LIHAT SEMUA", color = EmeraldMint, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigate(Routes.INVENTORY) })
                    }
                    
                    if (recentItems.isEmpty()) {
                        Text("Belum ada barang di inventaris.", color = Slate500, fontSize = 12.sp)
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(recentItems) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.name, color = Color.White, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        val expStr = item.expiryDate?.let { " • Exp: ${SimpleDateFormat("MM/yy", Locale.getDefault()).format(Date(it))}" } ?: ""
                                        Text("Sisa ${item.stock} Unit$expStr", color = Slate500, fontSize = 10.sp)
                                    }
                                    
                                    val statusColor = if (item.stock <= 5) NeonRed else if (item.stock <= 10) Color(0xFFFFA500) else EmeraldMint
                                    val statusText = if (item.stock <= 5) "RE-STOCK!" else if (item.stock <= 10) "MINIM" else "STOK AMAN"
                                    
                                    Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                                }
                                if (item != recentItems.last()) {
                                    HorizontalDivider(color = Slate800, thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun formatShortNum(num: Double): String {
    return when {
        num >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", num / 1_000_000_000)
        num >= 1_000_000 -> String.format(Locale.US, "%.1fM", num / 1_000_000)
        num >= 1_000 -> String.format(Locale.US, "%.1fK", num / 1_000)
        else -> num.toLong().toString()
    }
}

@Composable
fun BottomNavBtn(icon: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}
