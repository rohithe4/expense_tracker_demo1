package com.example.expensetrackerdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.expensetrackerdemo.ui.theme.ColorBg
import com.example.expensetrackerdemo.ui.theme.ColorText
import com.example.expensetrackerdemo.ui.theme.DisplayMd
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(800) // Brief intentional pause for a premium feel
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Finance Tracker",
            style = DisplayMd,
            color = ColorText
        )
    }
}
