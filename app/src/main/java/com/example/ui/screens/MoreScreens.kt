package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(customers: List<Customer>, onAddCustomer: (String, String, String) -> Unit, onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Pelanggan", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = EmeraldMint) { Text("+", color = Color.Black, fontSize = 24.sp) }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(customers) { c ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(c.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(c.contact, color = Color.Gray)
                        Text(c.notes, color = Color.LightGray)
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Tambah Pelanggan", color = EmeraldMint) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Kontak") })
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Catatan") })
                }
            },
            confirmButton = {
                Button(onClick = { onAddCustomer(name, contact, notes); showDialog = false }) { Text("Simpan") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestockScreen(items: List<RestockItem>, onAdd: (String, Int) -> Unit, onToggle: (RestockItem) -> Unit, onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Belanja Restock", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = EmeraldMint) { Text("+", color = Color.Black, fontSize = 24.sp) }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(items) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(item.name, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Jumlah: ${item.qty}", color = Color.Gray)
                        }
                        Checkbox(checked = item.isBought, onCheckedChange = { onToggle(item) }, colors = CheckboxDefaults.colors(checkedColor = EmeraldMint, checkmarkColor = Color.Black))
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var qty by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Tambah Belanja", color = EmeraldMint) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") })
                    OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Jumlah") })
                }
            },
            confirmButton = {
                Button(onClick = { onAdd(name, qty.toIntOrNull() ?: 1); showDialog = false }) { Text("Simpan") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(ownerName: String, onSaveName: (String) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(ownerName) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Pemilik Toko") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSaveName(name) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)) {
                Text("Simpan Nama")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Backup / Restore Data", color = EmeraldMint, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { /* Simulated offline backup */ }, colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Color.White)) { Text("Backup") }
                Button(onClick = { /* Simulated offline restore */ }, colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Color.White)) { Text("Restore") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi", color = EmeraldMint) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, tint = EmeraldMint, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("M", color = EmeraldMint, fontSize = 64.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(16.dp))
                Text("WARUNGKU", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Aplikasi pencatatan warung offline yang membantu mengelola stok, transaksi, hutang, pelanggan, dan keuntungan secara mudah.",
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(64.dp))
            Text("Developer: Maskaav", color = EmeraldMint, fontWeight = FontWeight.Bold)
        }
    }
}
