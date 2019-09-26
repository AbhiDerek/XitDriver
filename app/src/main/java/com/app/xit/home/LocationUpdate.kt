package com.app.xit.home

import android.location.Location

interface LocationUpdate {
    fun getCurrentLocation():Location?
    fun getCurrentLatitude():Double?
    fun getCurrentLongitude():Double?
}