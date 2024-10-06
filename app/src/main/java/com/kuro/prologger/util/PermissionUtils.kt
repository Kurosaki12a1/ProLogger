package com.kuro.prologger.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment.isExternalStorageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

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

    LaunchedEffect(allPermissionsGranted) {
        if (allPermissionsGranted) {
            // All permissions are already granted
            val grantedPermissionsMap = permissions.associateWith { true }
            onPermissionsResult(grantedPermissionsMap)
        } else {
            // Launch the permissions request
            permissionsLauncher.launch(permissions.toTypedArray())
        }
    }
}

@Composable
fun RequestExternalStoragePermission(context: Context, onPermissionGranted: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        onPermissionGranted.invoke()
        return
    }

    val manageStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (isExternalStorageManager()) {
                onPermissionGranted.invoke()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (isExternalStorageManager()) {
            onPermissionGranted.invoke()
        } else {
            // See Splash Screen a bit before requesting permission
            delay(500L)
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${context.packageName}")
            manageStoragePermissionLauncher.launch(intent)
        }
    }
}

fun requestOverlayPermission(activity: Activity, code: Int) {
    if (!hasOverlayPermission(activity)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, code)
    }
}

fun hasOverlayPermission(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun requestExternalStorage(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
        }
    }
}