package com.incentives.prototype

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.incentives.prototype.minigameT1.MinigameT1

class Repository(private val context: Application) {

    private val _actionData = SingleLiveEvent<ActionData>()
    val actionData: LiveData<ActionData> = _actionData

    fun sendAction(actionString: String, parameters: Map<String, Any?> = emptyMap()) {
        _actionData.value = ActionData(actionString, parameters)
    }

    fun showNotificationForActivity(title: String, content: String) {
        val notificationIntent = Intent(context, MinigameT1::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private val NOTIFICATION_ID = 2
        @Volatile private var instance: Repository? = null

        fun getInstance(context: Application): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository(context).also { instance = it }
            }
        }
    }
}