package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.TransactionSuccessType
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TransactionSuccessToast(
    type: TransactionSuccessType,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    if (type == TransactionSuccessType.NONE) return

    val subtitle = when (type) {
        TransactionSuccessType.CREATED -> "Your transaction was successfully created."
        TransactionSuccessType.UPDATED -> "Your transaction was successfully updated."
        else -> ""
    }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    // Physicality: Use Animatable for gesture-following
    val offsetY = remember { Animatable(0f) }
    
    // Motion Spec: Small scale entry and slide
    AnimatedVisibility(
        visible = type != TransactionSuccessType.NONE,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.96f, animationSpec = tween(220)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.96f, animationSpec = tween(180))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            // Only allow downward drag
                            if (delta > 0 || offsetY.value > 0) {
                                scope.launch {
                                    offsetY.snapTo(offsetY.value + delta)
                                }
                            }
                        },
                        onDragStopped = { velocity ->
                            if (offsetY.value > 100f || velocity > 500f) {
                                // Dismiss if dragged enough or fast enough
                                scope.launch {
                                    offsetY.animateTo(
                                        targetValue = 500f,
                                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                    onDismiss()
                                }
                            } else {
                                // Spring back to rest
                                scope.launch {
                                    offsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = 0.8f,
                                            stiffness = 500f
                                        )
                                    )
                                }
                            }
                        }
                    ),
                shape = RoundedCornerShape(24.dp),
                color = ColorSurface,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ColorSuccess.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ColorSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Text Content
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Success",
                            style = BodyLg.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ColorText
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtitle,
                            style = BodyMd.copy(
                                color = ColorTextMuted,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        )
                    }

                    // Close Icon
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = ColorTextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
