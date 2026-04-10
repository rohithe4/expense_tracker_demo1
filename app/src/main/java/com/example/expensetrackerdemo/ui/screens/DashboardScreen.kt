package com.example.expensetrackerdemo.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.expensetrackerdemo.ui.components.HomeHeroCardSkeleton
import com.example.expensetrackerdemo.ui.components.RecentTransactionsSkeleton
import com.example.expensetrackerdemo.ui.components.HomeHeroCard
import com.example.expensetrackerdemo.ui.components.RecentTransactionsSection
import com.example.expensetrackerdemo.ui.theme.*
import com.example.expensetrackerdemo.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Int) -> Unit,
    onNavigateToAddTemplate: () -> Unit
) {
    val uiState by viewModel.dashboardState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val rawPullOffset = remember { mutableFloatStateOf(0f) }
    var releaseInertialTrigger by remember { mutableLongStateOf(0L) }
    val pullThresholdPx = with(LocalDensity.current) { 80.dp.toPx() }
    
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
                    // Tension multiplier for rubber-banding
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
                            // Trigger inertial jump as soon as card is almost home to remove the "settle" delay
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
    // Max visual offset restricted slightly so it doesn't leave the screen entirely
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
            // Helper label securely behind the translating screen
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
                // Top Section (Main Focus)
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
                            releaseInertialTrigger = releaseInertialTrigger
                        )
                    } else {
                        HomeHeroCardSkeleton()
                    }
                }
            }

            // Bottom Section (Recent Transactions)
            // Optimization: Parallel reveal. Both hero and transactions now reveal instantly as data arrives.
            AnimatedContent(
                targetState = uiState.isReady,
                transitionSpec = {
                    androidx.compose.animation.fadeIn(animationSpec = tween(200)) togetherWith 
                    androidx.compose.animation.fadeOut(animationSpec = tween(200))
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
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onUndoDelete = { viewModel.addTransaction(it) },
                        snackbarHostState = snackbarHostState
                    )
                } else {
                    RecentTransactionsSkeleton()
                }
            }
            
            // Bottom Spacing
            Spacer(modifier = Modifier.height(40.dp).navigationBarsPadding())
        }
    }
}
}
