package com.example.catalogapp.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.DetailHeroImageDefaults.HERO_ASPECT_HEIGHT
import com.example.catalogapp.core.designsystem.components.DetailHeroImageDefaults.HERO_ASPECT_WIDTH
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val SHIMMER_ANIMATION_DURATION_MS = 1200
private const val SHIMMER_TRANSLATE_TARGET = 1000f
private val ChipCornerRadius = 12.dp
private val ChipSkeletonWidthDp = 72.dp
private val ChipSkeletonHeightDp = 32.dp
private val ChipSkeletonWidths = listOf(56.dp, 88.dp, 72.dp, 64.dp)
private val DetailSkeletonCardCorner = 24.dp
private val DetailSkeletonFeatureGridHeight = 96.dp
private const val CARD_OVERLAP_DP = 48

/**
 * Shared shimmer brush used by every skeleton in the design system.
 * Colors intentionally come from MaterialTheme so light/dark mode both work correctly.
 */
@Composable
internal fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.surfaceContainerHighest,
        MaterialTheme.colorScheme.surfaceContainerHigh
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = SHIMMER_TRANSLATE_TARGET,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SHIMMER_ANIMATION_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - SHIMMER_TRANSLATE_TARGET, 0f),
        end = Offset(translateAnimation, SHIMMER_TRANSLATE_TARGET)
    )
}

@Composable
private fun ShimmerBox(modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(rememberShimmerBrush())) {}
}

@Composable
fun CategoryChipSkeleton(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ChipCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .width(ChipSkeletonWidthDp)
                .height(ChipSkeletonHeightDp)
                .background(rememberShimmerBrush())
        ) {}
    }
}

@Composable
fun CategoryChipRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(CatalogTheme.spacing.sm)
    ) {
        ChipSkeletonWidths.forEach { width ->
            Surface(
                shape = RoundedCornerShape(ChipCornerRadius),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(
                    modifier = Modifier
                        .width(width)
                        .height(ChipSkeletonHeightDp)
                        .background(rememberShimmerBrush())
                ) {
                    CategoryChipSkeleton()
                }
            }
        }
    }
}

/**
 * Mirrors ProductCard's exact dimensions: square image area, two-line title,
 * price bar. Used in ProductGridSkeleton while catalog data loads.
 */
@Composable
fun ProductCardSkeleton(modifier: Modifier = Modifier) {
    val spacing = CatalogTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProductCardDefaults.CORNER_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Image area — SAME aspect ratio constants as ProductImageSection
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(
                        ProductCardDefaults.IMAGE_ASPECT_WIDTH /
                                ProductCardDefaults.IMAGE_ASPECT_HEIGHT
                    )
            )
            // Info area — SAME padding/spacing tokens as ProductInfoSection
            Column(
                modifier = Modifier.padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                // Title — 2 lines, matches maxLines = 2 on the real title
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(spacing.md)
                )
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(spacing.md)
                )
                // Rating row placeholder — matches RatingRow's height
                ShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(spacing.sm)
                )
                // Price — matches titleMedium text block
                ShimmerBox(
                    modifier = Modifier
                        .width(70.dp)
                        .height(spacing.md)
                )
            }
        }
    }
}

/**
 * Mirrors the detail screen's content card sections below the hero image:
 * title, price, and a features-grid placeholder. The hero image itself
 * is NOT shimmered here — Coil's placeholder() handles that separately.
 */
@Composable
fun DetailSkeleton(modifier: Modifier = Modifier) {
    val spacing = CatalogTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(HERO_ASPECT_WIDTH / HERO_ASPECT_HEIGHT)
                .background(rememberShimmerBrush())
        ) {}

        Box(modifier = Modifier.padding(horizontal = spacing.md)) {
            DetailSkeletonContentCard()
        }
    }
}

@Composable
private fun DetailSkeletonContentCard() {
    val spacing = CatalogTheme.spacing
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = -CARD_OVERLAP_DP.dp),
        shape = RoundedCornerShape(DetailSkeletonCardCorner),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(spacing.md)) {
            DetailSkeletonHeader()
            Spacer(modifier = Modifier.height(spacing.lg))
            DetailSkeletonPriceAndFeatures()
            Spacer(modifier = Modifier.height(spacing.xl))
            DetailSkeletonDescriptionLines()
        }
    }
}

@Composable
private fun DetailSkeletonHeader() {
    val spacing = CatalogTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(spacing.lg)
            .background(rememberShimmerBrush())
    ) {}
    Spacer(modifier = Modifier.height(spacing.sm))
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(spacing.md)
            .background(rememberShimmerBrush())
    ) {}
}

@Composable
private fun DetailSkeletonPriceAndFeatures() {
    val spacing = CatalogTheme.spacing
    Column(
        modifier = Modifier
            .width(80.dp)
            .height(spacing.lg)
            .background(rememberShimmerBrush())
    ) {}
    Spacer(modifier = Modifier.height(spacing.lg))
    Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
        repeat(2) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(DetailSkeletonFeatureGridHeight)
                    .background(rememberShimmerBrush())
            ) {}
        }
    }
}

@Composable
private fun DetailSkeletonDescriptionLines() {
    val spacing = CatalogTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        repeat(3) { index ->
            val widthFraction = if (index == 2) 0.5f else 1f
            Column(
                modifier = Modifier
                    .fillMaxWidth(widthFraction)
                    .height(spacing.sm)
                    .background(rememberShimmerBrush())
            ) {}
        }
    }
}
