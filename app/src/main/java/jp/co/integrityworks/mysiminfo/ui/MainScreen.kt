package jp.co.integrityworks.mysiminfo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import jp.co.integrityworks.mysiminfo.R
import jp.co.integrityworks.mysiminfo.ui.home.DeviceScreen
import jp.co.integrityworks.mysiminfo.ui.home.HomeViewModel
import jp.co.integrityworks.mysiminfo.ui.home.NetworkScreen
import jp.co.integrityworks.mysiminfo.ui.home.SimScreen
import jp.co.integrityworks.mysiminfo.ui.home.SpeedScreen
import jp.co.integrityworks.mysiminfo.ui.home.UsageScreen

sealed class Screen(val route: String, val resourceId: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Sim : Screen("sim", R.string.tab_sim, Icons.Default.CellTower)
    object Network : Screen("network", R.string.tab_network, Icons.Default.Router)
    object Usage : Screen("usage", R.string.tab_usage, Icons.Default.NetworkCheck)
    object Device : Screen("device", R.string.tab_device, Icons.Default.Smartphone)
    object Speed : Screen("speed", R.string.tab_speed, Icons.Default.Speed)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: HomeViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Sim,
        Screen.Network,
        Screen.Usage,
        Screen.Device,
        Screen.Speed
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = items.find { it.route == currentDestination?.route } ?: Screen.Sim

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(currentScreen.resourceId))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Column {
                // AdMob banner
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    factory = { ctx ->
                        AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = ctx.getString(R.string.ad_unit_id)
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Sim.route, Modifier.padding(innerPadding)) {
            composable(Screen.Sim.route) { SimScreen(viewModel) }
            composable(Screen.Network.route) { NetworkScreen(viewModel) }
            composable(Screen.Usage.route) { UsageScreen(viewModel) }
            composable(Screen.Device.route) { DeviceScreen(viewModel) }
            composable(Screen.Speed.route) { SpeedScreen(viewModel) }
        }
    }
}
