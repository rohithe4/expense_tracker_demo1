package com.example.expensetrackerdemo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.expensetrackerdemo.R

// Set up Figtree Variable FontFamily properly
@OptIn(ExperimentalTextApi::class)
val Figtree = FontFamily(
    Font(
        resId = R.font.figtree,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        resId = R.font.figtree,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        resId = R.font.figtree,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        resId = R.font.figtree,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        resId = R.font.figtree,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    )
)

// Tabular numbers setting for amount fields
const val TabularNumFeatureSettings = "tnum"

// Semantic Typography Tokens

// Display (Balances - SemiBold)
val DisplayLg = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 32.sp,
    lineHeight = 33.6.sp,
    letterSpacing = (-0.03).em,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = Color.Black
)

val DisplayMd = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 28.sp,
    lineHeight = 30.8.sp,
    letterSpacing = (-0.02).em,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = Color.Black
)

// Titles
val TitleLg = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 24.sp,
    lineHeight = 28.8.sp,
    letterSpacing = (-0.02).em,
    color = Color.Black
)

val TitleMd = TextStyle( // Used for side balances
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600 (Numbers)
    fontSize = 20.sp,
    lineHeight = 25.sp,
    letterSpacing = (-0.01).em,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = Color.Black
)

val TitleSm = TextStyle( // Used for "Net Balance"
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500 (Other texts)
    fontSize = 18.sp,
    lineHeight = 23.4.sp,
    letterSpacing = (-0.01).em,
    color = ColorText
)

// Body
val BodyLg = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500 (Other texts)
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = ColorText
)

val BodyLgNumeric = BodyLg.copy(
    fontWeight = FontWeight.SemiBold,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = Color.Black
)

val BodyMd = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500 (Other texts)
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = ColorText
)

val BodyMdNumeric = BodyMd.copy(
    fontWeight = FontWeight.SemiBold,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = Color.Black
)

val BodySm = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500 (Other texts)
    fontSize = 14.sp,
    lineHeight = 20.3.sp,
    color = ColorText
)

// Labels
val LabelLg = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 14.sp,
    lineHeight = 18.2.sp,
    color = ColorText
)

val LabelMd = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 13.sp,
    lineHeight = 16.9.sp,
    color = ColorText
)

val LabelSm = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Normal, // 400
    fontSize = 12.sp,
    lineHeight = 15.6.sp,
    color = ColorText
)

// Metadata / Microcopy
val MetaMd = TextStyle( // INCOMING / OUTGOING
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500 (Other texts)
    fontSize = 12.sp,
    lineHeight = 15.6.sp,
    letterSpacing = 0.08.em,
    color = ColorTextMuted
)

val MetaSm = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 11.sp,
    lineHeight = 14.3.sp,
    letterSpacing = 0.12.em,
    color = ColorTextMuted
)

// Navigation
val NavLabel = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 11.sp,
    lineHeight = 13.2.sp,
    color = ColorTextMuted
)

// Buttons
val ButtonMd = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 14.sp,
    lineHeight = 16.8.sp,
    color = ColorSurface
)

val ButtonSm = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 13.sp,
    lineHeight = 15.6.sp,
    color = ColorSurface
)

val LabelSmall = LabelSm

// Hero Typography
val HeroOverline = MetaMd.copy(
    color = ColorTextMuted,
    letterSpacing = 0.14.em,
    fontWeight = FontWeight.Medium
)

val HeroMainAmount = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.ExtraBold, // 800
    fontSize = 32.sp,
    lineHeight = 32.sp,
    letterSpacing = (-0.04).em,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = ColorText
)

val HeroStatLabel = MetaMd.copy(
    color = ColorTextMuted,
    fontWeight = FontWeight.Medium
)

val HeroStatValue = TitleSm.copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.SemiBold,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = ColorText
)

// Transaction List Typography
val ListSectionHeader = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Bold, // 700
    fontSize = 18.sp,
    color = ColorText
)

val ListActionText = MetaMd.copy(
    fontSize = 12.sp,
    fontWeight = FontWeight.SemiBold, // 600
    color = ColorPrimary,
    letterSpacing = 0.08.em
)

val ListGroupHeader = MetaSm.copy(
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium, // 500
    letterSpacing = 0.14.em,
    color = ColorTextMuted
)

val ListMerchantName = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 14.sp,
    color = ColorText
)

val ListMetadata = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 13.sp,
    color = ColorTextMuted
)

val ListAmountBold = TextStyle(
    fontFamily = Figtree,
    fontWeight = FontWeight.Bold, // 700
    fontSize = 14.sp,
    fontFeatureSettings = TabularNumFeatureSettings,
    color = ColorText
)

val Typography = Typography(
    displayLarge = DisplayLg,
    displayMedium = DisplayMd,
    titleLarge = TitleLg,
    titleMedium = TitleMd,
    titleSmall = TitleSm,
    bodyLarge = BodyLg,
    bodyMedium = BodyMd,
    bodySmall = BodySm,
    labelLarge = LabelLg,
    labelMedium = LabelMd,
    labelSmall = LabelSm
)