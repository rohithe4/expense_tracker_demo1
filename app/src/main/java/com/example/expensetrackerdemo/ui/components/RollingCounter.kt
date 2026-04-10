package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import java.util.Currency
import kotlin.math.roundToInt

@Composable
fun RollingCounter(
    value: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    prefix: String = "₹",
    delayMillis: Int = 0,
    durationMillis: Int = 400,
    releaseInertialTrigger: Long = 0L
) {
    val formattedValue = remember(value) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            currency = Currency.getInstance("INR")
            val hasDecimal = value % 1.0 != 0.0
            minimumFractionDigits = if (hasDecimal) 2 else 0
            maximumFractionDigits = 2
        }
        formatter.format(value).replace("₹", "").trim()
    }
    
    // Persistent tracking to avoid re-animating on back navigation if nothing changed
    var lastAnimatedValue by rememberSaveable { mutableStateOf<Double?>(null) }
    
    // Check if this is the very first time we are animating (initial load)
    val isInitialLoad = lastAnimatedValue == null
    
    // Faster, consistent duration for all actions: Snappy but premium
    val currentDuration = 400
    
    // We only want to animate in specific cases:
    // 1. Initial launch/load (lastAnimatedValue is null)
    // 2. Value has changed (Add/Edit/Delete occurred)
    val shouldAnimate = remember(value) {
        val changed = lastAnimatedValue != null && lastAnimatedValue != value
        val initial = lastAnimatedValue == null
        initial || changed
    }

    // Update the tracker after composition
    SideEffect {
        lastAnimatedValue = value
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        // Prefix (e.g., ₹)
        Text(
            text = prefix,
            style = style,
            modifier = Modifier.padding(end = 2.dp)
        )
        
    // Filter out signs/commas to get true digit indices for staggering
    var numericDigitIndex = 0
    val totalDigits = formattedValue.count { it.isDigit() }
    
    formattedValue.forEach { char ->
        if (char.isDigit()) {
            DigitReel(
                targetDigit = char.toString().toInt(),
                style = style,
                baseDelayMillis = delayMillis,
                durationMillis = currentDuration,
                index = numericDigitIndex,
                trigger = value,
                shouldAnimate = shouldAnimate,
                isInitialLoad = isInitialLoad,
                releaseInertialTrigger = releaseInertialTrigger
            )
            numericDigitIndex++
        } else if (char != ' ') { // Avoid empty space reels, just render as text
            Text(
                text = char.toString(),
                style = style
            )
        }
    }
}
}

@Composable
private fun DigitReel(
    targetDigit: Int,
    style: TextStyle,
    baseDelayMillis: Int,
    durationMillis: Int,
    index: Int,
    trigger: Double,
    shouldAnimate: Boolean,
    isInitialLoad: Boolean,
    releaseInertialTrigger: Long,
    modifier: Modifier = Modifier
) {
    // 3 physical cycles in the reel 
    val cycles = 3 
    val targetValue = ((cycles - 1) * 10 + targetDigit).toFloat()
    
    // Fix: If we just navigated back with a new value, Animatable(targetValue) would prevent animation.
    // We initialize it to a "previous" position (either 0 or one cycle back) if we know we should animate.
    val scrollOffset = remember { 
        Animatable(
            if (shouldAnimate) {
                if (isInitialLoad) 0f else targetValue - 10f 
            } else {
                targetValue
            }
        )
    }
    
    // Snappier stagger for smooth L-to-R flow
    val staggerDelay = index * 10L
    
    LaunchedEffect(trigger, shouldAnimate) {
        if (shouldAnimate) {
            // Only reset to zero on true initial load (first time app opens / state is null)
            if (isInitialLoad) {
                scrollOffset.snapTo(0f)
            }

            delay(baseDelayMillis + staggerDelay)
            
            scrollOffset.animateTo(
                targetValue = targetValue,
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = CubicBezierEasing(0.33f, 1.0f, 0.68f, 1.0f) // Snappier exit
                )
            )
        } else {
            // Immediate snap to target if no animation is requested (e.g., back navigation with same value)
            scrollOffset.snapTo(targetValue)
        }
    }

    // Inertial Jump: Triggers when the card snaps back after pull, ONLY if value didn't change
    var lastJumpTrigger by remember { mutableStateOf(0L) }
    val inertialOffset = remember { Animatable(0f) }
    
    // Random variations for a "natural" unorganized feel
    val randomJitter = remember { (0..40).random() }
    val velocityVariation = remember { (-30..30).random().toFloat() }
    
    LaunchedEffect(releaseInertialTrigger) {
        if (releaseInertialTrigger > 0L && releaseInertialTrigger != lastJumpTrigger) {
            lastJumpTrigger = releaseInertialTrigger
            
            // Only fire the inertial jump if the numbers are not already performing a full rollout
            if (!scrollOffset.isRunning) {
                // Wave-like stagger + random jitter to make it feel unorganized/natural
                delay(index * 20L + randomJitter)
                
                inertialOffset.animateTo(
                    targetValue = 0f,
                    initialVelocity = -160f + velocityVariation, 
                    animationSpec = spring(
                        dampingRatio = 0.6f, 
                        stiffness = 400f + (velocityVariation * 2) // Vary stiffness based on velocity
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .height(with(androidx.compose.ui.platform.LocalDensity.current) { style.fontSize.toDp() * 1.3f })
            .offset(y = inertialOffset.value.dp)
            .clipToBounds()
    ) {
        DigitLayout(
            scrollOffset = scrollOffset.value,
            style = style
        ) {
            repeat(cycles) {
                for (i in 0..9) {
                    Text(
                        text = i.toString(),
                        style = style,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DigitLayout(
    scrollOffset: Float,
    style: TextStyle,
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        
        // Assume all digits have same height (tnum ensures same width usually)
        val digitHeight = placeables.firstOrNull()?.height ?: 0
        val digitWidth = placeables.firstOrNull()?.width ?: 0
        
        layout(digitWidth, digitHeight) {
            placeables.forEachIndexed { index, placeable ->
                val yOffset = (index - scrollOffset) * digitHeight
                placeable.placeRelative(0, yOffset.roundToInt())
            }
        }
    }
}
