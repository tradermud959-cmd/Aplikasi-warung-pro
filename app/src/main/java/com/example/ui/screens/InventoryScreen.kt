package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Item
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.BackgroundNavy
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    items: List<Item>,
    onBack: () -> Unit,
    onAddItem: (String, String, Int, Double, Double, Long?) -> Unit,
    onEditItem: (Item) -> Unit,
    onDeleteItem: (Item) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialogFor by remember { mutableStateOf<Item?>(null) }
    var showDeleteConfirmFor by remember { mutableStateOf<Item?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    
    val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000

    Scaffold(
        containerColor = BackgroundNavy,
        topBar = {
            TopAppBar(
                title = { Text("Inventaris", color = EmeraldMint) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = EmeraldMint)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldMint,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Pencarian Barang", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldMint, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn {
                items(filteredItems) { item ->
                    InventoryItemCard(
                        item = item, 
                        thirtyDaysMs = thirtyDaysMs,
                        onEdit = { showEditDialogFor = item },
                        onDelete = { showDeleteConfirmFor = item }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddOrEditInventoryDialog(
            item = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, cat, stock, buy, sell, exp ->
                onAddItem(name, cat, stock, buy, sell, exp)
                showAddDialog = false
            }
        )
    }

    showEditDialogFor?.let { itemToEdit ->
        AddOrEditInventoryDialog(
            item = itemToEdit,
            onDismiss = { showEditDialogFor = null },
            onSave = { name, cat, stock, buy, sell, exp ->
                onEditItem(itemToEdit.copy(
                    name = name,
                    category = cat,
                    stock = stock,
                    buyPrice = buy,
                    sellPrice = sell,
                    expiryDate = exp
                ))
                showEditDialogFor = null
            }
        )
    }

    showDeleteConfirmFor?.let { itemToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmFor = null },
            containerColor = SurfaceDark,
            title = { Text("Hapus Barang", color = EmeraldMint) },
            text = { Text("Apakah yakin ingin menghapus barang ini?\n\n${itemToDelete.name}", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteItem(itemToDelete)
                        showDeleteConfirmFor = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
                ) { Text("Ya") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmFor = null }) { Text("Batal", color = Color.Gray) }
            }
        )
    }
}

@Composable
fun InventoryItemCard(item: Item, thirtyDaysMs: Long, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isLowStock = item.stock <= 5
    val isNearExpiry = item.expiryDate != null && (item.expiryDate - System.currentTimeMillis() < 7L * 24 * 60 * 60 * 1000)
    val isUnsold = item.lastSoldDate != null && (System.currentTimeMillis() - item.lastSoldDate > thirtyDaysMs)
    
    Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Kategori: ${item.category}", color = Color.Gray, fontSize = 14.sp)
                }
                Row {
                    TextButton(onClick = onEdit, contentPadding = PaddingValues(4.dp), modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)) {
                        Text("✏ Edit", color = EmeraldMint, fontSize = 12.sp)
                    }
                    TextButton(onClick = onDelete, contentPadding = PaddingValues(4.dp), modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)) {
                        Text("🗑 Hapus", color = NeonRed, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Stok: ${item.stock}", color = EmeraldMint, fontSize = 16.sp)
                Text(text = "Harga: Rp${item.sellPrice.toLong()}", color = Color.White)
            }
            if (item.expiryDate != null) {
                Text(text = "Expired: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.expiryDate))}", color = Color.Gray)
            }
            
            if (isLowStock) {
                WarningText("⚠ Stok Hampir Habis")
            }
            if (isNearExpiry) {
                WarningText("⚠ Barang Mendekati Kadaluarsa")
            }
            if (isUnsold) {
                WarningText("⚠ Barang kurang laku (Tidak terjual >30 hari)")
            }
        }
    }
}

@Composable
fun WarningText(message: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Text(
        text = message,
        color = NeonRed.copy(alpha = alpha),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun AddOrEditInventoryDialog(
    item: Item?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Double, Double, Long?) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var cat by remember { mutableStateOf(item?.category ?: "") }
    var stock by remember { mutableStateOf(item?.stock?.toString() ?: "") }
    var buy by remember { mutableStateOf(item?.buyPrice?.toString() ?: "") }
    var sell by remember { mutableStateOf(item?.sellPrice?.toString() ?: "") }
    var expiry by remember { 
        mutableStateOf(item?.expiryDate?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "")
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(if (item == null) "Tambahkan Stok" else "Edit Barang", color = EmeraldMint) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Jumlah Stok") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = buy, onValueChange = { buy = it }, label = { Text("Harga Beli") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sell, onValueChange = { sell = it }, label = { Text("Harga Jual") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = expiry, onValueChange = { expiry = it }, label = { Text("Expired (dd/MM/yyyy) opsional") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val expDate = try {
                        if (expiry.isNotBlank()) java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).parse(expiry)?.time else null
                    } catch (e: Exception) { null }
                    
                    onSave(name, cat, stock.toIntOrNull() ?: 0, buy.toDoubleOrNull() ?: 0.0, sell.toDoubleOrNull() ?: 0.0, expDate)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = Color.Gray) }
        }
    )
}
