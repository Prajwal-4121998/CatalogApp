package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val REVIEW_CARD_CORNER = 16
private const val AVATAR_SIZE = 40
private const val STAR_SIZE = 14
private const val EMPTY_STAR_ALPHA = 0.2f
private const val TOTAL_STARS = 5

private data class ReviewData(
    val name: String,
    val rating: Int,
    val review: String,
    val date: String
)

private val sampleReviews = listOf(
    ReviewData(
        name = "Rohan Sharma",
        rating = 5,
        review = "\"The build quality is absolutely phenomenal. " +
                "It feels much more expensive than it is. " +
                "Highly recommend for anyone looking for a premium product.\"",
        date = "2 days ago"
    ),
    ReviewData(
        name = "Ananya Verma",
        rating = 4,
        review = "\"Absolutely love the battery life! " +
                "The software is fluid and the tracking seems very accurate " +
                "compared to my other devices.\"",
        date = "1 week ago"
    )
)

@Composable
internal fun DetailReviewsSection(modifier: Modifier = Modifier) {
    val spacing = CatalogTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = { }) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            sampleReviews.forEach { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewData) {
    val spacing = CatalogTheme.spacing
    // Stitch: bg-surface-container p-lg rounded-2xl border border-outline-variant/20 shadow-sm
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(REVIEW_CARD_CORNER.dp)
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = EMPTY_STAR_ALPHA),
                shape = RoundedCornerShape(REVIEW_CARD_CORNER.dp)
            )
            .padding(spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReviewerAvatar(name = review.name)
                Column {
                    Text(
                        text = review.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Stitch: stars in text-secondary-container (amber)
                    StarRow(rating = review.rating)
                }
            }
            Text(
                text = review.date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(spacing.md))
        Text(
            text = review.review,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReviewerAvatar(name: String) {
    // Stitch: w-10 h-10 rounded-full — initials avatar since we don't have real user images
    Box(
        modifier = Modifier
            .size(AVATAR_SIZE.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.first().toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun StarRow(rating: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(TOTAL_STARS) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(STAR_SIZE.dp),
                // Stitch: filled stars in secondary-container (amber), empty star muted
                tint = if (index < rating) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = EMPTY_STAR_ALPHA)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailReviewsSectionPreview() {
    CatalogTheme { DetailReviewsSection() }
}
