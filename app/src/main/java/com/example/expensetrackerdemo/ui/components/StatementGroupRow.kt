package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.HistoryItem
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun StatementGroupRow(
    group: HistoryItem.StatementGroup,
    isOpen: Boolean,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    // Swipe to delete logic
    val revealPx = with(density) { 110.dp.toPx() }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(isOpen) {
        if (!isOpen && offsetX.value != 0f) {
            offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColorSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Action Layer (Edit and Delete)
            val progress = if (revealPx > 0) abs(offsetX.value) / revealPx else 0f
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SwipeActionIcon(
                    modifier = Modifier.graphicsLayer {
                        alpha = progress
                        translationX = (20 * (1f - progress)).dp.toPx()
                    },
                    icon = Icons.Default.Edit,
                    tint = ColorPrimaryLight,
                    onClick = onEdit
                )
                SwipeActionIcon(
                    modifier = Modifier.graphicsLayer {
                        alpha = progress
                        translationX = (10 * (1f - progress)).dp.toPx()
                    },
                    icon = Icons.Default.Delete,
                    tint = ColorError,
                    onClick = onDelete
                )
            }

            // Foreground Content
            Surface(
                color = ColorSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clickable(
                        onClick = onClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (offsetX.value < -(revealPx * 0.4f)) {
                                        offsetX.animateTo(-revealPx, spring(stiffness = Spring.StiffnessMedium))
                                        onOpen()
                                    } else {
                                        offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                                        onClose()
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                scope.launch {
                                    val newOffset = (offsetX.value + dragAmount).coerceIn(-revealPx, 0f)
                                    offsetX.snapTo(newOffset)
                                }
                            }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
                ) {
                    // Top Row: Title and Amount
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = group.name,
                            style = ListMerchantName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        val totalAmount = group.totalAmount
                        Text(
                            text = (if (totalAmount >= 0) "+" else "-") + currencyFormatter.format(abs(totalAmount)),
                            style = ListAmountBold
                        )
                    }

                    // Divider - matched to design
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = ColorDivider.copy(alpha = 0.8f)
                    )

                    // Bottom Row: Count Pill and Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Count Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ColorSurface2,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = "${group.count} Transactions",
                                style = ListMetadata,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        // Date
                        Text(
                            text = group.dateRange,
                            style = ListMetadata,
                            color = ColorTextMuted
                        )
                    }
                }
            }
        }
    }
}
