package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TransactionType
import com.example.ui.BudgetUiState
import com.example.ui.IconUtils

@Composable
fun TransactionsScreen(
    uiState: BudgetUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterCategoryChange: (String?) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onOpenAddTransaction: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Expenses, 2: Income

    val typeFilteredList = uiState.filteredTransactions.filter { tx ->
        when (selectedTab) {
            1 -> tx.type == TransactionType.EXPENSE
            2 -> tx.type == TransactionType.INCOME
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("transactions_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Transactions Ledger",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search transactions, notes, methods...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transactions_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedFilterCategory == null,
                        onClick = { onFilterCategoryChange(null) },
                        label = { Text("All Categories") }
                    )
                }
                items(uiState.categories) { cat ->
                    FilterChip(
                        selected = uiState.selectedFilterCategory == cat.name,
                        onClick = {
                            if (uiState.selectedFilterCategory == cat.name) {
                                onFilterCategoryChange(null)
                            } else {
                                onFilterCategoryChange(cat.name)
                            }
                        },
                        label = { Text(cat.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs for All / Expense / Income
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All (${uiState.filteredTransactions.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Expenses") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Income") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary row
            val totalInc = typeFilteredList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExp = typeFilteredList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "In: ${IconUtils.formatPkr(totalInc, uiState.currencySymbol)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Out: ${IconUtils.formatPkr(totalExp, uiState.currencySymbol)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List
            if (typeFilteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching transactions found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(typeFilteredList, key = { it.id }) { tx ->
                        TransactionItemRow(
                            tx = tx,
                            currencySymbol = uiState.currencySymbol,
                            onDelete = { onDeleteTransaction(tx.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
