package com.burixer85.aipedia.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.burixer85.aipedia.home.presentation.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.burixer85.aipedia.core.di.SupabaseEntryPoint
import com.burixer85.aipedia.detail.presentation.DetailScreen
import dagger.hilt.android.EntryPointAccessors

@Composable
fun NavigationHost(navHostController: NavHostController) {
    val context = LocalContext.current
    val supabase = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            SupabaseEntryPoint::class.java
        ).supabaseClient()
    }

    NavHost(
        navController = navHostController,
        startDestination = NavigationRoute.Home.route,
    ) {

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
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }

        ) { backStackEntry ->
            val aiId = backStackEntry.arguments?.getString("aiId")
            DetailScreen(
                aiId = aiId,
                onBack = { navHostController.popBackStack() },
                supabase = supabase
            )
        }
    }
}