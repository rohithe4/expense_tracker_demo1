package com.example.expensetrackerdemo.ui.screens

import android.app.DatePickerDialog
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.data.model.Transaction
import com.example.expensetrackerdemo.ui.components.AppButton
import com.example.expensetrackerdemo.ui.components.AppButtonStyle
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: ExpenseViewModel,
    transactionId: Int = -1,
    onNavigateBack: () -> Unit
) {
    val isEditMode = transactionId != -1
    val templates by viewModel.allTemplates.collectAsState()
    val context = LocalContext.current
    
    // Form State
    var isIncome by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTemplate by remember { mutableStateOf<Template?>(null) }
    var originalTransaction by remember { mutableStateOf<Transaction?>(null) }

    // Load transaction if in edit mode
    LaunchedEffect(transactionId) {
        if (isEditMode) {
            viewModel.getTransactionById(transactionId)?.let { txn ->
                originalTransaction = txn
                isIncome = txn.type == 1
                amount = txn.amount.toString()
                name = txn.name
                category = txn.category
                source = txn.source
                note = txn.note ?: ""
                reference = txn.reference ?: ""
                selectedDate = txn.date
            }
        }
    }
    
    // Categories and Accounts moved to constants or defined inside build if needed, 
    // but better to keep them consistent.
    val categories = remember {
        listOf(
            "Food & Dining", "Transport", "Shopping", "Groceries", "Bills & Utilities",
            "Entertainment", "Health", "Education", "Salary", "Freelance", 
            "Business", "Investment", "Travel", "Rent", "Gifts", "Personal Care", "Technology", "Other"
        )
    }
    val accounts = remember {
        listOf(
            "Cash", "Bank Account", "Credit Card", "Debit Card", "UPI", 
            "Wallet", "Salary Account", "Savings Account", "Other"
        )
    }

    // UI State
    var showTemplates by remember { mutableStateOf(false) }
    var showAmountSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Validation
    val isFormValid = remember(amount, name, category, source) {
        amount.isNotBlank() && name.isNotBlank() && category.isNotBlank() && source.isNotBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Transaction" else "Log Transaction", style = BodyLg.copy(fontWeight = FontWeight.Bold)) },
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
                        text = if (isEditMode) "Update Transaction" else "Save Transaction",
                        onClick = {
                            val parsedAmount = amount.toDoubleOrNull()
                            if (parsedAmount != null && parsedAmount > 0 && name.isNotBlank()) {
                                if (isEditMode && originalTransaction != null) {
                                    viewModel.updateTransaction(
                                        originalTransaction!!.copy(
                                            name = name,
                                            amount = parsedAmount,
                                            type = if (isIncome) 1 else -1,
                                            category = category,
                                            source = source,
                                            date = selectedDate,
                                            note = note,
                                            reference = reference
                                        )
                                    )
                                } else {
                                    viewModel.addTransaction(
                                        Transaction(
                                            templateId = selectedTemplate?.id,
                                            name = name,
                                            amount = parsedAmount,
                                            type = if (isIncome) 1 else -1,
                                            category = category,
                                            source = source,
                                            date = selectedDate,
                                            note = note,
                                            reference = reference
                                        )
                                    )
                                }
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        style = AppButtonStyle.Primary,
                        enabled = isFormValid
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // 1. Transaction Type (Sliding Segmented Control)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TRANSACTION TYPE", style = MetaSm.copy(color = ColorText, letterSpacing = 0.12.sp))
                TransactionTypeToggle(
                    isIncome = isIncome,
                    onSelectionChange = { isIncome = it }
                )
            }

            // 2. Amount Input (Trigger for Bottom Sheet)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AMOUNT", style = MetaSm.copy(color = ColorText, letterSpacing = 0.12.sp))
                Surface(
                    onClick = { showAmountSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ColorSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTextMuted.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("₹", style = TitleLg.copy(fontSize = 24.sp, color = ColorTextMuted.copy(alpha = 0.6f)))
                        Text(
                            text = if (amount.isEmpty()) "0.00" else amount,
                            style = TitleLg.copy(
                                fontSize = 32.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = if (amount.isEmpty()) ColorTextMuted.copy(alpha = 0.4f) else ColorText
                            )
                        )
                    }
                }
            }

            if (showAmountSheet) {
                AmountEntrySheet(
                    initialAmount = amount,
                    onDismiss = { showAmountSheet = false },
                    onConfirm = { 
                        amount = it
                        showAmountSheet = false 
                    },
                    sheetState = sheetState
                )
            }

            /*
            // 3. Template Selection (Outlined helpers)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("TEMPLATE (AUTO-FILL)", style = MetaSm.copy(color = ColorText))
                Surface(
                    onClick = { showTemplates = !showTemplates },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = ColorSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColorTextMuted.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTemplate?.name ?: "Tap to select template", 
                            style = BodyLg.copy(color = if (selectedTemplate != null) ColorText else ColorTextMuted)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ColorText)
                    }
                }
                if (showTemplates && templates.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = ColorSurface,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
                    ) {
                        Column {
                            templates.forEach { template ->
                                ListItem(
                                    headlineContent = { Text(template.name, style = BodyLg.copy(fontWeight = FontWeight.Medium)) },
                                    modifier = Modifier.clickable {
                                        selectedTemplate = template
                                        name = template.name
                                        category = template.category
                                        isIncome = template.type == 1
                                        showTemplates = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            */

            // 4. Details Group (Merchant, Category, Source, Smart Date)
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                DetailInput(label = "Title / Merchant", value = name, onValueChange = { name = it }, placeholder = "Apple Store")
                
                DetailDropdown(
                    label = "Category", 
                    value = category, 
                    placeholder = "Select category", 
                    options = categories, 
                    onOptionSelected = { category = it }
                )
                
                DetailDropdown(
                    label = "Account / Source", 
                    value = source, 
                    placeholder = "Select account", 
                    options = accounts, 
                    onOptionSelected = { source = it }
                )
                
                // Smart Date Field
                SmartDateField(
                    label = "Date",
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            // 5. Notes & Reference
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                DetailInput(label = "Notes", value = note, onValueChange = { note = it }, placeholder = "Optional description", isMultiline = true)
                DetailInput(label = "Reference ID", value = reference, onValueChange = { reference = it }, placeholder = "Reference / Receipt #")
            }
            
            // Padding for sticky bottom 
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun TransactionTypeToggle(
    isIncome: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    val incomeInteractionSource = remember { MutableInteractionSource() }
    val expenseInteractionSource = remember { MutableInteractionSource() }
    
    val isIncomeFocused by incomeInteractionSource.collectIsFocusedAsState()
    val isExpenseFocused by expenseInteractionSource.collectIsFocusedAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ColorSurface2)
            .border(
                width = 1.dp,
                color = if (isIncomeFocused || isExpenseFocused) ColorPrimary else ColorDivider.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val width = maxWidth
        val indicatorX by animateDpAsState(
            targetValue = if (isIncome) 0.dp else width / 2,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        )
        val indicatorColor by animateColorAsState(
            targetValue = if (isIncome) ColorSuccess.copy(alpha = 0.15f) else ColorError.copy(alpha = 0.15f),
            animationSpec = tween(durationMillis = 220)
        )

        // 1. Sliding Indicator Pill
        Box(
            modifier = Modifier
                .offset(x = indicatorX)
                .fillMaxHeight()
                .width(width / 2)
                .padding(4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(indicatorColor)
        )

        // 2. Clickable Labels (Indication = null to remove touch ripple)
        Row(modifier = Modifier.fillMaxSize()) {
            val incomeTextColor by animateColorAsState(
                targetValue = if (isIncome) ColorSuccess else ColorTextMuted,
                animationSpec = tween(durationMillis = 180)
            )
            val expenseTextColor by animateColorAsState(
                targetValue = if (!isIncome) ColorError else ColorTextMuted,
                animationSpec = tween(durationMillis = 180)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = incomeInteractionSource,
                        indication = null,
                        onClick = { onSelectionChange(true) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    style = BodyMd.copy(
                        fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                        color = incomeTextColor
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = expenseInteractionSource,
                        indication = null,
                        onClick = { onSelectionChange(false) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expense",
                    style = BodyMd.copy(
                        fontWeight = if (!isIncome) FontWeight.Bold else FontWeight.Medium,
                        color = expenseTextColor
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartDateField(
    label: String,
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    
    // Quick options logic
    val todayStart = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis }
    val yesterdayStart = remember { Calendar.getInstance().apply { add(Calendar.DATE, -1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis }

    val dateFormatterFormatted = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    
    val dateLabel = when {
        selectedDate >= todayStart && selectedDate < todayStart + 86400000 -> "Today"
        selectedDate >= yesterdayStart && selectedDate < yesterdayStart + 86400000 -> "Yesterday"
        else -> dateFormatterFormatted.format(Date(selectedDate))
    }

    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), style = MetaSm.copy(color = ColorText))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = dateLabel,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select date", style = BodyLg.copy(color = ColorTextMuted)) },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                        IconButton(onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val c = Calendar.getInstance()
                                    c.set(year, month, day)
                                    onDateSelected(c.timeInMillis)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Open calendar", tint = ColorPrimary, modifier = Modifier.size(20.dp))
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ColorText)
                    }
                },
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
                DropdownMenuItem(
                    text = { Text("Today", style = BodyLg) },
                    onClick = {
                        onDateSelected(System.currentTimeMillis())
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Yesterday", style = BodyLg) },
                    onClick = {
                        onDateSelected(yesterdayStart)
                        expanded = false
                    }
                )
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
                placeholder = { Text(placeholder, style = BodyLg.copy(color = ColorTextMuted)) },
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
            placeholder = { Text(placeholder, style = BodyLg.copy(color = ColorTextMuted)) },
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmountEntrySheet(
    initialAmount: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    sheetState: SheetState
) {
    var amount by remember { mutableStateOf(initialAmount) }
    var isAdding by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = ColorSurface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Enter the amount",
                style = BodyMd.copy(color = ColorText, fontWeight = FontWeight.Medium)
            )

            // Large Amount Display (Rolling Counter)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("₹", style = TitleLg.copy(fontSize = 32.sp, color = ColorTextMuted))
                Spacer(Modifier.width(8.dp))
                RollingAmountDisplay(
                    amount = if (amount.isEmpty()) "0.00" else amount,
                    isAdding = isAdding,
                    isPlaceholder = amount.isEmpty()
                )
            }

            // Custom Keypad
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf(".", "0", "DEL")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { key ->
                            KeypadButton(
                                text = key,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    when (key) {
                                        "DEL" -> if (amount.isNotEmpty()) {
                                            isAdding = false
                                            amount = amount.dropLast(1)
                                        }
                                        "." -> {
                                            isAdding = true
                                            if (amount.isEmpty()) amount = "0."
                                            else if (!amount.contains(".")) amount += "."
                                        }
                                        else -> {
                                            isAdding = true
                                            if (amount == "0") {
                                                amount = key
                                            } else {
                                                val parts = amount.split(".")
                                                if (parts.size < 2 || parts[1].length < 2) {
                                                    if (amount.length < 10) { // Safety cap
                                                        amount += key
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            AppButton(
                text = "Confirm Amount",
                onClick = { onConfirm(amount) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0,
                style = AppButtonStyle.Primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RollingAmountDisplay(
    amount: String,
    isAdding: Boolean,
    isPlaceholder: Boolean
) {
    val displayAmount = if (amount.isEmpty()) "0.00" else amount
    
    Row(
        modifier = Modifier.clipToBounds(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        // By always keeping 12 slots present and initialized to null, we ensure 
        // that new digits transition from 'null' to 'char', causing them to 
        // ROLL UP with the exact same animation as the very first digit.
        val totalSlots = 12
        for (i in 0 until totalSlots) {
            val char = displayAmount.getOrNull(i)
            DigitSlot(
                char = char,
                isAdding = isAdding,
                isPlaceholder = isPlaceholder,
                isInitialDigit = amount.length <= 1,
                key = i
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DigitSlot(
    char: Char?,
    isAdding: Boolean,
    isPlaceholder: Boolean,
    isInitialDigit: Boolean,
    key: Int
) {
    val duration = 220

    AnimatedContent(
        targetState = char,
        contentKey = { "${it}_$key" },
        transitionSpec = {
            if (isAdding) {
                // Scale Up + Jump Up + Fade In (Bottom-Center anchor)
                (scaleIn(
                    animationSpec = keyframes {
                        durationMillis = duration
                        0.82f at 0 with FastOutSlowInEasing
                        1.06f at 140 with FastOutSlowInEasing
                        1.0f at duration
                    },
                    transformOrigin = TransformOrigin(0.5f, 1f)
                ) + slideInVertically(
                    animationSpec = keyframes {
                        durationMillis = duration
                        IntOffset(0, 32) at 0 with FastOutSlowInEasing
                        IntOffset(0, -6) at 140 with FastOutSlowInEasing
                        IntOffset(0, 0) at duration
                    }
                ) { 0 } + fadeIn(tween(duration, easing = FastOutSlowInEasing))).togetherWith(
                    scaleOut(
                        animationSpec = tween(duration, easing = FastOutSlowInEasing),
                        targetScale = 0.5f,
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    ) + fadeOut(tween(duration, easing = FastOutSlowInEasing))
                ).using(SizeTransform(clip = false))
            } else {
                // Scale Down + Fade Out (Center anchor)
                (scaleIn(
                    animationSpec = tween(duration, easing = FastOutSlowInEasing),
                    initialScale = 1.2f,
                    transformOrigin = TransformOrigin(0.5f, 1f)
                ) + fadeIn(tween(duration, easing = FastOutSlowInEasing))).togetherWith(
                    scaleOut(
                        animationSpec = tween(duration, easing = FastOutSlowInEasing),
                        targetScale = 0.5f,
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    ) + fadeOut(tween(duration, easing = FastOutSlowInEasing))
                ).using(SizeTransform(clip = false))
            }
        },
        label = "DigitRoll_$key"
    ) { targetChar ->
        if (targetChar != null) {
            Text(
                text = targetChar.toString(),
                style = TitleLg.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaceholder) ColorTextMuted.copy(alpha = 0.4f) else ColorText,
                    fontFeatureSettings = "tnum"
                ),
                modifier = Modifier.widthIn(min = 22.dp)
            )
        } else {
            // Placeholder for inactive slots to keep them in the tree for entry/exit animations.
            // Using a Height prevents the row from collapsing.
            Box(Modifier.height(48.dp).width(0.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = ColorSurface2
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (text) {
                "DEL" -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete",
                    modifier = Modifier.size(22.dp),
                    tint = ColorText
                )
                else -> Text(
                    text = text,
                    style = BodyLg.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorText
                    )
                )
            }
        }
    }
}
