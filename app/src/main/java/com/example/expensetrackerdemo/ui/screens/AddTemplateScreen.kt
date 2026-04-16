package com.example.expensetrackerdemo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.ui.components.AppButton
import com.example.expensetrackerdemo.ui.components.AppButtonStyle
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel

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

    val categories = remember {
        listOf(
            "Food & Dining", "Transport", "Shopping", "Groceries", "Bills & Utilities",
            "Entertainment", "Health", "Education", "Salary", "Freelance",
            "Business", "Investment", "Travel", "Rent", "Gifts", "Personal Care", "Technology", "Other"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Template", style = BodyLg.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = ColorSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Transaction Type
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("TRANSACTION TYPE", style = MetaSm.copy(color = ColorTextMuted, letterSpacing = 0.12.em))
                TransactionTypeToggle(
                    isIncome = isIncome,
                    onSelectionChange = { isIncome = it }
                )
            }

            // 2. Details
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                DetailInput(
                    label = "Template Name",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "e.g., Apple Store"
                )

                DetailDropdown(
                    label = "Default Category",
                    value = category,
                    placeholder = "Select category",
                    options = categories,
                    onOptionSelected = { category = it }
                )

                DetailInput(
                    label = "Sample SMS Text (Optional)",
                    value = sampleText,
                    onValueChange = { sampleText = it },
                    placeholder = "Paste a sample SMS to help auto-detection",
                    isMultiline = true
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun TransactionTypeToggle(
    isIncome: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    // Reusing logic from AddTransactionScreen for consistency
    // In a real app, this should be a shared component
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ColorSurface2)
            .border(
                width = 1.dp,
                color = ColorDivider.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val width = maxWidth
        val indicatorX by androidx.compose.animation.core.animateDpAsState(
            targetValue = if (isIncome) 0.dp else width / 2,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 220, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        )
        val indicatorColor by animateColorAsState(
            targetValue = if (isIncome) ColorSuccess.copy(alpha = 0.15f) else ColorError.copy(alpha = 0.15f),
            animationSpec = androidx.compose.animation.core.tween<Color>(durationMillis = 220)
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorX)
                .fillMaxHeight()
                .width(width / 2)
                .padding(4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(indicatorColor)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            val incomeTextColor by animateColorAsState(
                targetValue = if (isIncome) ColorSuccess else ColorTextMuted,
                animationSpec = androidx.compose.animation.core.tween<Color>(durationMillis = 180)
            )
            val expenseTextColor by animateColorAsState(
                targetValue = if (!isIncome) ColorError else ColorTextMuted,
                animationSpec = androidx.compose.animation.core.tween<Color>(durationMillis = 180)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectionChange(true) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Income", style = BodyMd.copy(fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium, color = incomeTextColor))
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelectionChange(false) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("Expense", style = BodyMd.copy(fontWeight = if (!isIncome) FontWeight.Bold else FontWeight.Medium, color = expenseTextColor))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailDropdown(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), style = MetaSm.copy(color = ColorText))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, style = BodyLg.copy(color = ColorTextMuted.copy(alpha = 0.5f))) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(16.dp),
                textStyle = BodyLg.copy(color = ColorText, fontWeight = FontWeight.Medium),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorPrimary,
                    unfocusedBorderColor = ColorTextMuted.copy(alpha = 0.2f),
                    unfocusedContainerColor = ColorSurface,
                    focusedContainerColor = ColorSurface
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(ColorSurface)
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption, style = BodyLg) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isMultiline: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), style = MetaSm.copy(color = ColorText))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, style = BodyLg.copy(color = ColorTextMuted.copy(alpha = 0.5f))) },
            modifier = Modifier.fillMaxWidth().then(if (isMultiline) Modifier.height(110.dp) else Modifier),
            shape = RoundedCornerShape(16.dp),
            singleLine = !isMultiline,
            textStyle = BodyLg.copy(color = ColorText, fontWeight = FontWeight.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorPrimary,
                unfocusedBorderColor = ColorTextMuted.copy(alpha = 0.2f),
                unfocusedLabelColor = ColorText,
                focusedLabelColor = ColorPrimary,
                focusedContainerColor = ColorSurface,
                unfocusedContainerColor = ColorSurface
            )
        )
    }
}
