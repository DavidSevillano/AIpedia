package com.burixer85.aipedia.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.burixer85.aipedia.core.di.SupabaseEntryPoint
import com.burixer85.aipedia.detail.presentation.DetailScreen
import com.burixer85.aipedia.home.presentation.HomeScreen
import com.burixer85.aipedia.profile.presentation.ProfileScreen
import com.burixer85.aipedia.ui.theme.MdOnSurfaceVariant
import com.burixer85.aipedia.ui.theme.MdPrimaryContainer
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

    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomNav = currentRoute?.startsWith(NavigationRoute.Ai.route) != true

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navHostController,
            startDestination = NavigationRoute.Home.route,
        ) {
            composable(NavigationRoute.Home.route) {
                HomeScreen(
                    onAiClick = { aiId ->
                        navHostController.navigate(NavigationRoute.Ai.route + "?aiId=$aiId")
                    }
                )
            }

            composable(NavigationRoute.Profile.route) {
                ProfileScreen(
                    onSignOut = {
                        navHostController.navigate(NavigationRoute.Home.route) {
                            popUpTo(NavigationRoute.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
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
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) { backStackEntry ->
                val aiId = backStackEntry.arguments?.getString("aiId")
                DetailScreen(
                    aiId = aiId,
                    onBack = { navHostController.popBackStack() },
                    supabase = supabase
                )
            }
        }

        if (showBottomNav) {
            SharedBottomNav(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navHostController.navigate(route) {
                            popUpTo(NavigationRoute.Home.route) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private data class NavItem(
    val route: String,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector
)

private val navItems = listOf(
    NavItem(NavigationRoute.Home.route, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("explore", "Explorar", Icons.Filled.Explore, Icons.Outlined.Explore),
    NavItem(NavigationRoute.Profile.route, "Perfil", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
private fun SharedBottomNav(
    currentRoute: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color(0xD91C2026),
            tonalElevation = 0.dp,
            shadowElevation = 20.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                navItems.forEach { item ->
                    NavBarItem(
                        item = item,
                        isActive = currentRoute == item.route,
                        onClick = if (item.route != "explore") { { onItemSelected(item.route) } } else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isActive: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val bg = if (isActive) MdPrimaryContainer else Color.Transparent
    val tint = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MdOnSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .background(bg, RoundedCornerShape(26.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isActive) item.iconFilled else item.iconOutlined,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            if (isActive) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = item.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint,
                    letterSpacing = (-0.065).sp
                )
            }
        }
    }
}
