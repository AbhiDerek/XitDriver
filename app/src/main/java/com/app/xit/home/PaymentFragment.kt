package com.app.xit.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.location.Distance
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import kotlinx.android.synthetic.main.fragment_payment.*
import org.json.JSONObject

class PaymentFragment : Fragment(), View.OnClickListener{

    companion object{
        val TAG = "Payment"
    }

    private var distance: Float =0.0f
    private var duration: Long =0L
    private lateinit var progressBar : ProgressBar
    private lateinit var tvAmt: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_payment, null)
        progressBar = view.findViewById(R.id.progressBar)
        distance = AppPrefs.getDistance() / 10000
        duration = AppPrefs.getDuration() / 86400

        (view.findViewById(R.id.tv_booking_id) as TextView).setText(AppPrefs.getBookingId())
        (view.findViewById(R.id.tv_time) as TextView).setText("Time :   "+duration.toString())
        (view.findViewById(R.id.tv_distance) as TextView).setText("Distance :   "+distance.toString())
        (view.findViewById(R.id.btn_close) as Button).setOnClickListener(this)
        tvAmt = view.findViewById(R.id.tv_amt)
        getPaymentDetail()

        return view
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btn_close){
            AppPrefs.setBookingStatus(HomeFragment.JOURNEY_STARTED)

            AppPrefs.setBookingId("")
            AppPrefs.setDropAdress("")
            AppPrefs.setPickupAdress("")
            AppPrefs.setDropLatitude("")
            AppPrefs.setDropLongitude("")
            AppPrefs.setPickupLatitude("")
            AppPrefs.setPickupLongitude("")
            AppPrefs.setDistance(0F)
            AppPrefs.setDuration(0L)
            (requireActivity() as HomeActivity).replaceFragment(HomeFragment())
        }
    }

    fun getPaymentDetail(){
        var map = JSONObject()
        map.put("time", duration)
        map.put("mils", distance)
        map.put("drid", AppPrefs.getDriverId())

        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.paymentDetail, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
                    Log.i(TAG, "BOOKING COMPLETE RESPONSE : $data")
                    tvAmt.setText(JSONObject(data).optString("data"))
                    /* val json: JSONObject = JSONObject(data).optJSONObject("data")
                     var gson = Gson()
                     val driverModel : DriverModel = gson.fromJson(json.toString(), DriverModel::class.java)
                     AppConstants.driverDetailModel = driverModel
                     setData()*/
                }

            }

            override fun error(e: Exception) {
                super.error(e)
                progressBar.visibility = View.GONE
                Log.e(TAG, "ERROR: $e")
            }

        })
    }
}