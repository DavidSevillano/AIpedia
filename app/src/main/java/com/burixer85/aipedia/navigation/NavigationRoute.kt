package com.burixer85.aipedia.navigation

sealed class NavigationRoute(val route:String) {
    object Home : NavigationRoute("home")
}