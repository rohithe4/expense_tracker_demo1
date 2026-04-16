package com.example.expensetrackerdemo.data.repository

import com.example.expensetrackerdemo.data.local.ExpenseDao
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {

    fun getAllTemplates(): Flow<List<Template>> = dao.getAllTemplates()

    suspend fun insertTemplate(template: Template): Long {
        return dao.insertTemplate(template)
    }

    fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> = dao.getRecentTransactions(limit)

    // Above-the-fold fast path – only 5 rows, used for initial home screen render
    fun getTopTransactions(): Flow<List<Transaction>> = dao.getTopTransactions()

    suspend fun insertTransaction(transaction: Transaction): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(id: Int): Transaction? = dao.getTransactionById(id)

    fun getTransactionsByGroupId(groupId: String): Flow<List<Transaction>> = dao.getTransactionsByGroupId(groupId)

    suspend fun deleteTransactionsByGroupId(groupId: String) = dao.deleteTransactionsByGroupId(groupId)

    fun getTotalIncome(): Flow<Double?> = dao.getTotalIncome()

    fun getTotalExpense(): Flow<Double?> = dao.getTotalExpense()
}
