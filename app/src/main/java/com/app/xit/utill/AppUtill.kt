package com.app.xit.utill

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


object AppUtill {

    val TAG = "AppUtill"
    fun bitmapToBase64(bitmap: Bitmap): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream .toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)

        return encoded
    }

    fun getAddressFromLatLong(context: Context, latitude: Double?, longitude: Double?): String{
        val geocoder = Geocoder(context, Locale.getDefault())
        val lat = latitude as Double
        val longi = longitude as Double
        var completeAddress: String = ""

        var addresses: List<Address> = emptyList()
        try {
            addresses = geocoder.getFromLocation(
                lat,
                longi,
                // In this sample, we get just a single address.
                1)
            if(!TextUtils.isEmpty(addresses.get(0).featureName)) {
                completeAddress = addresses.get(0).featureName
            }
            if(!TextUtils.isEmpty(addresses.get(0).subAdminArea)) {
                completeAddress = completeAddress + " " + addresses.get(0).subAdminArea
            }
            if(!TextUtils.isEmpty(addresses.get(0).subLocality)) {
                completeAddress = completeAddress + " " + addresses.get(0).subLocality
            }
            if(!TextUtils.isEmpty(addresses.get(0).postalCode)) {
                completeAddress = completeAddress + " " + addresses.get(0).postalCode
            }
            if(!TextUtils.isEmpty(addresses.get(0).adminArea)) {
                completeAddress = completeAddress + " " + addresses.get(0).adminArea
            }

            return completeAddress
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available)
//            Log.e(TAG, errorMessage, ioException)
            Log.e(TAG, ioException.message, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "${illegalArgumentException.message}. Latitude = $latitude , " +
                    "Longitude =  $longitude", illegalArgumentException)
        }

        return ""

    }

}