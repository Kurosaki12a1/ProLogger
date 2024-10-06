package com.kuro.prologger.domain.usecase

import com.kuro.prologger.domain.repository.PermissionRepository
import javax.inject.Inject

class ForegroundServiceUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {
    operator fun invoke(): Boolean = permissionRepository.isNotificationPermissionGranted()
}