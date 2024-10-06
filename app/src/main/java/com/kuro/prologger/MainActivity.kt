package com.kuro.prologger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kuro.prologger.navigation.util.AppNavigator
import com.kuro.prologger.presentation.App
import com.kuro.prologger.presentation.theme.ProLoggerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: AppNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProLoggerTheme {
                App(navigator)
            }
        }
    }
}