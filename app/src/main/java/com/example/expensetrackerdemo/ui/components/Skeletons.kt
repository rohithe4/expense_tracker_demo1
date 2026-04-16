package com.example.expensetrackerdemo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetrackerdemo.ui.theme.ColorBg
import com.example.expensetrackerdemo.ui.theme.ColorSurface
import com.example.expensetrackerdemo.ui.theme.ColorSurface2

@Composable
fun ShimmerItem(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(ColorSurface2.copy(alpha = alpha.value))
    )
}

@Composable
fun HomeHeroCardSkeleton(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(24.dp),
        color = ColorSurface
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerItem(modifier = Modifier.width(80.dp).height(12.dp))
                ShimmerItem(modifier = Modifier.width(180.dp).height(32.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ColorBg,
                        modifier = Modifier.height(84.dp).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ShimmerItem(modifier = Modifier.width(60.dp).height(10.dp))
                            ShimmerItem(modifier = Modifier.width(80.dp).height(16.dp))
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ColorBg,
                        modifier = Modifier.height(84.dp).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ShimmerItem(modifier = Modifier.width(60.dp).height(10.dp))
                            ShimmerItem(modifier = Modifier.width(80.dp).height(16.dp))
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                ShimmerItem(modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(16.dp))
                ShimmerItem(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(16.dp))
            }
        }
    }
}

@Composable
fun RecentTransactionsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ShimmerItem(modifier = Modifier.width(140.dp).height(20.dp))
            ShimmerItem(modifier = Modifier.width(60.dp).height(20.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = ColorSurface
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                repeat(4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ShimmerItem(modifier = Modifier.width(120.dp).height(14.dp))
                            ShimmerItem(modifier = Modifier.width(80.dp).height(10.dp))
                        }
                        ShimmerItem(modifier = Modifier.width(60.dp).height(18.dp))
                    }
                }
            }
        }
    }
}
