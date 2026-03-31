package com.ignaherner.pawcare

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ignaherner.pawcare.data.local.WorkManagerSyncManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PawCareApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManagerSyncManager: WorkManagerSyncManager

    override fun onCreate() {
        super.onCreate()
        kotlinx.coroutines.MainScope().launch {
            workManagerSyncManager.syncWorkers()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}