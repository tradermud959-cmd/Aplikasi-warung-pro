package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val stock: Int,
    val buyPrice: Double,
    val sellPrice: Double,
    val expiryDate: Long?, // Nullable if no expiry
    val lastSoldDate: Long? = null
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val total: Double,
    val cash: Double,
    val change: Double,
    val capital: Double,
    val isDebt: Boolean = false,
    val isDebtPaid: Boolean = false
)

@Entity(tableName = "transaction_items")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val itemId: Int,
    val itemName: String,
    val qty: Int,
    val buyPrice: Double,
    val sellPrice: Double
)

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val amount: Double,
    val date: Long,
    val isPaid: Boolean = false,
    val itemsDescription: String = "",
    val transactionId: Int? = null
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contact: String,
    val notes: String,
    val totalTransactions: Double = 0.0
)

@Entity(tableName = "restock_items")
data class RestockItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val qty: Int,
    val isBought: Boolean = false
)
