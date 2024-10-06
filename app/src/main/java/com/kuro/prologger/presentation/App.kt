package com.kuro.prologger.presentation

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.kuro.prologger.core.AppNavigationBar
import com.kuro.prologger.navigation.Graph
import com.kuro.prologger.navigation.Route
import com.kuro.prologger.navigation.bottomNavigationItems
import com.kuro.prologger.navigation.graph.RootNavGraph
import com.kuro.prologger.navigation.util.AppNavigator
import com.kuro.prologger.navigation.util.NavigationIntent
import com.kuro.prologger.presentation.theme.BackgroundColor
import com.kuro.prologger.presentation.theme.GreenColor
import com.kuro.prologger.presentation.theme.titleFont
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    appNavigator: AppNavigator,
    onFloatingButtonClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowTopAndBottomBar = remember(navBackStackEntry) {
        derivedStateOf { currentRoute != null && currentRoute != Route.Splash::class.qualifiedName }
    }

    NavigationEffects(
        navHostController = navController,
        navigationChannel = appNavigator.navigationChannel
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            if (shouldShowTopAndBottomBar.value) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = GreenColor,
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = titleFont,
                            text = stringResource(R.string.app_name)
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            if (shouldShowTopAndBottomBar.value) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = onFloatingButtonClick,
                    containerColor = BackgroundColor
                ) {
                    Text(
                        text = stringResource(R.string.start_get_log),
                        fontFamily = titleFont,
                        color = GreenColor
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            if (shouldShowTopAndBottomBar.value) {
                Column {
                    HorizontalDivider(color = BackgroundColor, thickness = 1.dp)
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
        }
    ) {
        RootNavGraph(
            navController = navController,
            paddingValues = it,
            startDestination = Graph.SplashGraph
        )
    }
}

fun getCurrentRoute(route: String?): Route {
    return if (route == null) return Route.Home
    else Route::class.sealedSubclasses.find { it.qualifiedName == route }?.objectInstance
        ?: Route.Home
}

@Composable
fun BottomNavigationBar(
    selectedRoute: Route,
    onNavigationClick: (Route) -> Unit
) {
    AppNavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
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