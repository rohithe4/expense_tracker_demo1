package com.example.expensetrackerdemo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.ui.theme.*

enum class AppButtonStyle {
    Primary,
    Secondary,
    Tertiary
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val contentColor = when (style) {
        AppButtonStyle.Primary -> Color.White
        AppButtonStyle.Secondary -> ColorText
        AppButtonStyle.Tertiary -> ColorPrimary
    }

    val containerColor = when (style) {
        AppButtonStyle.Primary -> ColorButtonPrimary
        AppButtonStyle.Secondary -> ColorSurface
        AppButtonStyle.Tertiary -> Color.Transparent
    }

    val disabledContainerColor = when (style) {
        AppButtonStyle.Primary -> ColorButtonDisabled
        AppButtonStyle.Secondary -> ColorSurface
        AppButtonStyle.Tertiary -> Color.Transparent
    }

    val disabledContentColor = when (style) {
        AppButtonStyle.Primary -> ColorTextMuted.copy(alpha = 0.3f)
        AppButtonStyle.Secondary -> ColorTextMuted.copy(alpha = 0.3f)
        AppButtonStyle.Tertiary -> ColorTextMuted.copy(alpha = 0.3f)
    }

    val border = when (style) {
        AppButtonStyle.Secondary -> BorderStroke(1.dp, ColorButtonSecondaryBorder)
        else -> null
    }

    val height = when (style) {
        AppButtonStyle.Tertiary -> 32.dp
        else -> 48.dp
    }

    val fontSize = when (style) {
        AppButtonStyle.Tertiary -> 13.sp
        else -> 15.sp
    }

    val horizontalPadding = when (style) {
        AppButtonStyle.Tertiary -> 8.dp
        else -> 18.dp
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
        border = if (enabled) border else (if (style == AppButtonStyle.Secondary) BorderStroke(1.dp, ColorButtonSecondaryBorder.copy(alpha = 0.5f)) else null),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        elevation = null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = text,
                style = FigtreeStyle(
                    size = fontSize,
                    weight = FontWeight.SemiBold,
                    color = if (enabled) contentColor else disabledContentColor
                )
            )

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FigtreeStyle(
    size: androidx.compose.ui.unit.TextUnit,
    weight: FontWeight,
    color: Color
) = androidx.compose.ui.text.TextStyle(
    fontFamily = Figtree,
    fontSize = size,
    fontWeight = weight,
    color = color
)
