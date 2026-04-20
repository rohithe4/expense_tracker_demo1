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
import android.content.Context
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Int) -> Unit,
    onNavigateToAddTemplate: () -> Unit,
    onNavigateToHistory: () -> Unit
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
    val context = LocalContext.current
    
    // Swipe Tour State
    val sharedPref = remember { context.getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE) }
    var showSwipeTour by remember { mutableStateOf(false) }
    var hintTransactionId by remember { mutableStateOf<Int?>(null) }
    var tourTriggeredByCreation by remember { mutableStateOf(false) }

    // Track when a transaction was created to trigger tour after snackbar dismisses
    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess == TransactionSuccessType.CREATED) {
            tourTriggeredByCreation = true
        } else if (transactionSuccess == TransactionSuccessType.NONE && tourTriggeredByCreation) {
            tourTriggeredByCreation = false
            // Snackbar just finished its 3s display
            if (uiState.recentTransactions.size == 1) {
                val hasSeenTour = sharedPref.getBoolean("has_seen_swipe_tour", false)
                if (!hasSeenTour) {
                    delay(2500) // Wait 2.5 seconds after snackbar dismisses
                    showSwipeTour = true
                    sharedPref.edit().putBoolean("has_seen_swipe_tour", true).apply()
                }
            }
        }
    }

    // Safety check: if app restarts and first txn exists but tour never shown
    LaunchedEffect(uiState.recentTransactions.isNotEmpty(), uiState.isReady) {
        if (uiState.isReady && uiState.recentTransactions.isNotEmpty() && transactionSuccess == TransactionSuccessType.NONE) {
            val hasSeenTour = sharedPref.getBoolean("has_seen_swipe_tour", false)
            if (!hasSeenTour && !tourTriggeredByCreation) {
                // Not showing snackbar currently, can show tour after a short delay
                delay(1000)
                showSwipeTour = true
                sharedPref.edit().putBoolean("has_seen_swipe_tour", true).apply()
            }
        }
    }

    // Trigger hint after tour is closed
    LaunchedEffect(showSwipeTour) {
        if (!showSwipeTour && uiState.recentTransactions.isNotEmpty()) {
            val hasSeenHint = sharedPref.getBoolean("has_seen_swipe_hint", false)
            if (!hasSeenHint) {
                val hasSeenTour = sharedPref.getBoolean("has_seen_swipe_tour", false)
                // Only show hint if tour was shown OR if we missed it
                if (hasSeenTour) {
                    hintTransactionId = uiState.recentTransactions.firstOrNull()?.id
                    sharedPref.edit().putBoolean("has_seen_swipe_hint", true).apply()
                }
            }
        }
    }

    val rawPullOffset = remember { mutableFloatStateOf(0f) }
    var releaseInertialTrigger by remember { mutableLongStateOf(0L) }
    val pullThresholdPx = with(LocalDensity.current) { 80.dp.toPx() }
    
    var showImportPreview by remember { mutableStateOf(false) }
    var importedTransactions by remember { mutableStateOf<List<com.example.expensetrackerdemo.data.model.Transaction>>(emptyList()) }
    var isImporting by remember { mutableStateOf(false) }

    var showGroupNamePrompt by remember { mutableStateOf(false) }
    var tempImportedTransactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    
    // Receipt Overlay State
    var selectedTransactionForReceipt by remember { mutableStateOf<Transaction?>(null) }

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
                        tempImportedTransactions = parsed as List<Transaction>
                        showGroupNamePrompt = true
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
        snackbarHost = { AppSnackbarHost(snackbarHostState) }
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
                                onImportClick = { 
                                    pdfPickerLauncher.launch(
                                        arrayOf(
                                            "application/pdf", 
                                            "text/csv", 
                                            "text/comma-separated-values",
                                            "application/vnd.ms-excel",
                                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                        )
                                    ) 
                                },
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
                            onViewAllClick = { viewModel.setSearchQuery(""); onNavigateToHistory() },
                            onEditTransaction = { transactionId ->
                                coroutineScope.launch {
                                    viewModel.getTransactionById(transactionId)?.let {
                                        selectedTransactionForReceipt = it
                                    }
                                }
                            },
                            onDeleteTransaction = { transaction: Transaction -> viewModel.deleteTransaction(transaction) },
                            onUndoDelete = { transaction: Transaction -> viewModel.addTransaction(transaction) },
                            snackbarHostState = snackbarHostState,
                            hintId = hintTransactionId
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

            if (showGroupNamePrompt) {
                var groupName by remember { mutableStateOf("New Statement") }
                AppDialog(
                    onDismissRequest = { showGroupNamePrompt = false },
                    title = "Statement Name",
                    content = {
                        Column {
                            Text("Give this group of transactions a name:", style = BodySm, color = ColorTextMuted)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                label = { Text("Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ColorPrimary,
                                    cursorColor = ColorPrimary,
                                    focusedLabelColor = ColorPrimary,
                                    unfocusedBorderColor = ColorDivider,
                                    unfocusedLabelColor = ColorTextMuted
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        AppButton(
                            text = "Next",
                            onClick = {
                                val groupId = UUID.randomUUID().toString()
                                importedTransactions = tempImportedTransactions.map { 
                                    it.copy(statementGroupId = groupId, statementGroupName = groupName) 
                                }
                                showGroupNamePrompt = false
                                showImportPreview = true
                            },
                            style = AppButtonStyle.Primary
                        )
                    },
                    dismissButton = {
                        AppButton(
                            text = "Cancel",
                            onClick = { showGroupNamePrompt = false },
                            style = AppButtonStyle.Tertiary
                        )
                    }
                )
            }

            if (isImporting) {
                AppLoadingDialog(message = "Parsing statement...")
            }

            if (showImportPreview) {
                ImportPreviewDialog(
                    importedTransactions = importedTransactions,
                    onUpdateTransactions = { 
                        importedTransactions = it 
                    },
                    onDismiss = { showImportPreview = false },
                    onSave = {
                        importedTransactions.forEach { viewModel.addTransaction(it) }
                        showImportPreview = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Imported ${importedTransactions.size} transactions")
                        }
                    }
                )
            }

            if (showSwipeTour) {
                SwipeTourModal(onDismiss = { showSwipeTour = false })
            }

            // Receipt Overlay
            selectedTransactionForReceipt?.let { txn ->
                TransactionReceiptOverlay(
                    transaction = txn,
                    onDismiss = { selectedTransactionForReceipt = null },
                    onDelete = { 
                        viewModel.deleteTransaction(txn)
                        selectedTransactionForReceipt = null
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Transaction deleted",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addTransaction(txn)
                            }
                        }
                    },
                    onEdit = { id ->
                        selectedTransactionForReceipt = null
                        onNavigateToEditTransaction(id)
                    }
                )
            }
        }
    }
}
