package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_bills")
data class RecurringBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDayOfMonth: Int,
    val provider: String,
    val isPaidThisMonth: Boolean = false,
    val category: String = "Utilities"
)
