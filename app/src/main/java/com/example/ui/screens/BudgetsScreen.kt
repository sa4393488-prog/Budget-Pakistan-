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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.RecurringBillEntity
import com.example.ui.BudgetUiState
import com.example.ui.CategoryBudgetProgress
import com.example.ui.IconUtils

@Composable
fun BudgetsScreen(
    uiState: BudgetUiState,
    onSetCategoryBudget: (String, Double) -> Unit,
    onToggleBillPaid: (RecurringBillEntity) -> Unit,
    onAddBill: (title: String, amount: Double, dueDay: Int, provider: String, category: String) -> Unit,
    onDeleteBill: (RecurringBillEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Category Budgets, 1: Recurring Bills
    var editingCategoryProgress by remember { mutableStateOf<CategoryBudgetProgress?>(null) }
    var showAddBillDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("budgets_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Budgets & Recurring Bills",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Category Limits") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Utility Bills (${uiState.bills.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // Category Budgets Tab
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.budgetProgressList) { progress ->
                    CategoryBudgetCard(
                        progress = progress,
                        currencySymbol = uiState.currencySymbol,
                        onEdit = { editingCategoryProgress = progress }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Recurring Bills Tab
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val unpaidCount = uiState.bills.count { !it.isPaidThisMonth }
                    Text(
                        text = if (unpaidCount > 0) "$unpaidCount Unpaid Bill(s) Remaining" else "All Bills Paid This Month! 🎉",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (unpaidCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showAddBillDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Bill")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.bills, key = { it.id }) { bill ->
                        RecurringBillItem(
                            bill = bill,
                            currencySymbol = uiState.currencySymbol,
                            onTogglePaid = { onToggleBillPaid(bill) },
                            onDelete = { onDeleteBill(bill) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Edit Budget Dialog
    editingCategoryProgress?.let { prog ->
        EditCategoryBudgetDialog(
            categoryName = prog.categoryName,
            currentLimit = prog.limit,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { editingCategoryProgress = null },
            onSave = { newLimit ->
                onSetCategoryBudget(prog.categoryName, newLimit)
                editingCategoryProgress = null
            }
        )
    }

    // Add Bill Dialog
    if (showAddBillDialog) {
        AddBillDialog(
            currencySymbol = uiState.currencySymbol,
            onDismiss = { showAddBillDialog = false },
            onAdd = { title, amount, dueDay, provider ->
                onAddBill(title, amount, dueDay, provider, "Utilities")
                showAddBillDialog = false
            }
        )
    }
}

@Composable
fun CategoryBudgetCard(
    progress: CategoryBudgetProgress,
    currencySymbol: String,
    onEdit: () -> Unit
) {
    val pct = progress.percentage
    val progressColor = when {
        pct >= 1.0f -> MaterialTheme.colorScheme.error
        pct >= 0.8f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = IconUtils.getIconByName(progress.iconName),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = progress.categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Spent ${IconUtils.formatPkr(progress.spent, currencySymbol)} of ${IconUtils.formatPkr(progress.limit, currencySymbol)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { pct.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (pct >= 1.0f) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Over budget by ${IconUtils.formatPkr(progress.spent - progress.limit, currencySymbol)}!",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RecurringBillItem(
    bill: RecurringBillEntity,
    currencySymbol: String,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (bill.isPaidThisMonth) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = bill.isPaidThisMonth,
                onCheckedChange = { onTogglePaid() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (bill.isPaidThisMonth) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${bill.provider} • Due on ${bill.dueDayOfMonth}th",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = IconUtils.formatPkr(bill.amount, currencySymbol),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (bill.isPaidThisMonth) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Bill",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditCategoryBudgetDialog(
    categoryName: String,
    currentLimit: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var limitText by remember { mutableStateOf(currentLimit.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Set Budget for $categoryName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it },
                    label = { Text("Monthly Limit ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val parsed = limitText.toDoubleOrNull() ?: currentLimit
                            onSave(parsed)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun AddBillDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, amount: Double, dueDay: Int, provider: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDayText by remember { mutableStateOf("10") }
    var provider by remember { mutableStateOf("Utility Dept") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Add Recurring Bill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bill Title (e.g. PTCL Broadband)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Approx Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dueDayText,
                        onValueChange = { dueDayText = it },
                        label = { Text("Due Day (1-31)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = provider,
                        onValueChange = { provider = it },
                        label = { Text("Provider") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val day = dueDayText.toIntOrNull() ?: 1
                            if (title.isNotBlank() && amt > 0) {
                                onAdd(title, amt, day, provider)
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
