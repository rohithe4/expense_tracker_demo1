package com.example.expensetrackerdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.data.model.Transaction
import com.example.expensetrackerdemo.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TransactionSuccessType {
    NONE, CREATED, UPDATED
}

data class TransactionUiModel(
    val id: Int,
    val name: String,
    val category: String,
    val amountFormatted: String,
    val timeFormatted: String,
    val dateLabel: String,
    val isIncome: Boolean,
    val originalTransaction: com.example.expensetrackerdemo.data.model.Transaction
)

data class DashboardUiState(
    val netBalance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val isReady: Boolean = false
)

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _transactionSuccess = MutableStateFlow(TransactionSuccessType.NONE)
    val transactionSuccess = _transactionSuccess.asStateFlow()

    fun notifyTransactionCreated() {
        _transactionSuccess.value = TransactionSuccessType.CREATED
    }

    fun notifyTransactionUpdated() {
        _transactionSuccess.value = TransactionSuccessType.UPDATED
    }

    fun clearTransactionSuccess() {
        _transactionSuccess.value = TransactionSuccessType.NONE
    }

    // Eagerly: DB queries fire when ViewModel is created, not when UI subscribes.
    // This is the single biggest performance improvement — data is already in-flight
    // by the time the first Compose frame renders.
    // Consolidated Home Screen State
    val dashboardState: StateFlow<DashboardUiState> = combine(
        repository.getTotalIncome(),
        repository.getTotalExpense(),
        repository.getRecentTransactions(15)
    ) { income, expense, transactions ->
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val groupKeyFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        
        val now = System.currentTimeMillis()
        val today = groupKeyFormatter.format(Date(now))
        val yesterday = groupKeyFormatter.format(Date(now - 24 * 60 * 60 * 1000))

        val mappedTransactions = transactions.map { txn ->
            val date = Date(txn.date)
            val gKey = groupKeyFormatter.format(date)
            val dateLabel = when (gKey) {
                today -> "TODAY"
                yesterday -> "YESTERDAY"
                else -> dateFormatter.format(date).uppercase()
            }
            
            TransactionUiModel(
                id = txn.id,
                name = txn.name,
                category = txn.category,
                amountFormatted = (if (txn.type == 1) "+" else "-") + currencyFormatter.format(txn.amount),
                timeFormatted = timeFormatter.format(date),
                dateLabel = dateLabel,
                isIncome = txn.type == 1,
                originalTransaction = txn
            )
        }

        DashboardUiState(
            income = income ?: 0.0,
            expense = expense ?: 0.0,
            netBalance = (income ?: 0.0) - (expense ?: 0.0),
            recentTransactions = mappedTransactions,
            isReady = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DashboardUiState()
    )

    // Legacy fields kept for compatibility during migration, but ideally replaced by dashboardState
    val totalIncome = repository.getTotalIncome()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val totalExpense = repository.getTotalExpense()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val recentTransactions = repository.getTopTransactions()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Non-critical: still lazy, loaded after primary content is visible
    val allTemplates = repository.getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    suspend fun getTransactionById(id: Int): Transaction? = repository.getTransactionById(id)

    fun addTemplate(template: Template) {
        viewModelScope.launch {
            repository.insertTemplate(template)
        }
    }

    class Factory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExpenseViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
