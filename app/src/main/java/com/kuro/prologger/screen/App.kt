package com.kuro.prologger.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kuro.prologger.R
import com.kuro.prologger.navigation.Graph
import com.kuro.prologger.navigation.Route
import com.kuro.prologger.navigation.bottomNavigationItems
import com.kuro.prologger.navigation.graph.RootNavGraph
import com.kuro.prologger.navigation.util.AppNavigator
import com.kuro.prologger.navigation.util.NavigationIntent
import com.kuro.prologger.ui.theme.GreenColor
import com.kuro.prologger.ui.theme.titleFont
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun App(
    appNavigator: AppNavigator
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowTopAndBottomBar = remember(navBackStackEntry) {
        derivedStateOf {
            currentRoute == null || currentRoute != Route.Splash::class.qualifiedName
        }
    }

    NavigationEffects(
        navHostController = navController,
        navigationChannel = appNavigator.navigationChannel
    )

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            if (shouldShowTopAndBottomBar.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        color = GreenColor,
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = titleFont,
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.app_name)
                    )
                    Image(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = "Settings"
                    )
                }
            }
        },
        bottomBar = {
            if (shouldShowTopAndBottomBar.value) {
                BottomNavigationBar(
                    selectedRoute = getCurrentRoute(currentRoute),
                    onNavigationClick = {
                        navController.navigate(it) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        RootNavGraph(
            navController = navController,
            paddingValues = it,
            startDestination = Graph.SplashGraph
        )
    }

    /*   LaunchedEffect(Unit) {
           coroutineScope.launch {
               while (true) {
                   logList = readDeviceLogs()
                   delay(2000L) // Refresh every 2 seconds
               }
           }
       }

       LazyColumn(
           modifier = Modifier
               .background(Color.Black)
               .padding(16.dp)
       ) {
           items(logList) { log ->
               Text(
                   text = log,
                   color = Color.White,
                   modifier = Modifier.padding(vertical = 4.dp)
               )
           }
       }*/
}

fun getCurrentRoute(route: String?): Route {
    return if (route == null) return Route.Splash
    else Route::class.sealedSubclasses.find { it.qualifiedName == route }?.objectInstance ?: Route.Splash
}

@Composable
fun BottomNavigationBar(
    selectedRoute: Route,
    onNavigationClick: (Route) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        containerColor = Color.White
    ) {
        bottomNavigationItems.forEach { item ->
            val isSelected = item.route == selectedRoute
            NavigationBarItem(
                alwaysShowLabel = false,
                selected = isSelected,
                icon = {
                    if (isSelected) {
                        Image(
                            painter = painterResource(id = item.iconPressed),
                            contentDescription = item.route.toString()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.route.toString()
                        )
                    }
                },
                onClick = { onNavigationClick(item.route) }
            )
        }
    }

}

@Composable
fun NavigationEffects(
    navigationChannel: Channel<NavigationIntent>,
    navHostController: NavHostController
) {
    val activity = (LocalContext.current as? Activity)
    LaunchedEffect(activity, navHostController, navigationChannel) {
        navigationChannel.receiveAsFlow().collect { intent ->
            if (activity?.isFinishing == true) {
                return@collect
            }
            when (intent) {
                is NavigationIntent.NavigateBack -> {
                    if (intent.route != null) {
                        navHostController.popBackStack(intent.route, intent.inclusive)
                    } else {
                        navHostController.popBackStack()
                    }
                }

                is NavigationIntent.NavigateTo -> {
                    navHostController.navigate(intent.route) {
                        launchSingleTop = intent.isSingleTop
                        intent.popUpToRoute?.let { popUpToRoute ->
                            popUpTo(popUpToRoute) { inclusive = intent.inclusive }
                        }
                    }
                }
            }
        }
    }
}

fun readDeviceLogs(): List<String> {
    val logList = mutableListOf<String>()
    try {
        val process = Runtime.getRuntime().exec("logcat -d")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            logList.add(line)
            line = reader.readLine()
        }
    } catch (e: Exception) {
        logList.add("Error reading logs: ${e.message}")
    }
    return logList
}