package com.app.xit

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.xit.home.HomeActivity
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.google.android.gms.location.*
import org.json.JSONObject

class GetCurrentLocationService(): Service(){

    private lateinit var locationCallback: LocationCallback
    val CHANNEL_DEFAULT_IMPORTANCE = "xit_location_service"
    var ONGOING_NOTIFICATION_ID: Int = 1
    var CHANEL_ID: String = " "
    val TAG = "GetCurrentLoac"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANEL_ID = createNotificationChannel(channelId = CHANEL_ID, channelName = CHANNEL_DEFAULT_IMPORTANCE)
        }
        sendNotification(CHANEL_ID)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.i(TAG, "Location Callback ${location?.latitude}, ${location?.longitude}")

                    AppPrefs.setCurrentLatitude(location?.latitude as Double)
                    AppPrefs.setCurrentLongitude(location?.longitude as Double)

                    changeBookingStatus()

                }
            }
        }
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
//                Log.i(TAG, "Last Location ${location?.latitude}, ${location?.longitude}")
            }

    }

    private fun changeBookingStatus(){
        if(TextUtils.isEmpty(AppPrefs.getDriverId())){
            return
        }
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        map.put("latlong", AppPrefs.getCurrentLatitude() +","+ AppPrefs.getCurrentLongitude())

        HitApi.hitPostJsonRequest(this, AppConstants.driverLocationUpdate, map, object :
            ServerResponse {

            override fun success(t: String) {
                super.success(t)

            }

            override fun error(e: Exception) {
                super.error(e)
            }

        })
    }

    fun createLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 50000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    fun sendNotification(channelId: String){
        val pendingIntent: PendingIntent =
            Intent(this, HomeActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
//                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.fetch_loc))
                .setSmallIcon(R.drawable.small_notification_icon)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .build()
        } else {
            Notification.Builder(this)
//                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.fetch_loc))
                .setSmallIcon(R.drawable.small_notification_icon)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .build()
        }

        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }


}