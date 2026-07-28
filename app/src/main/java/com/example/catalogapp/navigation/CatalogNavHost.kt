package com.example.catalogapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.catalogapp.feature.catalog.CatalogScreen
import com.example.catalogapp.feature.detail.DetailScreen
import com.example.catalogapp.feature.search.SearchScreen

@Composable
fun CatalogNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = CatalogRoute
    ) {
        composable<CatalogRoute> {
            CatalogScreen(
                onNavigateToDetail = { productId ->
                    navController.navigate(DetailRoute(productId))
                },
                onNavigateToSearch = {
                    navController.navigate(SearchRoute)
                }
            )
        }

        composable<DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailRoute>()
            DetailScreen(
                productId = route.productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SearchRoute> {
            SearchScreen(
                onNavigateToDetail = { productId ->
                    navController.navigate(DetailRoute(productId))
                }
            )
        }
    }
}


