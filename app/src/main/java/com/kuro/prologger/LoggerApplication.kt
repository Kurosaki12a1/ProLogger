package com.kuro.prologger

import android.app.Application
import com.kuro.prologger.util.Utils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoggerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.setUpSize(this)
    }
}