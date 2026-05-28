package com.burixer85.aipedia.core.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus

class InAppUpdateManager(context: Context) {
    private val appUpdateManager: AppUpdateManager =
        com.google.android.play.core.appupdate.AppUpdateManagerFactory.create(context)

    fun checkForUpdates(
        activity: Activity,
        onUpdateAvailable: (AppUpdateInfo) -> Unit,
        onUpdateNotAvailable: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable =
                appUpdateInfo.updateAvailability() == com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE

            if (isUpdateAvailable && (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) || appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))) {
                onUpdateAvailable(appUpdateInfo)
            } else {
                onUpdateNotAvailable()
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    }

    fun startImmediateUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo) {
        val updateType = if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            AppUpdateType.FLEXIBLE
        } else {
            AppUpdateType.IMMEDIATE
        }

        val options = AppUpdateOptions.newBuilder(updateType)
            .setAllowAssetPackDeletion(true)
            .build()

        appUpdateManager.startUpdateFlow(appUpdateInfo, activity, options)
    }

    fun registerInstallStateListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.registerListener(listener)
    }

    fun unregisterInstallStateListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.unregisterListener(listener)
    }
}
