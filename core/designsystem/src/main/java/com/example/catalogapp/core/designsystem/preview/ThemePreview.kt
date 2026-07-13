package com.example.catalogapp.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.components.CatalogEmptyState
import com.example.catalogapp.core.designsystem.components.CatalogErrorState
import com.example.catalogapp.core.designsystem.components.CatalogLoadingState
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

// ─── Color palette preview ────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Color Palette - Light")
@Preview(
    showBackground = true,
    name = "Color Palette - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
internal fun ColorPalettePreview() {
    CatalogTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Color Palette",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                ColorRow(
                    "Primary",
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary
                )
                ColorRow(
                    "Primary Container",
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ColorRow(
                    "Secondary",
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.onSecondary
                )
                ColorRow(
                    "Secondary Container",
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.onSecondaryContainer
                )
                ColorRow(
                    "Surface",
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.colorScheme.onSurface
                )
                ColorRow(
                    "Background",
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.onBackground
                )
                ColorRow(
                    "Error",
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

@Composable
internal fun ColorRow(name: String, color: Color, onColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 48.dp)
                .background(color, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = name, color = onColor, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─── Typography preview ───────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Typography - Light")
@Preview(
    showBackground = true,
    name = "Typography - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
internal fun TypographyPreview() {
    CatalogTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
                Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
                Text("Title Large", style = MaterialTheme.typography.titleLarge)
                Text("Title Medium", style = MaterialTheme.typography.titleMedium)
                Text("Title Small", style = MaterialTheme.typography.titleSmall)
                Text("Body Large", style = MaterialTheme.typography.bodyLarge)
                Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
                Text("Body Small", style = MaterialTheme.typography.bodySmall)
                Text("Label Large", style = MaterialTheme.typography.labelLarge)
                Text("Label Medium", style = MaterialTheme.typography.labelMedium)
                Text("Label Small", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ─── Buttons and chips preview ────────────────────────────────────────────────
@Preview(showBackground = true, name = "Buttons - Light")
@Preview(
    showBackground = true,
    name = "Buttons - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
internal fun ButtonPreview() {
    CatalogTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Buttons", style = MaterialTheme.typography.titleLarge)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Primary Button")
                }
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Text("Outlined Button")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = {}, label = { Text("Active") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Inactive") })
                }
            }
        }
    }
}

// ─── Product card preview ─────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Product Card - Light")
@Preview(
    showBackground = true,
    name = "Product Card - Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
internal fun ProductCardPreview() {
    CatalogTheme {
        val spacing = CatalogTheme.spacing
        Surface {
            Column(modifier = Modifier.padding(spacing.md)) {
                Text(
                    text = "Product Card",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = spacing.sm)
                )
                Card(
                    modifier = Modifier.width(180.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Product Image",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(modifier = Modifier.padding(spacing.sm)) {
                            Text(
                                text = "Fjallraven Backpack",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2
                            )
                            Text(
                                text = "₹8,999",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "⭐ 3.9 (120)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Spacing preview ──────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Spacing Scale")
@Composable
internal fun SpacingPreview() {
    CatalogTheme {
        val spacing = CatalogTheme.spacing
        Surface {
            Column(
                modifier = Modifier.padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Text("Spacing Scale", style = MaterialTheme.typography.titleLarge)
                SpacingRow("xs = 4dp", spacing.xs)
                SpacingRow("sm = 8dp", spacing.sm)
                SpacingRow("md = 16dp", spacing.md)
                SpacingRow("lg = 24dp", spacing.lg)
                SpacingRow("xl = 32dp", spacing.xl)
            }
        }
    }
}

@Composable
internal fun SpacingRow(label: String, size: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(100.dp)
        )
        Box(
            modifier = Modifier
                .width(size * 2)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
        )
    }
}

// ─── State components preview ─────────────────────────────────────────────────
@Preview(showBackground = true, name = "Loading State", heightDp = 300)
@Composable
internal fun LoadingPreview() {
    CatalogTheme {
        Surface { CatalogLoadingState() }
    }
}

@Preview(showBackground = true, name = "Error State", heightDp = 400)
@Composable
internal fun ErrorPreview() {
    CatalogTheme {
        Surface {
            CatalogErrorState(
                message = "Unable to load products. Please check your connection.",
                onRetry = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty State", heightDp = 400)
@Composable
internal fun EmptyPreview() {
    CatalogTheme {
        Surface {
            CatalogEmptyState(message = "No products found in this category.")
        }
    }
}
