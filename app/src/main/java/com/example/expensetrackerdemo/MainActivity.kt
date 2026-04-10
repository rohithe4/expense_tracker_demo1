package com.example.expensetrackerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.expensetrackerdemo.data.local.ExpenseDatabase
import com.example.expensetrackerdemo.data.repository.ExpenseRepository
import com.example.expensetrackerdemo.ui.navigation.ExpenseNavGraph
import com.example.expensetrackerdemo.ui.theme.ExpenseTrackerDemoTheme
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual DI
        val database = ExpenseDatabase.getDatabase(this)
        val repository = ExpenseRepository(database.expenseDao())
        val factory = ExpenseViewModel.Factory(repository)
        val viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        setContent {
            ExpenseTrackerDemoTheme {
                ExpenseNavGraph(
                    navController = androidx.navigation.compose.rememberNavController(),
                    viewModel = viewModel
                )
            }
        }
    }
}