package com.kuro.prologger

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.kuro.prologger.extension.isServiceRunning
import com.kuro.prologger.framework.LogService
import com.kuro.prologger.navigation.util.AppNavigator
import com.kuro.prologger.presentation.App
import com.kuro.prologger.presentation.theme.ProLoggerTheme
import com.kuro.prologger.util.requestOverlayPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_OVERLAY_PERMISSION = 100
    }

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigator: AppNavigator

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            requestOverlayPermission(this, REQUEST_CODE_OVERLAY_PERMISSION)
        } else {
            Toast.makeText(this, getString(R.string.need_request_permission), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProLoggerTheme {
                App(
                    appNavigator = navigator,
                    onFloatingButtonClick = { onFloatingButtonClick() }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startService()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.need_overlay),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestMultiplePermissions(
        isServicePermissionGranted: Boolean
    ) {
        if (isServicePermissionGranted) {
            requestOverlayPermission(this, REQUEST_CODE_OVERLAY_PERMISSION)
            return
        }

        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        } else {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }

        if (permissions.isNotEmpty()) {
            multiplePermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            requestOverlayPermission(this, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }

    private fun onFloatingButtonClick() {
        if (!isServiceRunning(LogService::class.java)) {
            if (viewModel.isPermissionGranted()) {
                startService()
            } else {
                requestMultiplePermissions(
                    isServicePermissionGranted = viewModel.isForegroundServicePermissionGranted()
                )
            }
        } else {
            stopService(Intent(this, LogService::class.java))
        }
    }

    private fun startService() {
        val intent = Intent(this, LogService::class.java)
        startForegroundService(intent)
    }
}