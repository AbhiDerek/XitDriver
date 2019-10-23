package com.app.xit.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.google.gson.Gson
import org.json.JSONObject

class BankDetailFragment : Fragment(){

    companion object{
        val TAG = "BankDetailFragment"
    }

    lateinit var progressBar: ProgressBar
    lateinit var tvBankName: TextView
    lateinit var tvAccountNumber: TextView
    lateinit var tvRouting: TextView


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bank_info_row, null)
        progressBar = view.findViewById(R.id.progressBar)
        tvBankName = view.findViewById(R.id.tv_bank_name)
        tvAccountNumber = view.findViewById(R.id.tv_account_num)
        tvRouting = view.findViewById(R.id.tv_rout_number)

        fetchDriverProfile()


        return view
    }


    fun setData(){
        tvBankName.text = AppConstants.driverDetailModel.bank_name
        tvAccountNumber.text = AppConstants.driverDetailModel.ac_no
        tvRouting.text = AppConstants.driverDetailModel.routing_no
    }



    fun fetchDriverProfile(){
//        var map= mutableMapOf<String, String>()
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.driverProfile, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
                    val json: JSONObject = JSONObject(data).optJSONObject("data")
                    var gson = Gson()
                    val driverModel : DriverModel = gson.fromJson(json.toString(), DriverModel::class.java)
                    AppConstants.driverDetailModel = driverModel
                    setData()
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