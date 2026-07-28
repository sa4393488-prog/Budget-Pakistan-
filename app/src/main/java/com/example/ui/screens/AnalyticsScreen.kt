package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TransactionType
import com.example.ui.BudgetUiState
import com.example.ui.IconUtils

@Composable
fun AnalyticsScreen(
    uiState: BudgetUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Analytics & Zakat Estimator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Zakat Estimator Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zakat_calculator_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F766E), // Teal
                                    Color(0xFF042F2C),
                                    Color(0xFF004D38)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Zakat & Sadaqah Estimator 🤲",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Net Eligible Savings: ${IconUtils.formatPkr(uiState.netBalance.coerceAtLeast(0.0), uiState.currencySymbol)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE0F2FE)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (uiState.netBalance >= 150000.0) {
                            Text(
                                text = "Estimated Zakat Payable (2.5%):",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFFDE047)
                            )
                            Text(
                                text = IconUtils.formatPkr(uiState.estimatedZakat, uiState.currencySymbol),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFDE047)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✓ Your net savings exceed the Nisab threshold (~150,000 PKR).",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA7F3D0)
                            )
                        } else {
                            Text(
                                text = "Below Nisab Threshold (~150,000 PKR)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )
                            Text(
                                text = "Zakat is obligatory when net wealth exceeds the silver Nisab threshold (~52.5 tolas of silver).",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE0F2FE)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sadaqah spent this month
                        val sadaqahSpent = uiState.transactions
                            .filter { it.categoryName.contains("Zakat", ignoreCase = true) || it.title.contains("Sadaqah", ignoreCase = true) }
                            .sumOf { it.amount }

                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sadaqah Given This Month:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = IconUtils.formatPkr(sadaqahSpent, uiState.currencySymbol),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE047)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Income vs Expense Ratio Visual Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Income vs Expense Ratio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalFlow = uiState.totalIncome + uiState.totalExpenses
                    val incomePct = if (totalFlow > 0) (uiState.totalIncome / totalFlow).toFloat() else 0.5f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(incomePct.coerceAtLeast(0.01f))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Box(
                            modifier = Modifier
                                .weight((1f - incomePct).coerceAtLeast(0.01f))
                                .background(MaterialTheme.colorScheme.error)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Income (${(incomePct * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Expense (${((1f - incomePct) * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Category Expenses Breakdown List
        item {
            Text(
                text = "Expense Category Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val sortedCategories = uiState.categoryExpenseMap.entries.sortedByDescending { it.value }

        if (sortedCategories.isEmpty()) {
            item {
                Text(
                    text = "No expenses recorded yet for category breakdown.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(sortedCategories) { entry ->
                val categoryName = entry.key
                val amount = entry.value
                val catObj = uiState.categories.find { it.name == categoryName }
                val iconName = catObj?.iconName ?: "Category"
                val pct = if (uiState.totalExpenses > 0) (amount / uiState.totalExpenses).toFloat() else 0f

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = IconUtils.getIconByName(iconName),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = IconUtils.formatPkr(amount, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { pct },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
