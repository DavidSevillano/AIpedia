package com.burixer85.aipedia.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.burixer85.aipedia.home.presentation.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.burixer85.aipedia.Detail.presentation.DetailScreen

@Composable
fun NavigationHost(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = NavigationRoute.Home.route) {

        composable(NavigationRoute.Home.route) {
            HomeScreen(
                onAiClick = { aiId ->
                    navHostController.navigate(NavigationRoute.Ai.route + "?aiId=$aiId")
                },
            )
        }

        composable(
            route = NavigationRoute.Ai.route + "?aiId={aiId}",
            arguments = listOf(
                navArgument(name = "aiId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val aiId = backStackEntry.arguments?.getString("aiId")
            DetailScreen(aiId = aiId,
                onBack = { navHostController.popBackStack() }
                )
        }
    }
}