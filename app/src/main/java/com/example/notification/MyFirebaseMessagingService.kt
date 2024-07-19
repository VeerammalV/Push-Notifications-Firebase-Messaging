package com.example.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val tag = "MyFirebaseMsgService"
    private val notificationDao: NotificationDao by lazy {
        UserDatabase.getDatabase(applicationContext).notificationDao()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(tag, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            val title = it.title ?: ""
            val body = it.body ?: ""
            Log.d(tag, "Notification Title: $title")
            Log.d(tag, "Notification Body: $body")
            showNotification(title, body)
            saveNotification(title, body)

        }
    }

    private fun showNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            val channelId = getString(R.string.default_notification_channel_id)
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    private fun saveNotification(title: String, body: String) {
        Log.d(tag, "Saving notification to database")
        val notification = NotificationEntity(title = title, body = body, timestamp = formatTimestamp(System.currentTimeMillis()).toString())
        CoroutineScope(Dispatchers.IO).launch {
            notificationDao.insert(notification)
            NotificationRepository(notificationDao).addNotification(notification)
            val myIntent = Intent("FBR-MESSAGE")
            myIntent.putExtra("action", Constants.MESSAGE_RECEIVED)
            myIntent.putExtra("notification_body", body)
            sendBroadcast(myIntent)
        }

    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
