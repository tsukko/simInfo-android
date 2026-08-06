package jp.co.integrityworks.mysiminfo

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.co.integrityworks.mysiminfo.ui.MainScreen
import jp.co.integrityworks.mysiminfo.ui.home.DetailScreen
import jp.co.integrityworks.mysiminfo.ui.home.HomeViewModel

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            val viewModel: HomeViewModel = viewModel()
            MainScreen(viewModel)
        }
        composable(
            "detail/{message}",
            arguments = listOf(navArgument("message") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailScreen(
                navController = navController,
                message = backStackEntry.arguments?.getString("message") ?: "No Message"
            )
        }
    }
}