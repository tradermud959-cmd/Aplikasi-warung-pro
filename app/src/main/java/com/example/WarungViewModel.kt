package com.example

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WarungViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as WarungkuApp
    private val repository = app.repository
    private val storeConfig = app.dataStore

    val ownerNameKey = stringPreferencesKey("owner_name")
    val ownerName: StateFlow<String> = storeConfig.data
        .map { it[ownerNameKey] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val allItems = repository.itemDao.getAllItems().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allTransactions = repository.transactionDao.getAllTransactions().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allDebts = repository.debtDao.getAllDebts().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allCustomers = repository.customerDao.getAllCustomers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allRestockItems = repository.restockDao.getAllRestockItems().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveOwnerName(name: String) {
        viewModelScope.launch {
            storeConfig.edit { it[ownerNameKey] = name }
        }
    }

    // --- Inventory ---
    fun addItem(name: String, category: String, stock: Int, buyPrice: Double, sellPrice: Double, expiryDate: Long?) {
        viewModelScope.launch {
            repository.itemDao.insertItem(Item(
                name = name,
                category = category,
                stock = stock,
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                expiryDate = expiryDate
            ))
        }
    }
    
    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.itemDao.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.itemDao.deleteItem(item)
        }
    }

    // --- Transaction ---
    fun saveTransaction(items: List<TransactionItem>, cash: Double, isDebt: Boolean = false, customerName: String? = null) {
        viewModelScope.launch {
            val total = items.sumOf { it.qty * it.sellPrice }
            val capital = items.sumOf { it.qty * it.buyPrice }
            val change = if (isDebt) 0.0 else cash - total
            val transaction = Transaction(
                timestamp = System.currentTimeMillis(),
                total = total,
                cash = if (isDebt) 0.0 else cash,
                change = change,
                capital = capital,
                isDebt = isDebt,
                isDebtPaid = false
            )
            val txId = repository.processTransaction(transaction, items)

            if (isDebt && customerName != null) {
                val itemsDesc = items.joinToString(", ") { "${it.itemName} x${it.qty}" }
                repository.debtDao.insertDebt(Debt(
                    customerName = customerName,
                    amount = total,
                    date = transaction.timestamp,
                    isPaid = false,
                    itemsDescription = itemsDesc,
                    transactionId = txId
                ))
            }
        }
    }

    // --- Debt ---
    fun markDebtPaid(debt: Debt) {
        viewModelScope.launch {
            repository.debtDao.updateDebt(debt.copy(isPaid = true))
            debt.transactionId?.let { txId ->
                val tx = repository.transactionDao.getTransactionById(txId)
                if (tx != null) {
                    repository.transactionDao.updateTransaction(tx.copy(isDebtPaid = true, cash = tx.total))
                }
            }
        }
    }

    // --- Customers ---
    fun addCustomer(name: String, contact: String, notes: String) {
        viewModelScope.launch {
            repository.customerDao.insertCustomer(Customer(
                name = name,
                contact = contact,
                notes = notes
            ))
        }
    }

    // --- Restock ---
    fun addRestockItem(name: String, qty: Int) {
        viewModelScope.launch {
            repository.restockDao.insertRestockItem(RestockItem(
                name = name,
                qty = qty
            ))
        }
    }
    
    fun toggleRestockItem(item: RestockItem) {
        viewModelScope.launch {
            repository.restockDao.updateRestockItem(item.copy(isBought = !item.isBought))
        }
    }
    
    fun generateAndSaveReceipt(context: android.content.Context, transaction: Transaction) {
        viewModelScope.launch {
            val items = repository.transactionDao.getTransactionItemsSync(transaction.id)
            val customerName = if (transaction.isDebt) {
                repository.debtDao.getDebtByTransactionId(transaction.id)?.customerName ?: "Pelanggan"
            } else {
                "Umum"
            }
            
            val bitmap = com.example.utils.ReceiptGenerator.generate(context, transaction, items, customerName)
            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date(transaction.timestamp))
            val fileName = "Warungku_Nota_${customerName.replace(" ", "_")}_$timestamp.jpg"
            com.example.utils.ReceiptGenerator.saveToGallery(context, bitmap, fileName)
            
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                android.widget.Toast.makeText(context, "Nota berhasil disimpan ke Galeri", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
