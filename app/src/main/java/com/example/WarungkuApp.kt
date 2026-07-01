package com.example

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.AppDatabase
import com.example.data.AppRepository
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "settings")

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE transactions ADD COLUMN isDebt INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE transactions ADD COLUMN isDebtPaid INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE debts ADD COLUMN itemsDescription TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE debts ADD COLUMN transactionId INTEGER")
    }
}

class WarungkuApp : Application() {
    lateinit var database: AppDatabase
        private set
    
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "warungku-db"
        )
        .addMigrations(MIGRATION_1_2)
        .build()
        
        repository = AppRepository(
            itemDao = database.itemDao(),
            transactionDao = database.transactionDao(),
            debtDao = database.debtDao(),
            customerDao = database.customerDao(),
            restockDao = database.restockDao()
        )
    }
}
