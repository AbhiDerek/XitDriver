package com.app.xit

import android.content.Context
import android.content.SharedPreferences

object AppPrefs {

    private lateinit var sharedPreferences: SharedPreferences
    private val loginKey = "is_login"
    private val driverId = "driver_id"
    private val id1 = "id1"
    private val driverEmail = "driver_email"
    private val latitude = "current_latitude"
    private val longitude = "current_longitude"
    private val fcmToken = "fcm_token"
    private val bookingId = "booking_id"
    private val pickupLatitude = "pickup_latitude"
    private val pickupLongitude = "pickup_longitude"
    private val pickupAdress = "pickup_address"
    private val dropLatitude = "drop_latitude"
    private val dropLongitude = "drop_longitude"
    private val dropAdress = "drop_address"
    private val bookingStatus = "booking_status"
    private val isJourneyStarted= "journey_started"


    fun defaultPrefs(context: Context): SharedPreferences{
        sharedPreferences = context.getSharedPreferences("XitApp", Context.MODE_PRIVATE)
        return sharedPreferences
    }

    inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    fun setDuration(time: Long){
        if(time == 0L){
            sharedPreferences.edit().putLong(getBookingId() + "_time", 0L).apply()
        }else {
            val calculateTime = time + getDuration()
            sharedPreferences.edit().putLong(getBookingId() + "_time", calculateTime).apply()
        }
    }

    fun getDuration(): Long{
        return sharedPreferences.getLong(getBookingId()+"_time", 0)
    }

    fun setDistance(distance: Float){
        if(distance == 0.0f){
            sharedPreferences.edit().putFloat(getBookingId() + "_distance", 0.0f).apply()
        }else {
            val calculateDistance = distance + getDistance()
            sharedPreferences.edit().putFloat(getBookingId() + "_distance", calculateDistance).apply()
        }
    }

    fun getDistance(): Float{
        return sharedPreferences.getFloat(getBookingId()+"_distance", 0.0f)
    }

    fun setBearing(bearing: Float){
        sharedPreferences.edit().putFloat(getBookingId()+"_bearing", bearing).apply()
    }

    fun getBearing(): Float{
        return sharedPreferences.getFloat(getBookingId()+"_bearing", 0.0f)
    }

    fun isLogin(): Boolean{
        return sharedPreferences.getBoolean(loginKey, false)
    }

    fun setLogin(flag: Boolean){
        sharedPreferences.edit().putBoolean(loginKey, flag).apply()
    }

    fun getDriverId(): String{
        return sharedPreferences.getString(driverId, "") as String
    }

    fun setDriverId(id: String){
        sharedPreferences.edit().putString(driverId, id).apply()
    }

    fun getId1(): String{
        return sharedPreferences.getString(id1, "") as String
    }

    fun setid1(id: String){
        sharedPreferences.edit().putString(id1, id).apply()
    }

    fun getDriverEmail(): String{
        return sharedPreferences.getString(driverEmail, "") as String
    }

    fun setDriverEmail(email: String){
        sharedPreferences.edit().putString(driverEmail, email).apply()
    }

    fun setCurrentLatitude(lat: Double){
        sharedPreferences.edit().putString(latitude , lat.toString()).apply()
    }
    fun getCurrentLatitude(): String{
        return sharedPreferences.getString(latitude, "") as String
    }

    fun setCurrentLongitude(lat: Double){
        sharedPreferences.edit().putString(longitude , lat.toString()).apply()
    }
    fun getCurrentLongitude (): String{
        return sharedPreferences.getString(longitude , "") as String
    }

    fun setfcmToken(token: String){
        sharedPreferences.edit().putString(fcmToken , token).apply()
    }
    fun getFcmToken (): String{
        return sharedPreferences.getString(fcmToken , "") as String
    }

    fun setBookingId(booking: String?){
        sharedPreferences.edit().putString(bookingId , booking).apply()
    }
    fun getBookingId(): String{
        return sharedPreferences.getString(bookingId , "") as String
    }

    fun setPickupLatitude(pickLat: String?){
        sharedPreferences.edit().putString(pickupLatitude, pickLat).apply()
    }
    fun getPickupLatitude(): String{
        return sharedPreferences.getString(pickupLatitude , "") as String
    }
    fun setPickupLongitude(pickLong: String?){
        sharedPreferences.edit().putString(pickupLongitude, pickLong).apply()
    }
    fun getPickupLongitude(): String{
        return sharedPreferences.getString(pickupLongitude , "") as String
    }
    fun setDropLatitude(dropLat: String?){
        sharedPreferences.edit().putString(dropLatitude, dropLat).apply()
    }
    fun getDropLatitude(): String{
        return sharedPreferences.getString(dropLatitude , "") as String
    }
    fun setDropLongitude(dropLong: String?){
        sharedPreferences.edit().putString(dropLongitude, dropLong).apply()
    }
    fun getDropLongitude(): String{
        return sharedPreferences.getString(dropLongitude , "") as String
    }
    fun setPickupAdress(address: String?){
        sharedPreferences.edit().putString(pickupAdress, address).apply()
    }
    fun getPickupAddress(): String{
        return sharedPreferences.getString(pickupAdress , "") as String
    }
    fun setDropAdress(address: String?){
        sharedPreferences.edit().putString(dropAdress, address).apply()
    }
    fun getDropAddress(): String{
        return sharedPreferences.getString(dropAdress , "") as String
    }
    fun setBookingStatus(status: String?){
        sharedPreferences.edit().putString(bookingStatus, status).apply()
    }
    fun getBookingStatus(): String{
        return sharedPreferences.getString(bookingStatus , "") as String
    }
    fun setIsJourneyStarted(journeyStarted: String?){
        sharedPreferences.edit().putString(isJourneyStarted, journeyStarted).apply()
    }
    fun getIsJourneyStarted(): String{
        return sharedPreferences.getString(isJourneyStarted , "") as String
    }

    fun bookingReset(){
        setBookingId("")
        setBookingStatus("")
        setIsJourneyStarted("")
        setPickupLatitude("")
        setPickupLongitude("")
        setPickupAdress("")
        setDropLatitude("")
        setDropLongitude("")
        setDropAdress("")
    }

}