package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Item::class, Transaction::class, TransactionItem::class, Debt::class, Customer::class, RestockItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun transactionDao(): TransactionDao
    abstract fun debtDao(): DebtDao
    abstract fun customerDao(): CustomerDao
    abstract fun restockDao(): RestockDao
}
