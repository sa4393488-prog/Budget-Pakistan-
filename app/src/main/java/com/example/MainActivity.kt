package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BudgetViewModel
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.BudgetPakistanTheme

enum class NavDestination(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    TRANSACTIONS("Ledger", Icons.Default.SwapHoriz),
    BUDGETS("Budgets", Icons.Default.ReceiptLong),
    ANALYTICS("Analytics", Icons.Default.Analytics),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BudgetPakistanTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
                var showAddTransactionDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_navigation_bar")
                        ) {
                            NavDestination.entries.forEach { dest ->
                                NavigationBarItem(
                                    selected = currentDestination == dest,
                                    onClick = { currentDestination = dest },
                                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                                    label = { Text(dest.label) },
                                    modifier = Modifier.testTag("nav_${dest.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (currentDestination) {
                        NavDestination.HOME -> HomeScreen(
                            uiState = uiState,
                            onOpenAddTransaction = { showAddTransactionDialog = true },
                            onNavigateToTransactions = { currentDestination = NavDestination.TRANSACTIONS },
                            onNavigateToBills = { currentDestination = NavDestination.BUDGETS },
                            onNavigateToZakat = { currentDestination = NavDestination.ANALYTICS },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        NavDestination.TRANSACTIONS -> TransactionsScreen(
                            uiState = uiState,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onFilterCategoryChange = { viewModel.setFilterCategory(it) },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                            onOpenAddTransaction = { showAddTransactionDialog = true }
                        )

                        NavDestination.BUDGETS -> BudgetsScreen(
                            uiState = uiState,
                            onSetCategoryBudget = { name, limit -> viewModel.setCategoryBudget(name, limit) },
                            onToggleBillPaid = { viewModel.toggleBillPaid(it) },
                            onAddBill = { title, amount, dueDay, provider, cat ->
                                viewModel.addBill(title, amount, dueDay, provider, cat)
                            },
                            onDeleteBill = { viewModel.deleteBill(it) }
                        )

                        NavDestination.ANALYTICS -> AnalyticsScreen(
                            uiState = uiState
                        )

                        NavDestination.SETTINGS -> SettingsScreen(
                            uiState = uiState,
                            onSetCurrencySymbol = { viewModel.setCurrencySymbol(it) },
                            onResetSampleData = { viewModel.resetAndPreFillSampleData() }
                        )
                    }

                    if (showAddTransactionDialog) {
                        AddTransactionDialog(
                            categories = uiState.categories,
                            currencySymbol = uiState.currencySymbol,
                            onDismiss = { showAddTransactionDialog = false },
                            onAddTransaction = { title, amount, type, catName, catIcon, note, method ->
                                viewModel.addTransaction(title, amount, type, catName, catIcon, note, method)
                            }
                        )
                    }
                }
            }
        }
    }
}
