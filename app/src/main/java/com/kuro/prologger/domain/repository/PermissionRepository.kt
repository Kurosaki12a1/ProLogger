package com.kuro.prologger.domain.repository

interface PermissionRepository {
    fun isStoragePermissionGranted(): Boolean

    fun isFloatingServicePermissionGranted(): Boolean

    fun isNotificationPermissionGranted(): Boolean
}