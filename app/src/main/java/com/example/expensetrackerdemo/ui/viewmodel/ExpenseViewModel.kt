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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.asStateFlow

enum class TransactionSuccessType {
    NONE, CREATED, UPDATED
}

enum class HistoryTab {
    ALL, TODAY, GROUP
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

sealed class HistoryItem {
    data class StatementGroup(
        val groupId: String,
        val name: String,
        val count: Int,
        val totalAmount: Double,
        val dateRange: String,
        val latestDate: Long
    ) : HistoryItem()

    data class Transaction(
        val uiModel: TransactionUiModel
    ) : HistoryItem()

    data class DateGroup(
        val dateLabel: String,
        val items: List<TransactionUiModel>,
        val netTotal: Double
    ) : HistoryItem()
}

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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _historyTab = MutableStateFlow(HistoryTab.ALL)
    val historyTab = _historyTab.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setHistoryTab(tab: HistoryTab) {
        _historyTab.value = tab
    }

    // Consolidated Home Screen State
    val dashboardState: StateFlow<DashboardUiState> = combine(
        repository.getTotalIncome(),
        repository.getTotalExpense(),
        repository.getRecentTransactions(50)
    ) { income, expense, transactions ->
        DashboardUiState(
            income = income ?: 0.0,
            expense = expense ?: 0.0,
            netBalance = (income ?: 0.0) - (expense ?: 0.0),
            recentTransactions = mapToUiModels(transactions),
            isReady = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DashboardUiState()
    )

    // Full history state with filtering and grouping
    val historyItems: StateFlow<List<HistoryItem>> = combine(
        repository.getAllTransactions(),
        _searchQuery,
        _historyTab
    ) { transactions, query, tab ->
        val result = mutableListOf<HistoryItem>()
        
        // Group by statementGroupId
        val groupedByStatement = transactions.groupBy { it.statementGroupId }
        val individualTransactions = mutableListOf<Transaction>()
        
        groupedByStatement.forEach { (groupId, txns) ->
            if (groupId != null) {
                // It's a statement group
                val first = txns.first()
                val groupName = first.statementGroupName ?: "Imported Statement"

                // If searching, only include group if it matches OR its transactions match
                val matchesQuery = query.isEmpty() ||
                    groupName.contains(query, ignoreCase = true) ||
                    txns.any { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }

                if (matchesQuery && tab != HistoryTab.TODAY) {
                    val total = txns.sumOf { it.amount * it.type }
                    val minDate = txns.minOf { it.date }
                    val maxDate = txns.maxOf { it.date }
                    val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                    val dateRange = if (SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(minDate)) == SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(maxDate))) {
                        dateFormatter.format(Date(minDate))
                    } else {
                        "${dateFormatter.format(Date(minDate))} - ${dateFormatter.format(Date(maxDate))}"
                    }

                    result.add(HistoryItem.StatementGroup(
                        groupId = groupId,
                        name = groupName,
                        count = txns.size,
                        totalAmount = total,
                        dateRange = dateRange,
                        latestDate = maxDate
                    ))
                }
            } else {
                // These are individual transactions (not part of any statement)
                txns.forEach { txn ->
                    val matchesQuery = query.isEmpty() ||
                        txn.name.contains(query, ignoreCase = true) ||
                        txn.category.contains(query, ignoreCase = true)

                    if (matchesQuery && tab != HistoryTab.GROUP) {
                        if (tab == HistoryTab.TODAY) {
                            val groupKeyFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                            val today = groupKeyFormatter.format(Date())
                            if (groupKeyFormatter.format(Date(txn.date)) == today) {
                                individualTransactions.add(txn)
                            }
                        } else {
                            individualTransactions.add(txn)
                        }
                    }
                }
            }
        }

        // Group individual transactions by date
        val individualByDate = individualTransactions.groupBy { mapSingleToUiModel(it).dateLabel }
        
        individualByDate.forEach { (label, txns) ->
            val uiModels = txns.map { mapSingleToUiModel(it) }
            val total = uiModels.sumOf { it.originalTransaction.amount * it.originalTransaction.type }
            result.add(HistoryItem.DateGroup(
                dateLabel = label,
                items = uiModels.sortedByDescending { it.originalTransaction.date },
                netTotal = total
            ))
        }

        result.sortedByDescending { 
            when (it) {
                is HistoryItem.StatementGroup -> it.latestDate
                is HistoryItem.DateGroup -> it.items.first().originalTransaction.date
                is HistoryItem.Transaction -> it.uiModel.originalTransaction.date
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun mapSingleToUiModel(txn: Transaction): TransactionUiModel {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val groupKeyFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        
        val now = System.currentTimeMillis()
        val today = groupKeyFormatter.format(Date(now))
        val yesterday = groupKeyFormatter.format(Date(now - 24 * 60 * 60 * 1000))

        val date = Date(txn.date)
        val gKey = groupKeyFormatter.format(date)
        val dateLabel = when (gKey) {
            today -> "TODAY"
            yesterday -> "YESTERDAY"
            else -> dateFormatter.format(date).uppercase()
        }
        
        return TransactionUiModel(
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

    fun getTransactionsByGroupId(groupId: String) = repository.getTransactionsByGroupId(groupId)
        .combine(_searchQuery) { transactions, query ->
            val mapped = mapToUiModels(transactions)
            if (query.isEmpty()) mapped
            else mapped.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        }

    fun deleteStatementGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteTransactionsByGroupId(groupId)
        }
    }

    fun updateStatementGroupName(groupId: String, newName: String) {
        viewModelScope.launch {
            repository.getTransactionsByGroupId(groupId).firstOrNull()?.let { transactions ->
                transactions.forEach { txn ->
                    repository.updateTransaction(txn.copy(statementGroupName = newName))
                }
            }
        }
    }

    private fun mapToUiModels(transactions: List<Transaction>): List<TransactionUiModel> {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val groupKeyFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        
        val now = System.currentTimeMillis()
        val today = groupKeyFormatter.format(Date(now))
        val yesterday = groupKeyFormatter.format(Date(now - 24 * 60 * 60 * 1000))

        return transactions.map { txn ->
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
    }

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
