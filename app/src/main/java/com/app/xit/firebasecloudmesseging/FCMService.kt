package com.app.xit.firebasecloudmesseging

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.XitApplication
import com.app.xit.home.HomeActivity
import com.app.xit.location.BookingData
import com.app.xit.location.BookingSingleton
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService: FirebaseMessagingService(){

    companion object {
        private const val TAG = "FCMService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data: ${remoteMessage.data}")
        Log.d(TAG, "MessageId: ${remoteMessage.messageId}")
        Log.d(TAG, "MessageType: ${remoteMessage.messageType}")

        sendNotification("Message")

        var lonitude = AppPrefs.getCurrentLongitude().toDouble()
        var latitude = AppPrefs.getCurrentLatitude().toDouble()
        val pick_loc = "Pick Address"
//        BookingSingleton.bookingData.latitude = latitude
//        BookingSingleton.bookingData.longitude = lonitude
        val dr_longitude = AppPrefs.getCurrentLongitude().toDouble()
        val dr_latitude = AppPrefs.getCurrentLatitude().toDouble()
        val dr_loca = "Drop Address"

        val notifyIntent = Intent(HomeActivity.BROADCAST_ACTION).apply {
            putExtra("pick_latitude", latitude)
            putExtra("pick_longitude", lonitude)
            putExtra("pick_address", pick_loc)

            putExtra("drop_latitude", dr_latitude)
            putExtra("drop_longitude", dr_longitude)
            putExtra("drop_address", dr_loca)

            putExtra("is_passenger_request", true)
            putExtra("booking_id", "B244")
        }

        sendBroadcast(notifyIntent)
        sendNotification("Passenger Request")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }


    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("is_passenger_request", true)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val acceptIntent = Intent(this, PassengerRequestReceiver::class.java).apply {
            putExtra("booking_id", "B23")
            putExtra("accept_request", true)
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(this, 1, acceptIntent, PendingIntent.FLAG_ONE_SHOT)


        val rejectIntent = Intent(this, PassengerRequestReceiver::class.java).apply {
            putExtra("booking_id", "B23")
            putExtra("accept_request", false)
        }
        val rejectPendingIntent = PendingIntent.getBroadcast(this, 2, rejectIntent , PendingIntent.FLAG_CANCEL_CURRENT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.small_notification_icon)
//            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.accept_img, "ACCEPT", acceptPendingIntent)
            .addAction(R.drawable.reject_img, "REJECT", rejectPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}