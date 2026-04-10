package com.example.expensetrackerdemo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import com.example.expensetrackerdemo.ui.components.AppButton
import com.example.expensetrackerdemo.ui.components.AppButtonStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTemplateScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var sampleText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Template Name (e.g., Apple Store)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g., Technology)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sampleText,
                onValueChange = { sampleText = it },
                label = { Text("Sample SMS Text") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Type:")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = isIncome, onClick = { isIncome = true })
                Text("Income")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !isIncome, onClick = { isIncome = false })
                Text("Expense")
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = "Save Template",
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addTemplate(
                            Template(
                                name = name,
                                category = category.ifBlank { "General" },
                                sampleText = sampleText,
                                type = if (isIncome) 1 else -1
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Primary,
                enabled = name.isNotBlank()
            )
        }
    }
}
