package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.expensetrackerdemo.data.model.Template
import com.example.expensetrackerdemo.data.model.Transaction
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.TransactionUiModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionUiModel>,
    templates: List<Template>,
    onViewAllClick: () -> Unit,
    onEditTransaction: (Int) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onUndoDelete: (Transaction) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    hintId: Int? = null
) {
    val scope = rememberCoroutineScope()

    // Daily net calculation logic — compute once per transaction list change
    val dailyNets = remember(transactions) {
        transactions.groupBy { it.dateLabel }.mapValues { (_, items) ->
            items.sumOf { it.originalTransaction.amount * it.originalTransaction.type }
        }
    }
    // Global open transaction ID — only one row open at a time
    var openTransactionId by remember { mutableStateOf<Int?>(null) }
    
    // Pagination state per group (visible items)
    val visibleCounts = remember { mutableStateMapOf<String, Int>() }

    // UI only handles grouping for layout.
    val groups = remember(transactions) { transactions.groupBy { it.dateLabel } }
    val sortedLabels = remember(transactions) { transactions.map { it.dateLabel }.distinct() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Recent Transactions", style = ListSectionHeader)
            AppButton(
                text = "VIEW ALL",
                onClick = onViewAllClick,
                style = AppButtonStyle.Tertiary
            )
        }

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions yet.", style = BodyMd, color = ColorTextMuted)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                sortedLabels.forEach { label ->
                    groups[label]?.let { items ->
                        TransactionGroup(
                            label               = label,
                            items               = items,
                            netTotal            = dailyNets[label] ?: 0.0,
                            visibleCount        = visibleCounts[label] ?: 5,
                            onViewMore          = { 
                                val current = visibleCounts[label] ?: 5
                                visibleCounts[label] = current + 5
                            },
                            onCollapse          = {
                                visibleCounts[label] = 5
                            },
                            openTransactionId   = openTransactionId,
                            onOpenTransaction   = { id -> openTransactionId = id },
                            onClearOpenTransaction = { openTransactionId = null },
                            onEdit              = onEditTransaction,
                            onDelete            = { item ->
                                val txn = item.originalTransaction
                                onDeleteTransaction(txn)
                                openTransactionId = null
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message     = "Transaction deleted",
                                        actionLabel = "Undo",
                                        duration    = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onUndoDelete(txn)
                                    }
                                }
                            },
                            hintId = hintId
                        )
                    }
                }
            }
        }
    }
}


// Components moved to TransactionListItems.kt

