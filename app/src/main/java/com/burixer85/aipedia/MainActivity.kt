package com.burixer85.aipedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.burixer85.aipedia.navigation.NavigationHost
import com.burixer85.aipedia.ui.theme.AIpediaTheme
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import androidx.activity.result.contract.ActivityResultContracts
import com.burixer85.aipedia.core.util.UpdateConfig

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var appUpdateManager: AppUpdateManager

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            // Immediate update cancelled or failed — close app to enforce the update requirement
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.rgb(0x1C, 0x20, 0x26)
            )
        )
        setContent {
            AIpediaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHost(navHostController = rememberNavController())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkForUpdate()
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (isFinishing || isDestroyed) return@addOnSuccessListener
            val priority = info.updatePriority()
            val available = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val allowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            if (available && priority >= UpdateConfig.MIN_UPDATE_PRIORITY && allowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }
}
