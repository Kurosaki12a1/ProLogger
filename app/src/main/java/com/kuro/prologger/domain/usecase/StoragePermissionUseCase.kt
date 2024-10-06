package com.kuro.prologger.domain.usecase

import com.kuro.prologger.domain.repository.PermissionRepository
import javax.inject.Inject

class StoragePermissionUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository
) {

    operator fun invoke(): Boolean = permissionRepository.isStoragePermissionGranted()
}