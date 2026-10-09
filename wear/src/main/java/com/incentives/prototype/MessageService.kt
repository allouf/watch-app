package com.incentives.prototype

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.incentives.prototype.minigameT1.MinigameT1
import com.incentives.prototype.minigameT1.MyTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    //private lateinit var channelId: String
    private val notificationId = 1
    //private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private var currentProgressBarIndex = 0 // Index to keep track of which progress bar to depreciate

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        //startDepreciationTask()
    }

    /*private fun startDepreciationTask() {
        scheduler.scheduleAtFixedRate({
            val calendar = Calendar.getInstance()
            //val hour = calendar.get(Calendar.HOUR_OF_DAY)
            // Depreciate only between 6 AM and 11 PM
            //if (hour in 6..22) {
                MyTimer.vibrate(applicationContext)
                depreciateProgressBars()
            //}
        }, 1, 1, TimeUnit.HOURS)
    }*/

    private fun depreciateProgressBars() {
        val avatarProfile: AvatarProfile = loadAvatarProfile()

        if (avatarProfile.ProgressBars.isNotEmpty()) {
            val progressBar = avatarProfile.ProgressBars.getOrNull(currentProgressBarIndex)
            progressBar?.let {
                it.ProgressValue = (it.ProgressValue - 10.0f).coerceAtLeast(0.0f)
            }
            saveAvatarProfile(avatarProfile)

            // Increment the index for the next hour, and reset if it exceeds the number of progress bars
            currentProgressBarIndex = (currentProgressBarIndex + 1) % avatarProfile.ProgressBars.size
        }
    }

    private fun loadAvatarProfile(): AvatarProfile {
            return MyTimer.avatarProfile
    }

    private fun saveAvatarProfile(avatarProfile: AvatarProfile) {
        MyTimer.avatarProfile = avatarProfile
        MyTimer.updateAvatarProfile(applicationContext,avatarProfile)

        MyTimer.updateAvatarProfileListen(applicationContext, avatarProfile)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        /*when (intent?.action) {
            ACTION_SHOW_NOTIFICATION -> showBringToFrontNotification()
            // Handle other actions
        }*/
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, MinigameT1::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setContentTitle("MessageService")
            .setContentText("Service is running.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker("Status")
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification)
    }

    /*private fun startForegroundService() {
        val notificationIntent = Intent(this, MinigameT1::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("MessageService")
            .setContentText("Service is running.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker("Status")
            .setOngoing(true)
            .build()

        startForeground(notificationId, notification)
    }*/


    /*fun showBringToFrontNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an intent that will be wrapped in PendingIntent
        val intent = Intent(this, MinigameT1::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Create the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Minigame Active")
            .setContentText("Tap to return to the game.")
            .setSmallIcon(androidx.wear.R.drawable.notification_action_background) // Replace with your app's icon.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(notificationId, notification)
    }*/

    class MessageReceiverInternal : WearableListenerService() {
        override fun onMessageReceived(messageEvent: MessageEvent) {
            if (messageEvent.path == "/allouf_path") {

                val message = String(messageEvent.data)

                val messageIntent = Intent(this, MessageBroadcastReceiver::class.java)
                messageIntent.action = "com.incentives.prototype.MINIGAME"
                messageIntent.putExtra("message", message)
                sendBroadcast(messageIntent)

            } else {
                super.onMessageReceived(messageEvent)
            }
        }
        override fun onDataChanged(dataEvents: DataEventBuffer) {
            dataEvents.forEach { event ->
                when {
                    event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path?.equals("/avatar") == true -> {
                        val asset: Asset = DataMapItem.fromDataItem(event.dataItem).dataMap.getAsset("com.example.key.avatar")
                        // Coroutine to manage async work
                        CoroutineScope(Dispatchers.IO).launch {
                            // Replace this with the actual logic to process your asset
                            val success = processAsset(asset, 1)
                        }
                    }
                    event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path?.equals("/image") == true -> {
                        // Extract the Asset from the DataMap for the image path
                        val asset: Asset = DataMapItem.fromDataItem(event.dataItem).dataMap.getAsset("com.example.key.image")

                        // Coroutine to manage async work
                        CoroutineScope(Dispatchers.IO).launch {
                            // Replace this with the actual logic to process your asset
                            val success = processAsset(asset, 0)
                        }
                    }
                    event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path?.equals("/count") == true -> {
                        DataMapItem.fromDataItem(event.dataItem).dataMap.apply {
                            val counts = getStringArrayList("com.example.key.count")
                            updateCount(counts ?: arrayListOf())
                        }
                    }
                    event.type == DataEvent.TYPE_DELETED -> {
                        // Do something for the deletion event if needed
                    }
                }
            }
        }

        suspend fun processAsset(asset: Asset, type: Int): Boolean = withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val bitmapFuture = Wearable.getDataClient(applicationContext).getFdForAsset(asset)
                bitmapFuture.addOnSuccessListener { assetFd: DataClient.GetFdForAssetResponse ->
                    try {
                        BitmapFactory.decodeStream(assetFd.inputStream)?.let { bitmap ->
                            saveBitmapToFile(bitmap, type)
                            continuation.resume(true) // Resume and return true
                        } ?: run {
                            // Bitmap wasn’t processed correctly
                            continuation.resume(false)
                        }
                    } catch (e: Exception) {
                        Log.e("processAsset", "Error saving bitmap", e)
                        continuation.resume(false) // Resume and return false in case of exception
                    }
                }.addOnFailureListener {
                    // Directly encountering a failure
                    Log.e("processAsset", "Failed to get Asset File Descriptor")
                    continuation.resume(false)
                }
            }
        }

        private fun saveBitmapToFile(bitmap: Bitmap, type: Int) {
            var fixedFilename = ""
            when(type){
                0->{
                    fixedFilename = "image.png"
                }
                1->{
                    fixedFilename = "avatar.png"
                }
            }
            try {
                val stream: OutputStream = openFileOutput(fixedFilename, Context.MODE_PRIVATE)

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Compress and write bitmap as PNG

                stream.flush() // Always flush the stream
                stream.close() // Always close the stream

                // OPTIONAL: If you need to use the filePath elsewhere
                //val savedFilePath = getFileStreamPath(fixedFilename).absolutePath
                // Use `savedFilePath` as needed

                //Log.d("WearablePart", "Bitmap saved to $savedFilePath")
            } catch (e: IOException) {
                Log.e("WearablePart", "Error saving the image to internal storage", e)
            }
        }

        private fun updateCount(lst: java.util.ArrayList<String>) {
            MyTimer.selectedSyncNodesIds = lst
        }
    }
}
