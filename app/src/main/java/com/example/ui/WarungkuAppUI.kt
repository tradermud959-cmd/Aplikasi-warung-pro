package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.WarungViewModel
import com.example.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WarungkuAppUI() {
    val navController = rememberNavController()
    val viewModel: WarungViewModel = viewModel()
    
    val ownerName by viewModel.ownerName.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val allDebts by viewModel.allDebts.collectAsState()
    val allCustomers by viewModel.allCustomers.collectAsState()
    val allRestockItems by viewModel.allRestockItems.collectAsState()

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                ownerName = ownerName,
                onNameChange = { viewModel.saveOwnerName(it) },
                onStart = { navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.WELCOME) } },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onAbout = { navController.navigate(Routes.ABOUT) }
            )
        }
        
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                ownerName = ownerName,
                items = allItems,
                debts = allDebts,
                transactions = allTransactions,
                onNavigate = { navController.navigate(it) }
            )
        }
        
        composable(Routes.INVENTORY) {
            InventoryScreen(
                items = allItems,
                onBack = { navController.popBackStack() },
                onAddItem = { name, cat, stock, buy, sell, exp ->
                    viewModel.addItem(name, cat, stock, buy, sell, exp)
                },
                onEditItem = { viewModel.updateItem(it) },
                onDeleteItem = { viewModel.deleteItem(it) }
            )
        }
        
        composable(Routes.TRANSACTION) {
            TransactionScreen(
                items = allItems,
                onBack = { navController.popBackStack() },
                onNavigateToHistory = {
                    navController.popBackStack()
                    navController.navigate(Routes.HISTORY)
                },
                onSaveTransaction = { txItems, cash, isDebt, customerName ->
                    viewModel.saveTransaction(txItems, cash, isDebt, customerName)
                }
            )
        }
        
        composable(Routes.HISTORY) {
            val context = androidx.compose.ui.platform.LocalContext.current
            HistoryScreen(
                transactions = allTransactions,
                onBack = { navController.popBackStack() },
                onScreenshot = { tx -> viewModel.generateAndSaveReceipt(context, tx) }
            )
        }
        
        composable(Routes.DEBT) {
            DebtScreen(
                debts = allDebts,
                onMarkPaid = { debt -> viewModel.markDebtPaid(debt) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.PROFIT) {
            ProfitScreen(transactions = allTransactions, onBack = { navController.popBackStack() })
        }
        
        composable(Routes.CUSTOMER) {
            CustomerScreen(
                customers = allCustomers,
                onAddCustomer = { name, contact, notes -> viewModel.addCustomer(name, contact, notes) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.RESTOCK) {
            RestockScreen(
                items = allRestockItems,
                onAdd = { name, qty -> viewModel.addRestockItem(name, qty) },
                onToggle = { item -> viewModel.toggleRestockItem(item) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen(
                ownerName = ownerName,
                onSaveName = { viewModel.saveOwnerName(it) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
