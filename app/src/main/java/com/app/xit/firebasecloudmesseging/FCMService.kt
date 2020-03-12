package com.app.xit.firebasecloudmesseging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FCMService: FirebaseMessagingService(){

    companion object {
        private const val TAG = "FCMService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG, "From: ${remoteMessage.from}")
        Log.i(TAG, "Data: ${remoteMessage.data}")

        Log.i(TAG, "Notification extra: ${remoteMessage.toIntent().extras}")
        val bundle = remoteMessage.toIntent().extras as Bundle
        val data = bundle["gcm.notification.mydata"]

        Log.i(TAG, "Notification data: ${data}")

        val dt = JSONObject(bundle["gcm.notification.mydata"].toString())
        val data2 = JSONObject(dt["data"].toString())

        val orderNum = data2["order_number"].toString()
        val city = data2["p_city"].toString()
        val zip = data2["d_zipcode"].toString()
        val id = data2["id1"].toString()

        AppPrefs.setid1(id)

        val passengerName = data2["p_contact_name"].toString()
        val passengerEmail = data2["p_email"].toString()
        val passengerPhone = data2["p_phone"].toString()
        val passengerAddress = data2["p_address"].toString()
        val passengerCity = data2["p_city"].toString()
        val passengerState = data2["p_state"].toString()
        val passengerCountry = data2["p_country"].toString()
        val passengerZip = data2["p_zipcode"].toString()

        val totalAmount = data2["total_amount"].toString()
        val paymentMode = data2["payment_mode"].toString()
        val dateTime = data2["date_and_time"].toString()

        val drName = data2["d_contact_name"].toString()
        val drPhone = data2["d_phone"].toString()
        val drEmail = data2["d_email"].toString()
        val drAddress = data2["d_address"].toString()
        val drCity = data2["d_city"].toString()
        val drState = data2["d_state"].toString()
        val drCountry = data2["d_country"].toString()
        val drZip = data2["d_zipcode"].toString()

        val notifyIntent = Intent(HomeActivity.BROADCAST_ACTION).apply {
            putExtra("pick_address", passengerAddress)

            putExtra("drop_address", drAddress)
//            putExtra("drop_address", "Veer Savarkar Block, Block S 2, Upadhyay Block, Shakarpur Khas, Delhi, 110092")
            putExtra("pay_mode", paymentMode)

            putExtra("is_passenger_request", true)
            putExtra("booking_id", orderNum)
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