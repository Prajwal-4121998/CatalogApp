package com.example.catalogapp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CatalogShapes = Shapes(
    // extraSmall — 4dp (Stitch sm)
    extraSmall = RoundedCornerShape(4.dp),
    // small — 8dp (Stitch DEFAULT)
    small = RoundedCornerShape(8.dp),
    // medium — 12dp (Stitch md) — chips
    medium = RoundedCornerShape(12.dp),
    // large — 16dp (Stitch lg) — cards
    large = RoundedCornerShape(16.dp),
    // extraLarge — 24dp (Stitch xl) — buttons, search bars
    extraLarge = RoundedCornerShape(24.dp)
)
