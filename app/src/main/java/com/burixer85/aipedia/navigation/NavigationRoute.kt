package com.burixer85.aipedia.navigation

sealed class NavigationRoute(val route: String) {
    object Home : NavigationRoute("home")
    object Ai : NavigationRoute("ai")
    object Profile : NavigationRoute("profile")
    object Compare : NavigationRoute("compare")
}
