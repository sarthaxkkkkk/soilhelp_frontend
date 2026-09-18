package com.soilhelp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soilhelp.android.data.TokenManager
import com.soilhelp.android.ui.screens.DashboardScreen
import com.soilhelp.android.ui.screens.LoginScreen
import com.soilhelp.android.ui.screens.RegisterScreen
import com.soilhelp.android.ui.theme.SoilHelpTheme
import com.soilhelp.android.viewmodel.AuthViewModel
import com.soilhelp.android.viewmodel.ReadingsViewModel
import com.soilhelp.android.viewmodel.ViewModelFactory

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(applicationContext)
        val factory = ViewModelFactory(tokenManager)

        setContent {
            SoilHelpTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val startDestination = if (tokenManager.isLoggedIn()) Routes.DASHBOARD else Routes.LOGIN

                    AppNavHost(navController, startDestination, factory, tokenManager)
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    factory: ViewModelFactory,
    tokenManager: TokenManager
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.DASHBOARD) {
            val readingsViewModel: ReadingsViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = readingsViewModel,
                onLogout = {
                    tokenManager.clearToken()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
    }
}
