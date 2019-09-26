package com.app.xit.utill

import android.app.Activity
import android.location.Location
import com.google.android.gms.location.LocationServices

class LocationService {

    fun getLocationService(con: Activity){

        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(con)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
            }


    }
}