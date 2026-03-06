package uk.ac.tees.mad.minicart.presentation.naivagation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.minicart.ViewModel.AppViewModel
import uk.ac.tees.mad.minicart.presentation.screens.AuthScreen

@Composable
fun AppNav(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel? = null
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            AuthScreen(
                viewModel = appViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        

    }
}