package com.kuro.prologger.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Splash : Route()

    @Serializable
    data object Home : Route()

    @Serializable
    data object Search : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data class Details(val id: Int) : Route()
}

