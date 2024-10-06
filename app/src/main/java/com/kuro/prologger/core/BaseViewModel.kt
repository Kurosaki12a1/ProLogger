package com.kuro.prologger.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuro.prologger.navigation.Route
import com.kuro.prologger.navigation.util.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val appNavigator: AppNavigator
) : ViewModel() {

    fun tryNavigateBack(
        route: Route? = null,
        inclusive: Boolean = false,
    ) {
        appNavigator.tryNavigateBack(
            route = route,
            inclusive = inclusive
        )
    }

    fun tryNavigateTo(
        route: Route,
        popUpToRoute: Route? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    ) {
        appNavigator.tryNavigateTo(
            route = route,
            popUpToRoute = popUpToRoute,
            inclusive = inclusive,
            isSingleTop = isSingleTop
        )
    }

    fun navigateBack(
        route: Route? = null,
        inclusive: Boolean = false,
    ) {
        viewModelScope.launch {
            appNavigator.navigateBack(
                route = route,
                inclusive = inclusive
            )
        }
    }

    fun navigateTo(
        route: Route,
        popUpToRoute: Route? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    ) {
        viewModelScope.launch {
            appNavigator.navigateTo(
                route = route,
                popUpToRoute = popUpToRoute,
                inclusive = inclusive,
                isSingleTop = isSingleTop
            )
        }
    }

}