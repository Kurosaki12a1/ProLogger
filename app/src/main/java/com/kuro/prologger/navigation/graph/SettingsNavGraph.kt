package com.kuro.prologger.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kuro.prologger.navigation.Graph
import com.kuro.prologger.navigation.Route

fun NavGraphBuilder.settingsNavGraph() {
    navigation<Graph.SettingsGraph>(
        startDestination = Route.Settings
    ) {
        composable<Route.Settings> {
        }
    }
}