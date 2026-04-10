package com.example.expensetrackerdemo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String = "General",
    val sampleText: String,
    val type: Int // 1 for Credit (Income), -1 for Debit (Expense)
)
