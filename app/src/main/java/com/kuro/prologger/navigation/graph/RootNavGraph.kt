package com.kuro.prologger.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kuro.prologger.navigation.Graph

@Composable
fun RootNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: Graph
) {
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = startDestination
    ) {
        splashNavGraph()
        homeNavGraph()
        searchNavGraph()
        settingsNavGraph()
    }
}