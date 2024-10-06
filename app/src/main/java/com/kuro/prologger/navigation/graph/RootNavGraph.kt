package com.kuro.prologger.navigation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(700)) + fadeIn(animationSpec = tween(700))
        },
        exitTransition = {
            slideOutHorizontally(animationSpec = tween(700)) + fadeOut(animationSpec = tween(700))
        },
        popEnterTransition = {
            slideInHorizontally(animationSpec = tween(700)) + fadeIn(animationSpec = tween(700))
        },
        popExitTransition = {
            slideOutHorizontally(animationSpec = tween(700)) + fadeOut(animationSpec = tween(700))
        }
    ) {
        splashNavGraph()
        homeNavGraph()
        searchNavGraph()
        settingsNavGraph()
    }
}