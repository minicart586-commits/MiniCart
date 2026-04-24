package uk.ac.tees.mad.minicart.presentation.naivagation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.presentation.screens.AuthScreen
import uk.ac.tees.mad.minicart.presentation.screens.SplashScreen
import uk.ac.tees.mad.minicart.ui.theme.PrimaryTeal

@Composable
fun AppNav(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel? = null
) {
    val auth = FirebaseAuth.getInstance().currentUser
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.HOME,
        NavRoutes.CART,
        NavRoutes.SETTINGS
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {

                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.HOME,
                        onClick = {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(NavRoutes.HOME) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = PrimaryTeal.copy(alpha = 0.1f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.CART,
                        onClick = {
                            navController.navigate(NavRoutes.CART) {
                                popUpTo(NavRoutes.HOME)
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                        label = { Text("Cart") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = PrimaryTeal.copy(alpha = 0.1f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.SETTINGS,
                        onClick = {
                            navController.navigate(NavRoutes.SETTINGS) {
                                popUpTo(NavRoutes.HOME)
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            indicatorColor = PrimaryTeal.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { _ ->


        Surface(modifier = Modifier.padding(bottom = 90.dp)) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.SPLASH
            ) {

                composable(NavRoutes.SPLASH) {
                    SplashScreen(
                        onNavigateNext = {
                            val nextRoute =
                                if (auth != null) NavRoutes.HOME else NavRoutes.LOGIN

                            navController.navigate(nextRoute) {
                                popUpTo(NavRoutes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.LOGIN) {
                    AuthScreen(
                        viewModel = appViewModel,
                        initialIsLogin = true,
                        onLoginSuccess = {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(NavRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.SIGNUP) {
                    AuthScreen(
                        viewModel = appViewModel,
                        initialIsLogin = false,
                        onLoginSuccess = {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(NavRoutes.SIGNUP) { inclusive = true }
                            }
                        }
                    )
                }

                composable(NavRoutes.HOME) {
                    if (appViewModel != null) {
                        uk.ac.tees.mad.minicart.presentation.screens.HomeScreen(
                            viewModel = appViewModel,
                            onCartClick = { navController.navigate(NavRoutes.CART) },
                            onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
                        )
                    }
                }

                composable(NavRoutes.CART) {
                    if (appViewModel != null) {
                        uk.ac.tees.mad.minicart.presentation.screens.CartScreen(
                            viewModel = appViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }

                composable(NavRoutes.SETTINGS) {
                    if (appViewModel != null) {
                        uk.ac.tees.mad.minicart.presentation.screens.SettingsScreen(
                            orderState = appViewModel.orderState.value,
                            onResetOrderState = { appViewModel.resetOrderState() },
                            onBackClick = { navController.popBackStack() },
                            onLogoutClick = {
                                appViewModel.signout()
                                navController.navigate(NavRoutes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onClearCacheClick = {
                                appViewModel.clearCache()
                            }
                        )
                    }
                }
            }
        }
    }
}