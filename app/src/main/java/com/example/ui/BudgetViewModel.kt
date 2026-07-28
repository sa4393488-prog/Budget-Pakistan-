package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BudgetEntity
import com.example.data.BudgetRepository
import com.example.data.CategoryEntity
import com.example.data.RecurringBillEntity
import com.example.data.TransactionEntity
import com.example.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryBudgetProgress(
    val categoryName: String,
    val iconName: String,
    val spent: Double,
    val limit: Double,
    val percentage: Float
)

data class BudgetUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val bills: List<RecurringBillEntity> = emptyList(),
    val currencySymbol: String = "₨",
    val selectedFilterCategory: String? = null,
    val searchQuery: String = "",
    val isDataLoaded: Boolean = false
) {
    val filteredTransactions: List<TransactionEntity>
        get() {
            return transactions.filter { tx ->
                val matchesCategory = selectedFilterCategory == null || tx.categoryName == selectedFilterCategory
                val matchesSearch = searchQuery.isBlank() ||
                        tx.title.contains(searchQuery, ignoreCase = true) ||
                        tx.note.contains(searchQuery, ignoreCase = true) ||
                        tx.categoryName.contains(searchQuery, ignoreCase = true) ||
                        tx.paymentMethod.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }

    val totalIncome: Double
        get() = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

    val totalExpenses: Double
        get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val netBalance: Double
        get() = totalIncome - totalExpenses

    val categoryExpenseMap: Map<String, Double>
        get() = transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

    val budgetProgressList: List<CategoryBudgetProgress>
        get() {
            val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
            return expenseCategories.map { cat ->
                val spent = categoryExpenseMap[cat.name] ?: 0.0
                val budget = budgets.find { it.categoryName == cat.name }
                val limit = budget?.monthlyLimit ?: cat.defaultBudget
                val pct = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1.5f) else 0f
                CategoryBudgetProgress(
                    categoryName = cat.name,
                    iconName = cat.iconName,
                    spent = spent,
                    limit = limit,
                    percentage = pct
                )
            }
        }

    val totalBudgetLimit: Double
        get() = budgetProgressList.sumOf { it.limit }

    val totalBudgetSpent: Double
        get() = budgetProgressList.sumOf { it.spent }

    // Zakat Calculation (2.5% of net savings if above Nisab threshold ~150,000 PKR silver equivalent)
    val estimatedZakat: Double
        get() = if (netBalance > 150000.0) netBalance * 0.025 else 0.0
}

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BudgetRepository
    private val _currencySymbol = MutableStateFlow("₨")
    private val _selectedFilterCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<BudgetUiState>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BudgetRepository(db)

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        val currentMonthYear = BudgetRepository.getCurrentMonthYear()

        val flowData = combine(
            repository.allTransactions,
            repository.allCategories,
            repository.getBudgetsForMonth(currentMonthYear),
            repository.allBills
        ) { txs, cats, bdgts, bills ->
            Triple(txs, cats, bdgts) to bills
        }

        val flowState = combine(
            _currencySymbol,
            _selectedFilterCategory,
            _searchQuery
        ) { symbol, filterCat, query ->
            Triple(symbol, filterCat, query)
        }

        uiState = combine(flowData, flowState) { (data, bills), (symbol, filterCat, query) ->
            val (txs, cats, bdgts) = data
            BudgetUiState(
                transactions = txs,
                categories = cats,
                budgets = bdgts,
                bills = bills,
                currencySymbol = symbol,
                selectedFilterCategory = filterCat,
                searchQuery = query,
                isDataLoaded = true
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetUiState()
        )
    }

    fun setCurrencySymbol(symbol: String) {
        _currencySymbol.value = symbol
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterCategory(category: String?) {
        _selectedFilterCategory.value = category
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        categoryName: String,
        categoryIcon: String,
        note: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    type = type,
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    note = note,
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun setCategoryBudget(categoryName: String, monthlyLimit: Double) {
        viewModelScope.launch {
            val monthYear = BudgetRepository.getCurrentMonthYear()
            repository.setBudget(
                BudgetEntity(
                    categoryName = categoryName,
                    monthlyLimit = monthlyLimit,
                    monthYear = monthYear
                )
            )
        }
    }

    fun toggleBillPaid(bill: RecurringBillEntity) {
        viewModelScope.launch {
            repository.updateBill(bill.copy(isPaidThisMonth = !bill.isPaidThisMonth))
        }
    }

    fun addBill(title: String, amount: Double, dueDay: Int, provider: String, category: String) {
        viewModelScope.launch {
            repository.addBill(
                RecurringBillEntity(
                    title = title,
                    amount = amount,
                    dueDayOfMonth = dueDay,
                    provider = provider,
                    isPaidThisMonth = false,
                    category = category
                )
            )
        }
    }

    fun deleteBill(bill: RecurringBillEntity) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }

    fun resetAndPreFillSampleData() {
        viewModelScope.launch {
            repository.resetAndSeedSampleData()
        }
    }
}
