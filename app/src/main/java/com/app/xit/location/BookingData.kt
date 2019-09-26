package com.app.xit.location

import android.location.Location

object BookingData {

    var location: Location
    set(value) {
        location = value
    }
    get() {
        return location
    }
    var latitude: Double
    set(value) {
        latitude = value
    }
    get() {
        return latitude
    }
    var longitude: Double
    set(value) {
        longitude = value
    }
    get() {
        return longitude
    }




}