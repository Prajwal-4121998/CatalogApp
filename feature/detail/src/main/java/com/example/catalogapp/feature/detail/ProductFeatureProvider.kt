package com.example.catalogapp.feature.detail

private const val FIRST_N_CHARACTERS_FROM_STRING = 8

data class ProductFeature(val icon: String, val label: String, val value: String)

object ProductFeatureProvider {

    fun getFeaturesForProduct(
        category: String, rating: Float
    ): List<ProductFeature> {
        val ratingFormatted = String.format(java.util.Locale.getDefault(), "%.1f", rating)

        return when {
            category.contains("electronic", ignoreCase = true) || category.contains(
                "phone",
                ignoreCase = true
            ) || category.contains("laptop", ignoreCase = true) -> listOf(
                ProductFeature("◆", "Build", "Premium"),
                ProductFeature("⚡", "Battery", "Long Life"),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Warranty", "1 Year")
            )

            category.contains("jewelery", ignoreCase = true) || category.contains(
                "jewellery",
                ignoreCase = true
            ) || category.contains("jewelry", ignoreCase = true) -> listOf(
                ProductFeature("◆", "Material", "Certified"),
                ProductFeature("✦", "Purity", "Hallmark"),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Finish", "Polished")
            )

            category.contains("clothing", ignoreCase = true) || category.contains(
                "fashion",
                ignoreCase = true
            ) || category.contains("wear", ignoreCase = true) -> listOf(
                ProductFeature("◆", "Fabric", "Premium"),
                ProductFeature("✦", "Fit", "Regular"),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Care", "Easy Wash")
            )

            category.contains("men", ignoreCase = true) -> listOf(
                ProductFeature("◆", "Style", "Modern"),
                ProductFeature("✦", "Fit", "Slim Fit"),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Care", "Easy Wash")
            )

            category.contains("women", ignoreCase = true) -> listOf(
                ProductFeature("◆", "Style", "Trendy"),
                ProductFeature("✦", "Fit", "Comfort"),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Care", "Easy Wash")
            )

            else -> listOf(
                ProductFeature("◆", "Quality", "Premium"),
                ProductFeature(
                    "✦",
                    "Type",
                    category.take(FIRST_N_CHARACTERS_FROM_STRING)
                        .replaceFirstChar { it.uppercase() }),
                ProductFeature("★", "Rating", ratingFormatted),
                ProductFeature("✓", "Delivery", "Fast")
            )
        }
    }
}
