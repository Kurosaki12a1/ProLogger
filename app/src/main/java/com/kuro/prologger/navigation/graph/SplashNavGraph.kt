package com.kuro.prologger.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kuro.prologger.navigation.Graph
import com.kuro.prologger.navigation.Route
import com.kuro.prologger.screen.splash.SplashScreen

fun NavGraphBuilder.splashNavGraph() {
    navigation<Graph.SplashGraph>(
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreen()
        }
    }
}