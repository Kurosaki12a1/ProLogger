package com.kuro.prologger.presentation.splash

import androidx.lifecycle.viewModelScope
import com.kuro.prologger.core.BaseViewModel
import com.kuro.prologger.domain.usecase.StoragePermissionUseCase
import com.kuro.prologger.navigation.util.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    appNavigator: AppNavigator,
    private val storagePermissionUseCase: StoragePermissionUseCase
) : BaseViewModel(appNavigator) {
    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished = _isTimerFinished.asStateFlow()

    private val allPermissionsGranted = MutableStateFlow(false)

    private var job: Job? = null

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        job?.cancel()
        job = viewModelScope.launch {
            // Want user see splash screen before request
            allPermissionsGranted.value = storagePermissionUseCase()
            if (allPermissionsGranted.value) {
                viewModelScope.launch {
                    delay(2000)
                    _isTimerFinished.value = true
                }
            }
        }
    }
}