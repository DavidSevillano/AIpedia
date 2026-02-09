package com.burixer85.aipedia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AIpediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}