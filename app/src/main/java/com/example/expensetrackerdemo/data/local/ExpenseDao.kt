package com.example.expensetrackerdemo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM templates")
    fun getAllTemplates(): Flow<List<Template>>

    @Insert
    suspend fun insertTemplate(template: Template): Long

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    // Optimized: above-the-fold only – 5 rows for first visible render
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC LIMIT 5")
    fun getTopTransactions(): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions WHERE statementGroupId = :groupId ORDER BY date DESC")
    fun getTransactionsByGroupId(groupId: String): Flow<List<Transaction>>

    @Query("DELETE FROM transactions WHERE statementGroupId = :groupId")
    suspend fun deleteTransactionsByGroupId(groupId: String)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 1")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = -1")
    fun getTotalExpense(): Flow<Double?>
}
