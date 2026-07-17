package com.example.catalogapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.catalogapp.feature.catalog.CatalogScreen

@Composable
fun CatalogNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Catalog.route
    ) {
        composable(Screen.Catalog.route) {
            CatalogScreen(
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.Detail.createRoute(productId))
                }
            )
        }

        // Detail screen — added in next session
        composable(Screen.Detail.route) {
            // Placeholder until feature:detail is built
        }
    }
}
