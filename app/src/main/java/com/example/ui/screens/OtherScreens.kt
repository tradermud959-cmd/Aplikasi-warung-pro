package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(transactions: List<Transaction>, onBack: () -> Unit, onScreenshot: (Transaction) -> Unit) {
    Scaffold(
        containerColor = BackgroundNavy,
        topBar = {
            TopAppBar(
                title = { Text("Riwayat", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(transactions) { tx ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(tx.timestamp)), color = Color.Gray)
                            if (tx.isDebt) {
                                Text(if (tx.isDebtPaid) "Hutang Lunas" else "Hutang", color = if (tx.isDebtPaid) EmeraldMint else NeonRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Total: Rp${tx.total.toLong()}", color = EmeraldMint, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            
                            Button(
                                onClick = { onScreenshot(tx) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                            ) {
                                Text("📸 Screenshot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitScreen(transactions: List<Transaction>, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Harian", "Mingguan", "Bulanan")
    
    val now = System.currentTimeMillis()
    val filterTime = when (selectedTab) {
        0 -> now - (24 * 60 * 60 * 1000L)
        1 -> now - (7 * 24 * 60 * 60 * 1000L)
        else -> now - (30L * 24 * 60 * 60 * 1000L)
    }
    
    val filteredTx = transactions.filter { it.timestamp >= filterTime && (!it.isDebt || it.isDebtPaid) }
    
    val totalIncome = filteredTx.sumOf { it.total }
    val totalCapital = filteredTx.sumOf { it.capital }
    val netProfit = totalIncome - totalCapital

    Scaffold(
        containerColor = BackgroundNavy,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Laporan & Profit", color = EmeraldMint) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SurfaceDark,
                    contentColor = EmeraldMint
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, color = if (selectedTab == index) EmeraldMint else Color.Gray) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Pemasukan", color = Color.Gray)
                    Text("Rp${totalIncome.toLong()}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Modal", color = Color.Gray)
                    Text("Rp${totalCapital.toLong()}", color = NeonRed, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Keuntungan Bersih", color = EmeraldMint, fontWeight = FontWeight.Bold)
                    Text("Rp${netProfit.toLong()}", color = EmeraldMint, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(debts: List<Debt>, onMarkPaid: (Debt) -> Unit, onBack: () -> Unit) {
    Scaffold(
        containerColor = BackgroundNavy,
        topBar = {
            TopAppBar(
                title = { Text("Hutang", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(debts) { debt ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(debt.customerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(debt.date)), color = Color.Gray, fontSize = 12.sp)
                            }
                            Text(
                                if (debt.isPaid) "🟢 Lunas" else "🔴 Belum Lunas", 
                                color = if (debt.isPaid) EmeraldMint else NeonRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(debt.itemsDescription, color = Color.LightGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Rp${debt.amount.toLong()}", color = NeonRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            
                            if (!debt.isPaid) {
                                Button(onClick = { onMarkPaid(debt) }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)) {
                                    Text("Tandai Lunas", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
