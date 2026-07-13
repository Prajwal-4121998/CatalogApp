package com.example.catalogapp.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val xs: Dp = 4.dp,       // Stitch xs
    val sm: Dp = 8.dp,       // Stitch sm / base
    val md: Dp = 16.dp,      // Stitch md / mobile margin
    val lg: Dp = 24.dp,      // Stitch lg / tablet margin
    val xl: Dp = 32.dp,      // Stitch xl — section separation
    val xxl: Dp = 48.dp,     // extra large gaps
    val cardPadding: Dp = 12.dp,
    val screenPadding: Dp = 16.dp,
    val gutter: Dp = 16.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }
