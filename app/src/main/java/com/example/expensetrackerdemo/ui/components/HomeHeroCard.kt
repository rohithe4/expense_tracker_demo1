package com.example.expensetrackerdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeHeroCard(
    netBalance: Double,
    income: Double,
    expense: Double,
    onAddTransactionClick: () -> Unit,
    releaseInertialTrigger: Long = 0L,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x0F000000),
                ambientColor = Color(0x05000000)
            ),
        shape = RoundedCornerShape(24.dp),
        color = ColorSurface
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    // Soft ambient tint blobs
                    // Blue tint top-left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorHeroBlue.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = size.width * 0.7f
                        ),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.7f
                    )
                    // Peach tint bottom-right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ColorHeroPeach.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(size.width, size.height),
                            radius = size.width * 0.7f
                        ),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.7f
                    )
                }
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Top Content: Summary amount
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "NET INCOME",
                        style = HeroOverline
                    )
                    RollingCounter(
                        value = netBalance,
                        style = HeroMainAmount,
                        delayMillis = 0,
                        releaseInertialTrigger = releaseInertialTrigger
                    )
                }

                // Middle Content: Stat cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(
                        modifier = Modifier.weight(1f),
                        label = "INCOME",
                        amountValue = income,
                        icon = Icons.Default.ArrowUpward,
                        iconColor = ColorSuccess,
                        delayMillis = 60, // Tighter stagger
                        releaseInertialTrigger = releaseInertialTrigger
                    )
                    StatItem(
                        modifier = Modifier.weight(1f),
                        label = "EXPENSES",
                        amountValue = expense,
                        icon = Icons.Default.ArrowDownward,
                        iconColor = ColorError,
                        delayMillis = 120, // Tighter stagger
                        releaseInertialTrigger = releaseInertialTrigger
                    )
                }

                // Bottom Content: Integrated Action Button
                AppButton(
                    text = "Add transaction",
                    onClick = onAddTransactionClick,
                    style = AppButtonStyle.Secondary,
                    leadingIcon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Helper to provide a quick Figtree style variation if needed
@Composable
private fun FigtreeStyle(
    size: androidx.compose.ui.unit.TextUnit,
    weight: androidx.compose.ui.text.font.FontWeight,
    color: Color
) = androidx.compose.ui.text.TextStyle(
    fontFamily = Figtree,
    fontSize = size,
    fontWeight = weight,
    color = color
)

@Composable
private fun StatItem(
    label: String,
    amountValue: Double,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    releaseInertialTrigger: Long = 0L
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ColorSurface,
        shadowElevation = 0.5.dp // Super subtle inner elevation/shadow
    ) {
        Column(
            modifier = Modifier
                .background(ColorSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    style = HeroStatLabel
                )
            }
            RollingCounter(
                value = amountValue,
                style = HeroStatValue,
                delayMillis = delayMillis,
                releaseInertialTrigger = releaseInertialTrigger
            )
        }
    }
}
