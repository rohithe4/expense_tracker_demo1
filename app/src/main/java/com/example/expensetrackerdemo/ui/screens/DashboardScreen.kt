package com.example.expensetrackerdemo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import com.example.expensetrackerdemo.data.model.Transaction
import com.example.expensetrackerdemo.ui.components.*
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import com.example.expensetrackerdemo.ui.viewmodel.TransactionSuccessType
import com.example.expensetrackerdemo.ui.viewmodel.TransactionUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext
import com.example.expensetrackerdemo.util.PdfImportHelper
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Int) -> Unit,
    onNavigateToAddTemplate: () -> Unit
) {
    val uiState by viewModel.dashboardState.collectAsState()
    val transactionSuccess by viewModel.transactionSuccess.collectAsState()

    // Success Toast Auto-Dismiss
    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess != TransactionSuccessType.NONE) {
            delay(3000)
            viewModel.clearTransactionSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val rawPullOffset = remember { mutableFloatStateOf(0f) }
    var releaseInertialTrigger by remember { mutableLongStateOf(0L) }
    val pullThresholdPx = with(LocalDensity.current) { 80.dp.toPx() }
    
    val context = LocalContext.current
    var showImportPreview by remember { mutableStateOf(false) }
    var importedTransactions by remember { mutableStateOf<List<com.example.expensetrackerdemo.data.model.Transaction>>(emptyList()) }
    var isImporting by remember { mutableStateOf(false) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { 
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isImporting = true
                coroutineScope.launch {
                    val parsed = PdfImportHelper.parsePdf(context, it)
                    isImporting = false
                    if (parsed.isNotEmpty()) {
                        importedTransactions = parsed as List<com.example.expensetrackerdemo.data.model.Transaction>
                        showImportPreview = true
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Could not extract transactions from PDF"
                        )
                    }
                }
            }
        }
    )
    
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0 && rawPullOffset.floatValue > 0) {
                    val previouslyPulled = rawPullOffset.floatValue
                    val consumed = available.y.coerceAtLeast(-previouslyPulled)
                    rawPullOffset.floatValue = (previouslyPulled + consumed).coerceAtLeast(0f)
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && scrollState.value == 0) {
                    val pullMultiplier = 0.4f 
                    rawPullOffset.floatValue += available.y * pullMultiplier
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (rawPullOffset.floatValue > 0) {
                    val wasOverThreshold = rawPullOffset.floatValue > pullThresholdPx
                    coroutineScope.launch {
                        var hasTriggeredJump = false
                        Animatable(rawPullOffset.floatValue).animateTo(
                            targetValue = 0f,
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f)
                        ) {
                            rawPullOffset.floatValue = value
                            if (wasOverThreshold && value < 0.5f && !hasTriggeredJump) {
                                releaseInertialTrigger = System.currentTimeMillis()
                                hasTriggeredJump = true
                            }
                        }
                    }
                    return Velocity(0f, available.y)
                }
                return Velocity.Zero
            }
        }
    }

    val isOverThreshold = rawPullOffset.floatValue > pullThresholdPx
    val cardTranslationY = rawPullOffset.floatValue.coerceAtMost(pullThresholdPx * 1.5f)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) { data ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFF1E1E1E),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .wrapContentSize()
                            .widthIn(min = 200.dp, max = 340.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .defaultMinSize(minHeight = 40.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = data.visuals.message,
                                style = BodySm.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            data.visuals.actionLabel?.let { actionLabel ->
                                TextButton(
                                    onClick = { data.performAction() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = ColorPrimaryLight),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .height(28.dp)
                                        .wrapContentWidth()
                                ) {
                                    Text(
                                        text = actionLabel.uppercase(),
                                        style = ButtonSm.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
                .nestedScroll(nestedScrollConnection)
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = rawPullOffset.floatValue > 10f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (isOverThreshold) "RELEASE TO REFRESH".uppercase() else "PULL TO REFRESH".uppercase(),
                    style = HeroOverline.copy(
                        color = ColorTextMuted, 
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = cardTranslationY
                    }
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
                ) {
                    AnimatedContent(
                        targetState = uiState.isReady,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith 
                            fadeOut(animationSpec = tween(200))
                        },
                        label = "hero_reveal"
                    ) { ready ->
                        if (ready) {
                            HomeHeroCard(
                                netBalance = uiState.netBalance,
                                income = uiState.income,
                                expense = uiState.expense,
                                onAddTransactionClick = onNavigateToAddTransaction,
                                onImportClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                                releaseInertialTrigger = releaseInertialTrigger
                            )
                        } else {
                            HomeHeroCardSkeleton()
                        }
                    }
                }

                AnimatedContent(
                    targetState = uiState.isReady,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith 
                        fadeOut(animationSpec = tween(200))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    label = "transactions_reveal"
                ) { ready ->
                    if (ready) {
                        RecentTransactionsSection(
                            transactions = uiState.recentTransactions,
                            templates = emptyList(), 
                            onViewAllClick = { /* Navigate to All Transactions */ },
                            onEditTransaction = onNavigateToEditTransaction,
                            onDeleteTransaction = { transaction: Transaction -> viewModel.deleteTransaction(transaction) },
                            onUndoDelete = { transaction: Transaction -> viewModel.addTransaction(transaction) },
                            snackbarHostState = snackbarHostState
                        )
                    } else {
                        RecentTransactionsSkeleton()
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp).navigationBarsPadding())
            }

            TransactionSuccessToast(
                type = transactionSuccess,
                onDismiss = { viewModel.clearTransactionSuccess() }
            )

            if (isImporting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = false) {}, // absorb clicks
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ColorSurface,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = ColorPrimaryLight, modifier = Modifier.size(24.dp))
                            Text(text = "Parsing statement...", style = BodyLg)
                        }
                    }
                }
            }

            if (showImportPreview) {
                AlertDialog(
                    onDismissRequest = { showImportPreview = false },
                    title = { Text("Review Import", style = BodyLg.copy(fontWeight = FontWeight.Bold)) },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Found ${importedTransactions.size} transactions.",
                                style = BodySm.copy(color = ColorTextMuted)
                            )
                            importedTransactions.forEach { txn ->
                                val isIncome = txn.type == 1
                                val color = if (isIncome) ColorSuccess else ColorText
                                val amountPrefix = if (isIncome) "+" else "-"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = txn.name, style = BodySm.copy(fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box {
                                            var expanded by remember { mutableStateOf(false) }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable { expanded = true }
                                            ) {
                                                Text(text = txn.category, style = MetaSm.copy(color = ColorPrimaryLight))
                                                Icon(
                                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                                    contentDescription = "Edit Category",
                                                    tint = ColorPrimaryLight,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(ColorSurface2)
                                            ) {
                                                val categories = listOf(
                                                    "Food", "Travel/Transport", "Groceries", "Shopping", 
                                                    "Bills & Utilities", "Entertainment/Subs", "Health", 
                                                    "Home/Rent", "Loans/EMI", "Salary", "Personal", "Donations", "Other"
                                                )
                                                categories.forEach { cat ->
                                                    DropdownMenuItem(
                                                        text = { Text(cat, style = BodySm, color = ColorText) },
                                                        onClick = {
                                                            val newList = importedTransactions.toMutableList()
                                                            val index = newList.indexOf(txn)
                                                            if (index != -1) {
                                                                newList[index] = txn.copy(category = cat)
                                                                importedTransactions = newList
                                                            }
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Text(
                                        text = "$amountPrefix${txn.amount}",
                                        style = BodySm.copy(color = color, fontWeight = FontWeight.Bold)
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            importedTransactions.forEach { viewModel.addTransaction(it) }
                            showImportPreview = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Imported ${importedTransactions.size} transactions")
                            }
                        }) {
                            Text("Save All", style = ButtonSm, color = ColorPrimaryLight)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showImportPreview = false }) {
                            Text("Cancel", style = ButtonSm, color = ColorTextMuted)
                        }
                    },
                    containerColor = ColorSurface,
                    titleContentColor = ColorText,
                    textContentColor = ColorText
                )
            }
        }
    }
}
