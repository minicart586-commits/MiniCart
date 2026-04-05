package uk.ac.tees.mad.minicart.presentation.naivagation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.presentation.screens.AuthScreen

@Composable
fun AppNav(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel? = null
) {
    val auth= FirebaseAuth.getInstance().currentUser
    NavHost(
        navController = navController,
        startDestination = if (auth != null) NavRoutes.HOME else NavRoutes.LOGIN
    ) {
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
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        appViewModel.signout()
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onClearCacheClick = {
                        // For now, just add the button
                    }
                )
            }
        }
    }
}

