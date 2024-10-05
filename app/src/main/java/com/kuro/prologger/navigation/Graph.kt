package com.kuro.prologger.navigation

import kotlinx.serialization.Serializable

sealed class Graph {
    @Serializable
    data object SplashGraph : Graph()

    @Serializable
    data object HomeGraph : Graph()

    @Serializable
    data object SearchGraph : Graph()

    @Serializable
    data object SettingsGraph : Graph()
}