package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateMillis DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}

@Dao
interface RecurringBillDao {
    @Query("SELECT * FROM recurring_bills ORDER BY dueDayOfMonth ASC")
    fun getAllBills(): Flow<List<RecurringBillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: RecurringBillEntity)

    @Update
    suspend fun updateBill(bill: RecurringBillEntity)

    @Delete
    suspend fun deleteBill(bill: RecurringBillEntity)
}
