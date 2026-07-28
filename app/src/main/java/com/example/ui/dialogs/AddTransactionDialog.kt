package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.CategoryEntity
import com.example.data.TransactionType
import com.example.ui.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    categories: List<CategoryEntity>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onAddTransaction: (
        title: String,
        amount: Double,
        type: TransactionType,
        categoryName: String,
        categoryIcon: String,
        note: String,
        paymentMethod: String
    ) -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    val filteredCategories = categories.filter { it.type == type }
    var selectedCategory by remember(type, filteredCategories) {
        mutableStateOf(filteredCategories.firstOrNull()?.name ?: "General")
    }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val paymentMethods = listOf("Cash", "JazzCash", "EasyPaisa", "Bank Transfer", "Card")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_transaction_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Type Toggle (Expense vs Income)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { type = TransactionType.EXPENSE }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (type == TransactionType.EXPENSE) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expense",
                                color = if (type == TransactionType.EXPENSE) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (type == TransactionType.INCOME) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { type = TransactionType.INCOME }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (type == TransactionType.INCOME) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Income",
                                color = if (type == TransactionType.INCOME) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Description (e.g., Imtiaz Ration)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transaction_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        filteredCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = IconUtils.getIconByName(cat.iconName),
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(cat.name)
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat.name
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method selector chips
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(paymentMethods) { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && parsedAmount > 0) {
                            val catObj = filteredCategories.find { it.name == selectedCategory }
                            val iconName = catObj?.iconName ?: "Category"
                            onAddTransaction(
                                title,
                                parsedAmount,
                                type,
                                selectedCategory,
                                iconName,
                                note,
                                paymentMethod
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_transaction_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.EXPENSE) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Save Transaction", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
