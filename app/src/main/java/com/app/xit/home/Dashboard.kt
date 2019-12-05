package com.app.xit.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.xit.R
import com.app.xit.utill.AppConstants
import kotlinx.android.synthetic.main.fragmemt_dashboard.*
import kotlinx.android.synthetic.main.fragmemt_dashboard.view.*
import kotlinx.android.synthetic.main.fragmemt_dashboard.view.tv_banking

class Dashboard : Fragment(){

    lateinit var tvProfile: TextView
    lateinit var tvVehicleInformation: TextView
    lateinit var tvBookingHistory: TextView
    lateinit var tvBanking: TextView
    lateinit var tvDriver: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragmemt_dashboard, null)

        initialize(view)
        return view
    }

    fun initialize(view: View){

        tvProfile = view.tv_profile
        tvVehicleInformation = view.tv_vehicle_infor
        tvBookingHistory = view.tv_history
        tvBanking = view.tv_banking
        tvDriver = view.tv_driver

        tvProfile.setOnClickListener {
            (requireActivity() as HomeActivity).onPageChange(AppConstants.profilePage)
        }

        tvVehicleInformation.setOnClickListener {
            (requireActivity() as HomeActivity).onPageChange(AppConstants.vehicleInformationPage)
        }

        tvBookingHistory.setOnClickListener {
            (requireActivity() as HomeActivity).onPageChange(AppConstants.bookingHistoryPage)
        }

        tvBanking.setOnClickListener {
            (requireActivity() as HomeActivity).onPageChange(AppConstants.bankingDetail)
        }

        tvDriver.setOnClickListener {
            (requireActivity() as HomeActivity).onPageChange(AppConstants.homePage)
        }
    }
}