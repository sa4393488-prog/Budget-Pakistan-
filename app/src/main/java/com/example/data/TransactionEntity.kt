package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val categoryIcon: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMethod: String = "Cash"
)
