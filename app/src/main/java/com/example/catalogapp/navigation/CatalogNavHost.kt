package com.example.catalogapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.catalogapp.feature.catalog.CatalogScreen
import com.example.catalogapp.feature.detail.DetailScreen
import com.example.catalogapp.feature.search.SearchScreen

private const val NAV_TRANSITION_DURATION_MS = 250

@Composable
fun CatalogNavHost(
    navController: NavHostController = rememberNavController()
) {
    Surface(
        modifier = Modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = CatalogRoute,
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(NAV_TRANSITION_DURATION_MS)
                ) { fullWidth -> fullWidth } + fadeIn(tween(NAV_TRANSITION_DURATION_MS))
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(NAV_TRANSITION_DURATION_MS)
                ) { fullWidth -> -fullWidth } + fadeOut(tween(NAV_TRANSITION_DURATION_MS))
            },
            popEnterTransition = {
                slideInHorizontally(
                    animationSpec = tween(NAV_TRANSITION_DURATION_MS)
                ) { fullWidth -> -fullWidth } + fadeIn(tween(NAV_TRANSITION_DURATION_MS))
            },
            popExitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(NAV_TRANSITION_DURATION_MS)
                ) { fullWidth -> fullWidth } + fadeOut(tween(NAV_TRANSITION_DURATION_MS))
            }
        ) {
            composable<CatalogRoute> {
                CatalogScreen(
                    onNavigateToDetail = { productId ->
                        navController.navigateSafely(DetailRoute(productId))
                    },
                    onNavigateToSearch = {
                        navController.navigateSafely(SearchRoute)
                    }
                )
            }

            composable<DetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<DetailRoute>()
                DetailScreen(
                    productId = route.productId,
                    onNavigateBack = { navController.popBackStackSafely() }
                )
            }

            composable<SearchRoute> {
                SearchScreen(
                    onNavigateToDetail = { productId ->
                        navController.navigateSafely(DetailRoute(productId))
                    }
                )
            }
        }
    }
}

