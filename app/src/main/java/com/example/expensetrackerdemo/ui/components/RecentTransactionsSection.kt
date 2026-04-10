package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Global open transaction ID — only one row open at a time
    var openTransactionId by remember { mutableStateOf<Int?>(null) }
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
                            }
                        )
                    }
                }
            }
        }
    }
}


// ─── TransactionGroup ────────────────────────────────────────────────────────

@Composable
private fun TransactionGroup(
    label:                  String,
    items:                  List<TransactionUiModel>,
    openTransactionId:      Int?,
    onOpenTransaction:      (Int) -> Unit,
    onClearOpenTransaction: () -> Unit,
    onEdit:                 (Int) -> Unit,
    onDelete:               (TransactionUiModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text     = label,
            style    = ListGroupHeader,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Surface(
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(24.dp),
            color           = ColorSurface,
            shadowElevation = 0.5.dp
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    TransactionRow(
                        item    = item,
                        isOpen  = openTransactionId == item.id,
                        onOpen  = { onOpenTransaction(item.id) },
                        onClose = { if (openTransactionId == item.id) onClearOpenTransaction() },
                        onEdit  = { onEdit(item.id) },
                        onDelete = { onDelete(item) }
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            color     = ColorDivider.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier  = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── TransactionRow ──────────────────────────────────────────────────────────

@Composable
private fun TransactionRow(
    item:     TransactionUiModel,
    isOpen:   Boolean,
    onOpen:   () -> Unit,
    onClose:  () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    val scope   = rememberCoroutineScope()
    val density = LocalDensity.current

    // Total reveal: 2 icons (40dp each) + 12dp spacing + 16dp end padding = 108dp
    // This perfectly aligns the right edge of the icons with the end of the divider (16dp).
    val revealPx = with(density) { 108.dp.toPx() }

    val offsetX = remember { Animatable(0f) }

    // Sync with parent's "open one at a time" state
    LaunchedEffect(isOpen) {
        if (!isOpen && offsetX.value != 0f) {
            offsetX.animateTo(
                targetValue   = 0f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .background(ColorSurface),
        contentAlignment = Alignment.Center
    ) {
        // ── Action buttons layer (behind the row) ──────────────────────────
        val progress = if (revealPx > 0) abs(offsetX.value) / revealPx else 0f
        
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 16.dp), // Align with HorizontalDivider padding (16.dp)
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Edit Button
            SwipeActionIcon(
                modifier = Modifier.graphicsLayer { 
                    alpha = progress
                    translationX = (20 * (1f - progress)).dp.toPx()
                },
                icon    = Icons.Default.Edit,
                tint    = ColorPrimary,
                onClick = {
                    onEdit()
                    scope.launch {
                        offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                        onClose()
                    }
                }
            )

            // Delete Button
            SwipeActionIcon(
                modifier = Modifier.graphicsLayer { 
                    alpha = progress
                    translationX = (10 * (1f - progress)).dp.toPx()
                },
                icon    = Icons.Default.Delete,
                tint    = ColorError,
                onClick = onDelete
            )
        }

        // ── Foreground content (draggable) ─────────────────────────────────
        Surface(
            color    = ColorSurface,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                // Snap to open threshold (>30 % of reveal width)
                                if (offsetX.value < -(revealPx * 0.3f)) {
                                    offsetX.animateTo(
                                        targetValue   = -revealPx,
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                    onOpen()
                                } else {
                                    offsetX.animateTo(
                                        targetValue   = 0f,
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                    onClose()
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                                onClose()
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offsetX.value + dragAmount)
                                    .coerceIn(-revealPx, 0f)   // clamp: only left, only up to revealPx
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text     = item.name,
                        style    = ListMerchantName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text  = "${item.category} • ${item.timeFormatted}",
                        style = ListMetadata
                    )
                }
                Text(
                    text  = item.amountFormatted,
                    style = ListAmountBold.copy(color = ColorText)
                )
            }
        }
    }
}

// ─── SwipeActionIcon ─────────────────────────────────────────────────────────

@Composable
private fun SwipeActionIcon(
    icon:    ImageVector,
    tint:    Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .size(40.dp)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,   // no ripple flash
                onClick           = onClick
            ),
        shape  = RoundedCornerShape(12.dp),
        color  = ColorSurface2,
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = tint,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}
