package com.example.expensetrackerdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val templateId: Int? = null,
    val name: String, // Merchant / Title
    val amount: Double,
    val type: Int, // 1 for Income, -1 for Expense
    val category: String,
    val source: String, // Account / Payment Source
    val date: Long, // User-selected date (Midnight timestamp)
    val note: String? = null,
    val reference: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
