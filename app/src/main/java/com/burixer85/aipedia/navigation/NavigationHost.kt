package com.burixer85.aipedia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.burixer85.aipedia.presentation.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationHost(navHostController: NavHostController) {
    NavHost(navController = navHostController,startDestination = NavigationRoute.Home.route){
        composable(NavigationRoute.Home.route){
            HomeScreen()
        }
    }
}