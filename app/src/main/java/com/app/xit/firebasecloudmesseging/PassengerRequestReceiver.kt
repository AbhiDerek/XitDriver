package com.app.xit.firebasecloudmesseging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.xit.home.HomeActivity

class PassengerRequestReceiver : BroadcastReceiver(){

    val TAG = "PassengerReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val booking_id =intent?.getStringExtra("booking_id")
        val pickAddress =intent?.getStringExtra("pick_address")
        val dropAddress =intent?.getStringExtra("drop_address")
        val payMode =intent?.getStringExtra("pay_mode")
        val passengerRequest =intent?.getBooleanExtra("is_passenger_request", false)

        val acceptRequest = intent?.getBooleanExtra("accept_request", false)
        Log.i(TAG, "Booking id $booking_id")

        val homeIntent = Intent(context, HomeActivity::class.java).apply {
            putExtra("booking_id", booking_id)
            putExtra("accept_request", acceptRequest)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context?.startActivity(homeIntent)
    }

}