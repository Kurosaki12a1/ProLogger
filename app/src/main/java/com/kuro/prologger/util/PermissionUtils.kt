package com.kuro.prologger.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestSinglePermission(
    permission: String,
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val currentOnPermissionResult = rememberUpdatedState(onPermissionResult)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        currentOnPermissionResult.value(isGranted)
    }

    // Check if the permission is already granted
    val isGranted =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
        // Permission is already granted
        onPermissionResult(true)
    } else {
        // Launch the permission request
        permissionLauncher.launch(permission)
    }
}

@Composable
fun RequestMultiplePermissions(
    permissions: List<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit
) {
    val context = LocalContext.current
    val currentOnPermissionsResult = rememberUpdatedState(onPermissionsResult)

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        currentOnPermissionsResult.value(result)
    }

    // Check if all permissions are granted
    val allPermissionsGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    if (allPermissionsGranted) {
        // All permissions are already granted
        val grantedPermissionsMap = permissions.associateWith { true }
        onPermissionsResult(grantedPermissionsMap)
    } else {
        // Launch the permissions request
        permissionsLauncher.launch(permissions.toTypedArray())
    }
}

@Composable
fun RequestOverlayPermission() {
    val context = LocalContext.current

    if (!hasOverlayPermission(context)) {
        requestOverlayPermission(context)
    }
}

fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun requestOverlayPermission(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    context.startActivity(intent)
}