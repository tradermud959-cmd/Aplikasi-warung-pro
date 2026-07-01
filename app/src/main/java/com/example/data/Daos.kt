package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Update
    suspend fun updateItem(item: Item)

    @Query("UPDATE items SET stock = stock - :qty WHERE id = :itemId")
    suspend fun reduceStock(itemId: Int, qty: Int)

    @Query("UPDATE items SET lastSoldDate = :date WHERE id = :itemId")
    suspend fun updateLastSoldDate(itemId: Int, date: Long)
    
    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<Item>>

    @Delete
    suspend fun deleteItem(item: Item)
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    fun getTransactionItems(transactionId: Int): Flow<List<TransactionItem>>
    
    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getTransactionItemsSync(transactionId: Int): List<TransactionItem>
    
    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>>
}

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY isPaid ASC, date DESC")
    fun getAllDebts(): Flow<List<Debt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt)

    @Query("SELECT * FROM debts WHERE transactionId = :txId LIMIT 1")
    suspend fun getDebtByTransactionId(txId: Int): Debt?

    @Update
    suspend fun updateDebt(debt: Debt)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)
}

@Dao
interface RestockDao {
    @Query("SELECT * FROM restock_items ORDER BY isBought ASC, name ASC")
    fun getAllRestockItems(): Flow<List<RestockItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestockItem(item: RestockItem)

    @Update
    suspend fun updateRestockItem(item: RestockItem)
}
