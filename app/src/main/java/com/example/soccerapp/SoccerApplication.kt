package com.example.soccerapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.soccerapp.worker.MatchRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject



@HiltAndroidApp//hiltの開始地点。メインより前に起動する土台
class SoccerApplication : Application() , Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            PeriodicWorkRequestBuilder<MatchRefreshWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "match_refresh",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

}