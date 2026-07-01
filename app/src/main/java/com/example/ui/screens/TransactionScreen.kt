package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Item
import com.example.data.TransactionItem
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark

import com.example.ui.theme.BackgroundNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    items: List<Item>,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onSaveTransaction: (List<TransactionItem>, Double, Boolean, String?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    
    val selectedItems = remember { mutableStateListOf<TransactionItem>() }
    var cashInput by remember { mutableStateOf("") }
    
    var showDebtDialog by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }

    val total = selectedItems.sumOf { it.qty * it.sellPrice }
    val cash = cashInput.toDoubleOrNull() ?: 0.0
    val change = cash - total

    if (showDebtDialog) {
        AlertDialog(
            onDismissRequest = { showDebtDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Catat Hutang", color = EmeraldMint) },
            text = {
                Column {
                    Text("Total Hutang: Rp${total.toLong()}", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customerName.isNotBlank()) {
                            onSaveTransaction(selectedItems.toList(), 0.0, true, customerName)
                            showDebtDialog = false
                            onNavigateToHistory()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
                ) { Text("Simpan Hutang") }
            },
            dismissButton = {
                TextButton(onClick = { showDebtDialog = false }) { Text("Batal", color = Color.Gray) }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundNavy,
        topBar = {
            TopAppBar(
                title = { Text("Transaksi", color = EmeraldMint) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = EmeraldMint) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Search and select items
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Barang", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            
            if (searchQuery.isNotEmpty()) {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(filteredItems) { item ->
                        Text(
                            text = "${item.name} - Rp${item.sellPrice.toLong()} (Stok: ${item.stock})",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val existing = selectedItems.find { it.itemId == item.id }
                                    if (existing != null) {
                                        val index = selectedItems.indexOf(existing)
                                        selectedItems[index] = existing.copy(qty = existing.qty + 1)
                                    } else {
                                        selectedItems.add(
                                            TransactionItem(
                                                transactionId = 0,
                                                itemId = item.id,
                                                itemName = item.name,
                                                qty = 1,
                                                buyPrice = item.buyPrice,
                                                sellPrice = item.sellPrice
                                            )
                                        )
                                    }
                                    searchQuery = ""
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Barang Terpilih:", color = EmeraldMint, fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(selectedItems) { tItem ->
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tItem.itemName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Harga: Rp${tItem.sellPrice.toLong()}", color = Color.Gray, fontSize = 12.sp)
                                Text("Total: Rp${(tItem.qty * tItem.sellPrice).toLong()}", color = EmeraldMint, fontWeight = FontWeight.Medium)
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { 
                                        val index = selectedItems.indexOf(tItem)
                                        if (tItem.qty > 1) {
                                            selectedItems[index] = tItem.copy(qty = tItem.qty - 1)
                                        } else {
                                            selectedItems.removeAt(index)
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                Text(
                                    text = "${tItem.qty}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                
                                IconButton(
                                    onClick = { 
                                        val index = selectedItems.indexOf(tItem)
                                        selectedItems[index] = tItem.copy(qty = tItem.qty + 1)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+", color = EmeraldMint, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            IconButton(onClick = { selectedItems.remove(tItem) }, modifier = Modifier.padding(start = 8.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = NeonRed)
                            }
                        }
                    }
                }
            }
            
            Divider(color = Color.DarkGray)
            
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total:", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Rp${total.toLong()}", color = EmeraldMint, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cashInput,
                    onValueChange = { cashInput = it },
                    label = { Text("Uang Pelanggan") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kembalian:", color = Color.White, fontSize = 18.sp)
                    Text("Rp${change.toLong()}", color = if (change < 0) NeonRed else EmeraldMint, fontSize = 18.sp)
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        if (selectedItems.isNotEmpty()) {
                            showDebtDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = EmeraldMint),
                    enabled = selectedItems.isNotEmpty(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldMint)
                ) {
                    Text("📝 Hutang", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = {
                        if (selectedItems.isNotEmpty() && change >= 0) {
                            onSaveTransaction(selectedItems.toList(), cash, false, null)
                            onNavigateToHistory()
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                    enabled = selectedItems.isNotEmpty() && change >= 0
                ) {
                    Text("💰 Bayar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
