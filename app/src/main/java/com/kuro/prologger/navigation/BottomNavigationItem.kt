package com.kuro.prologger.navigation

import com.kuro.prologger.R

data class BottomNavigationItem(
    val route: Route,
    val icon: Int,
    val iconPressed: Int
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(Route.Home, R.drawable.ic_home, R.drawable.ic_home_pressed),
    BottomNavigationItem(Route.Search, R.drawable.ic_search, R.drawable.ic_search_pressed),
    BottomNavigationItem(Route.Settings, R.drawable.ic_settings, R.drawable.ic_settings_pressed)
)