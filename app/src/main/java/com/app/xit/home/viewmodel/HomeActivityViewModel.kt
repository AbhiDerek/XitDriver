package com.app.xit.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.xit.location.BookingData
import com.app.xit.location.BookingSingleton

class HomeActivityViewModel: ViewModel() {


       var locData: MutableLiveData<BookingData> = MutableLiveData<BookingData>()
        //        var locData2: MutableLiveData<BookingData> by lazy { MutableLiveData<BookingData>() }

        var bookingSingleton = MutableLiveData<BookingSingleton>()

        fun getLocData(): LiveData<BookingData> {
            return locData
        }

        fun getBookingSingleton():LiveData<BookingSingleton>{
            return bookingSingleton
        }


}