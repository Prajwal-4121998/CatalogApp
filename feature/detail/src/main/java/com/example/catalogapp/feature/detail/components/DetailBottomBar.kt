package com.example.catalogapp.feature.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.catalogapp.core.designsystem.theme.CatalogTheme

private const val BOTTOM_BAR_HEIGHT = 96
private const val WISHLIST_BUTTON_SIZE = 56
private const val WISHLIST_CORNER = 24
private const val CART_BUTTON_HEIGHT = 56
private const val CART_BUTTON_CORNER = 24
private const val CART_ICON_SIZE = 24

@Composable
internal fun DetailBottomBar(
    isWishlisted: Boolean,
    onWishlistClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = CatalogTheme.spacing
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BOTTOM_BAR_HEIGHT.dp)
                .padding(horizontal = spacing.md, vertical = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WishlistButton(isWishlisted = isWishlisted, onClick = onWishlistClick)
            AddToCartButton(onClick = onAddToCartClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WishlistButton(isWishlisted: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(WISHLIST_BUTTON_SIZE.dp),
        shape = RoundedCornerShape(WISHLIST_CORNER.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AddToCartButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = CatalogTheme.spacing
    Button(
        onClick = onClick,
        modifier = modifier.height(CART_BUTTON_HEIGHT.dp),
        shape = RoundedCornerShape(CART_BUTTON_CORNER.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(CART_ICON_SIZE.dp)
        )
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(
            text = "Add to Cart",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun DetailBottomBarPreview() {
    CatalogTheme {
        DetailBottomBar(
            isWishlisted = false,
            onWishlistClick = {},
            onAddToCartClick = {}
        )
    }
}
