package com.example.data

class AppRepository(
    val itemDao: ItemDao,
    val transactionDao: TransactionDao,
    val debtDao: DebtDao,
    val customerDao: CustomerDao,
    val restockDao: RestockDao
) {
    suspend fun processTransaction(
        transaction: Transaction,
        items: List<TransactionItem>
    ): Int {
        val txId = transactionDao.insertTransaction(transaction).toInt()
        val mappedItems = items.map { it.copy(transactionId = txId) }
        transactionDao.insertTransactionItems(mappedItems)
        
        items.forEach { txItem ->
            itemDao.reduceStock(txItem.itemId, txItem.qty)
            itemDao.updateLastSoldDate(txItem.itemId, transaction.timestamp)
        }
        return txId
    }
}
