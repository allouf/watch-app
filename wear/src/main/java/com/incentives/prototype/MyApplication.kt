package com.incentives.prototype

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.incentives.prototype.minigameT1.MyTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    companion object {
        lateinit var CHANNEL_ID: String
        lateinit var instance: MyApplication private set
    }
    // Define a top-level application scope
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        instance = this
        CHANNEL_ID = "${packageName}.channel"
        createNotificationChannel()
        Repository.getInstance(this)
        scheduleDepreciationWork()
        MyTimer.initializeWithContext(applicationContext)
    }

    private fun scheduleDepreciationWork() {
        val depreciationWorkRequest = PeriodicWorkRequestBuilder<DepreciationWorker>(15, TimeUnit.MINUTES)
            // If you have constraints like network connectivity, battery not low, etc., add them here
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DepreciationWork",
            ExistingPeriodicWorkPolicy.KEEP, // Keep the existing periodic work, don't replace it.
            depreciationWorkRequest
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name) // Provide your channel name.
            val descriptionText = getString(R.string.channel_description) // Provide your channel description.
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}