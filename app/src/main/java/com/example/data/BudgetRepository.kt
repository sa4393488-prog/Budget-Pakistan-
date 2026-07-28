package com.example.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class BudgetRepository(private val db: AppDatabase) {

    val allTransactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
    val allBills: Flow<List<RecurringBillEntity>> = db.recurringBillDao().getAllBills()

    fun getBudgetsForMonth(monthYear: String): Flow<List<BudgetEntity>> =
        db.budgetDao().getBudgetsForMonth(monthYear)

    suspend fun addTransaction(transaction: TransactionEntity) {
        db.transactionDao().insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        db.transactionDao().deleteById(id)
    }

    suspend fun setBudget(budget: BudgetEntity) {
        db.budgetDao().insertOrUpdateBudget(budget)
    }

    suspend fun addBill(bill: RecurringBillEntity) {
        db.recurringBillDao().insertBill(bill)
    }

    suspend fun updateBill(bill: RecurringBillEntity) {
        db.recurringBillDao().updateBill(bill)
    }

    suspend fun deleteBill(bill: RecurringBillEntity) {
        db.recurringBillDao().deleteBill(bill)
    }

    suspend fun checkAndSeedInitialData() {
        val categoryCount = db.categoryDao().getCategoryCount()
        if (categoryCount == 0) {
            seedDefaultCategories()
            seedDefaultBudgets()
            seedSampleTransactions()
            seedSampleBills()
        }
    }

    suspend fun resetAndSeedSampleData() {
        db.transactionDao().deleteAll()
        db.budgetDao().deleteAll()
        seedSampleTransactions()
        seedDefaultBudgets()
        seedSampleBills()
    }

    private suspend fun seedDefaultCategories() {
        val defaultCategories = listOf(
            CategoryEntity(name = "Groceries & Ration", iconName = "ShoppingCart", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 45000.0),
            CategoryEntity(name = "Utilities (KE/Gas/Net)", iconName = "FlashOn", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 25000.0),
            CategoryEntity(name = "House Rent & Maintenance", iconName = "Home", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 55000.0),
            CategoryEntity(name = "Fuel & Transport", iconName = "LocalGasStation", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 20000.0),
            CategoryEntity(name = "Dining & Foodpanda", iconName = "Restaurant", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 15000.0),
            CategoryEntity(name = "Education & School Fees", iconName = "School", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 30000.0),
            CategoryEntity(name = "Zakat & Sadaqah", iconName = "VolunteerActivism", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 10000.0),
            CategoryEntity(name = "Committee (BC) & Savings", iconName = "AccountBalance", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 25000.0),
            CategoryEntity(name = "Health & Medicines", iconName = "LocalHospital", type = TransactionType.EXPENSE, isDefault = true, defaultBudget = 10000.0),
            CategoryEntity(name = "Monthly Salary", iconName = "AttachMoney", type = TransactionType.INCOME, isDefault = true),
            CategoryEntity(name = "Freelance & Remittance", iconName = "Work", type = TransactionType.INCOME, isDefault = true),
            CategoryEntity(name = "Business / Profit", iconName = "TrendingUp", type = TransactionType.INCOME, isDefault = true)
        )
        db.categoryDao().insertCategories(defaultCategories)
    }

    private suspend fun seedDefaultBudgets() {
        val currentMonthYear = getCurrentMonthYear()
        val defaultBudgets = listOf(
            BudgetEntity(categoryName = "Groceries & Ration", monthlyLimit = 45000.0, monthYear = currentMonthYear),
            BudgetEntity(categoryName = "Utilities (KE/Gas/Net)", monthlyLimit = 25000.0, monthYear = currentMonthYear),
            BudgetEntity(categoryName = "House Rent & Maintenance", monthlyLimit = 55000.0, monthYear = currentMonthYear),
            BudgetEntity(categoryName = "Fuel & Transport", monthlyLimit = 20000.0, monthYear = currentMonthYear),
            BudgetEntity(categoryName = "Dining & Foodpanda", monthlyLimit = 15000.0, monthYear = currentMonthYear),
            BudgetEntity(categoryName = "Zakat & Sadaqah", monthlyLimit = 10000.0, monthYear = currentMonthYear)
        )
        defaultBudgets.forEach { db.budgetDao().insertOrUpdateBudget(it) }
    }

    private suspend fun seedSampleTransactions() {
        val now = System.currentTimeMillis()
        val dayMillis = 86400000L

        val samples = listOf(
            TransactionEntity(
                title = "Monthly Salary Credit",
                amount = 220000.0,
                type = TransactionType.INCOME,
                categoryName = "Monthly Salary",
                categoryIcon = "AttachMoney",
                dateMillis = now - (2 * dayMillis),
                note = "Received via Meezan Bank",
                paymentMethod = "Bank Transfer"
            ),
            TransactionEntity(
                title = "Monthly House Rent",
                amount = 55000.0,
                type = TransactionType.EXPENSE,
                categoryName = "House Rent & Maintenance",
                categoryIcon = "Home",
                dateMillis = now - (2 * dayMillis),
                note = "Paid to landlord",
                paymentMethod = "Bank Transfer"
            ),
            TransactionEntity(
                title = "Imtiaz Super Market Grocery",
                amount = 28450.0,
                type = TransactionType.EXPENSE,
                categoryName = "Groceries & Ration",
                categoryIcon = "ShoppingCart",
                dateMillis = now - (1 * dayMillis),
                note = "Monthly ration & household items",
                paymentMethod = "Debit Card"
            ),
            TransactionEntity(
                title = "K-Electric Electricity Bill",
                amount = 18600.0,
                type = TransactionType.EXPENSE,
                categoryName = "Utilities (KE/Gas/Net)",
                categoryIcon = "FlashOn",
                dateMillis = now - (1 * dayMillis),
                note = "Peak summer bill via EasyPaisa",
                paymentMethod = "EasyPaisa"
            ),
            TransactionEntity(
                title = "Fuel Petrol Refill",
                amount = 6500.0,
                type = TransactionType.EXPENSE,
                categoryName = "Fuel & Transport",
                categoryIcon = "LocalGasStation",
                dateMillis = now,
                note = "Full tank PSO hi-octane",
                paymentMethod = "Cash"
            ),
            TransactionEntity(
                title = "Foodpanda Dinner Order",
                amount = 2450.0,
                type = TransactionType.EXPENSE,
                categoryName = "Dining & Foodpanda",
                categoryIcon = "Restaurant",
                dateMillis = now,
                note = "Biryani & Kebabs with family",
                paymentMethod = "JazzCash"
            ),
            TransactionEntity(
                title = "Friday Sadaqah Donation",
                amount = 2000.0,
                type = TransactionType.EXPENSE,
                categoryName = "Zakat & Sadaqah",
                categoryIcon = "VolunteerActivism",
                dateMillis = now,
                note = "Local mosque welfare fund",
                paymentMethod = "Cash"
            )
        )

        samples.forEach { db.transactionDao().insertTransaction(it) }
    }

    private suspend fun seedSampleBills() {
        val bills = listOf(
            RecurringBillEntity(title = "K-Electric / LESCO Electric", amount = 18500.0, dueDayOfMonth = 10, provider = "Electricity Company", isPaidThisMonth = true),
            RecurringBillEntity(title = "SSGC / SNGPL Gas Bill", amount = 2800.0, dueDayOfMonth = 15, provider = "Gas Dept", isPaidThisMonth = false),
            RecurringBillEntity(title = "StormFiber Fiber Net", amount = 3500.0, dueDayOfMonth = 5, provider = "StormFiber", isPaidThisMonth = true),
            RecurringBillEntity(title = "Monthly Committee (BC)", amount = 25000.0, dueDayOfMonth = 1, provider = "BC Group", isPaidThisMonth = true)
        )
        bills.forEach { db.recurringBillDao().insertBill(it) }
    }

    companion object {
        fun getCurrentMonthYear(): String {
            val cal = Calendar.getInstance()
            val month = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val year = cal.get(Calendar.YEAR)
            return "$month-$year"
        }
    }
}
