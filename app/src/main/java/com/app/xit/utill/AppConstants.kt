package com.app.xit.utill

import com.app.xit.BuildConfig
import com.app.xit.loginsignup.LoginModel
import com.app.xit.userprofile.DriverModel

object AppConstants{

    val baseUrl: String by lazy {
        BuildConfig.base_url
    }
    
    lateinit var driverLoginModel: LoginModel
    lateinit var driverDetailModel: DriverModel

    val driverResponse = baseUrl+"driver_response.php"
    val driverLocationUpdate = baseUrl+"driver_location_update.php"
    val driverOrderHistory = baseUrl+"driver_past_order.php"
    val driverProfileUpdate= baseUrl+"driver_details_update.php"
    val driverProfilePicUpdate= baseUrl+"driver_photo_update.php"
    val paymentDetail= baseUrl+"driver_payment_details.php"
    val amountDetails = baseUrl + "driver_amount_return.php"
    val driverProfile = baseUrl+"driver_dashboard.php"
    val driverRegisteration = baseUrl+"individual_registration.php"
    val driverLogin = baseUrl+"driver_login.php"
    val proofApi = "http://zipitx.com/rstapi/proof_delivery.php"
    val updateBankDetail = baseUrl+"driver_bankInfo_update.php"
    val reachToPickup = baseUrl+"picked_reach_response.php"
    val beginToDrop = baseUrl+"picked_response.php"

    val homePage = "Home Page"
    val profilePage = "Profile Page"
    val vehicleInformationPage = "Vehicle Information"
    val bookingHistoryPage = "Booking History"
    val bankingDetail = "Banking Detail"
    val bookingInformationPage = "Booking Information"
}

