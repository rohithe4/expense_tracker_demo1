package com.example.expensetrackerdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.expensetrackerdemo.data.model.Transaction
import com.example.expensetrackerdemo.ui.components.*
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import com.example.expensetrackerdemo.ui.viewmodel.TransactionSuccessType
import com.example.expensetrackerdemo.ui.viewmodel.TransactionUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@Composable
fun GroupDetailScreen(
    viewModel: ExpenseViewModel,
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditTransaction: (Int) -> Unit
) {
    val transactionsState by viewModel.getTransactionsByGroupId(groupId).collectAsState(initial = null)
    val transactions = transactionsState ?: emptyList()
    var openTransactionId by remember { mutableStateOf<Int?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Receipt Overlay State
    var selectedTransactionForReceipt by remember { mutableStateOf<Transaction?>(null) }
    
    val transactionSuccess by viewModel.transactionSuccess.collectAsState()

    LaunchedEffect(transactionSuccess) {
        if (transactionSuccess != TransactionSuccessType.NONE) {
            delay(3000)
            viewModel.clearTransactionSuccess()
        }
    }

    // Auto-navigate back if the group becomes empty (only after initial load)
    LaunchedEffect(transactionsState) {
        if (transactionsState != null && transactionsState!!.isEmpty() && groupId.isNotEmpty()) {
            onNavigateBack()
        }
    }

    val groupName = transactions.firstOrNull()?.originalTransaction?.statementGroupName ?: "Imported Statement"
    val totalAmount = transactions.sumOf { it.originalTransaction.amount * it.originalTransaction.type }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(ColorBg),
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = ColorBg,
                modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ColorText)
                    }
                    Text(
                        text = groupName,
                        style = BodyLg.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider(color = ColorDivider.copy(alpha = 0.5f), thickness = 0.5.dp)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(ColorBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Summary Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = ColorSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f)),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "TOTAL STATEMENT BALANCE",
                        style = MetaSm.copy(color = ColorTextMuted, letterSpacing = 0.12.em),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (if (totalAmount >= 0) "₹" else "-₹") + currencyFormatter.format(Math.abs(totalAmount)).replace("₹", ""),
                        style = DisplayMd.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (totalAmount >= 0) ColorSuccess else ColorText
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Stats Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorSurface2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${transactions.size} Transactions",
                                style = MetaSm.copy(fontWeight = FontWeight.SemiBold),
                                color = ColorText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ColorSurface2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "Imported",
                                style = MetaSm.copy(fontWeight = FontWeight.SemiBold),
                                color = ColorTextMuted,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Group transactions into "DATE" headers just like main lists
            val groupedByDate = transactions.groupBy { 
                val txn = it.originalTransaction
                val now = System.currentTimeMillis()
                val today = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(now))
                val yesterday = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(now - 86400000L))
                val dateKey = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(txn.date))
                
                when (dateKey) {
                    today -> "TODAY"
                    yesterday -> "YESTERDAY"
                    else -> java.text.SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(txn.date)).uppercase()
                }
            }
            
            val sortedLabels = transactions.map { 
                val txn = it.originalTransaction
                val now = System.currentTimeMillis()
                val today = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(now))
                val yesterday = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(now - 86400000L))
                val dateKey = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(txn.date))
                
                when (dateKey) {
                    today -> "TODAY"
                    yesterday -> "YESTERDAY"
                    else -> java.text.SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(txn.date)).uppercase()
                }
            }.distinct()

            sortedLabels.forEach { label ->
                val items = groupedByDate[label] ?: emptyList()
                val dailyNet = items.sumOf { it.originalTransaction.amount * it.originalTransaction.type }
                
                // We'll reuse the TransactionGroup component but with no footer
                // Mapping back to UiModel locally for consistency
                val mappedItems = items.map { uiModel ->
                    uiModel.copy(dateLabel = label)
                }

                TransactionGroup(
                    label = label,
                    items = mappedItems,
                    netTotal = dailyNet,
                    visibleCount = mappedItems.size,
                    showFooter = false,
                    onViewMore = {},
                    onCollapse = {},
                    openTransactionId = openTransactionId,
                    onOpenTransaction = { openTransactionId = it },
                    onClearOpenTransaction = { openTransactionId = null },
                    onEdit = { transactionId ->
                        scope.launch {
                            viewModel.getTransactionById(transactionId)?.let {
                                selectedTransactionForReceipt = it
                            }
                        }
                    },
                    onDelete = { item ->
                        val txn = item.originalTransaction
                        viewModel.deleteTransaction(txn)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Transaction deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addTransaction(txn)
                            }
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp).navigationBarsPadding())
        }
        
        TransactionSuccessToast(
            type = transactionSuccess,
            onDismiss = { viewModel.clearTransactionSuccess() }
        )

        // Receipt Overlay
        selectedTransactionForReceipt?.let { txn ->
            TransactionReceiptOverlay(
                transaction = txn,
                onDismiss = { selectedTransactionForReceipt = null },
                onDelete = { 
                    viewModel.deleteTransaction(txn)
                    selectedTransactionForReceipt = null
                },
                onEdit = { id ->
                    selectedTransactionForReceipt = null
                    onNavigateToEditTransaction(id)
                }
            )
        }
    }
}
