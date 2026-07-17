package com.example.catalogapp.navigation

sealed class Screen(val route: String) {
    object Catalog : Screen("catalog")
    object Detail : Screen("detail/{productId}") {
        fun createRoute(productId: Int) = "detail/$productId"
    }
}
