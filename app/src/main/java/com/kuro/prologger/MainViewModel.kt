package com.kuro.prologger

import androidx.lifecycle.ViewModel
import com.kuro.prologger.domain.usecase.ForegroundServiceUseCase
import com.kuro.prologger.domain.usecase.OverlayPermissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val overLayPermissionUseCase: OverlayPermissionUseCase,
    private val foregroundServiceUseCase: ForegroundServiceUseCase
) : ViewModel() {

    private fun isOverlayPermissionGranted(): Boolean = overLayPermissionUseCase()

    fun isForegroundServicePermissionGranted(): Boolean = foregroundServiceUseCase()

    fun isPermissionGranted() : Boolean = isOverlayPermissionGranted() && isForegroundServicePermissionGranted()
}