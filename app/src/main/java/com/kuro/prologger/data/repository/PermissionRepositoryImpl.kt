package com.kuro.prologger.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.kuro.prologger.domain.repository.PermissionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionRepository {
    override fun isStoragePermissionGranted(): Boolean {
        val readStorageGranted = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val writeStorageGranted = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val manageStorageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }

        return readStorageGranted && writeStorageGranted && manageStorageGranted
    }

    override fun isFloatingServicePermissionGranted(): Boolean {
        return Settings.canDrawOverlays(context)
    }

    override fun isNotificationPermissionGranted(): Boolean {
        val foregroundServiceGranted =
            context.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED

        val postNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val foregroundServiceDataSyncGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                context.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        return foregroundServiceGranted && postNotificationGranted && foregroundServiceDataSyncGranted
    }
}