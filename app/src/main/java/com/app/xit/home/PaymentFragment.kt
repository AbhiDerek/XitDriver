package com.app.xit.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import org.json.JSONObject

class PaymentFragment : Fragment(){

    companion object{
        val TAG = "Payment"
    }

    private lateinit var progressBar : ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_payment, null)
        progressBar = view.findViewById(R.id.progressBar)
        getPaymentDetail()

        return view
    }

    fun getPaymentDetail(){
        var map = JSONObject()
        map.put("time", AppPrefs.getDistance())
        map.put("mils", AppPrefs.getDuration())
        map.put("driver_id", AppPrefs.getDriverId())

        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.paymentDetail, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
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