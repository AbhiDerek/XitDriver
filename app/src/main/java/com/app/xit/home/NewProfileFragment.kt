package com.app.xit.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.BuildConfig
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.json.JSONObject

class NewProfileFragment : Fragment(){

    companion object{
        val TAG = "ProfileFragment"
    }

    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvDrivingLicense: TextView
    private lateinit var imgDriver: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var linearVehicle: LinearLayout
    private lateinit var linearProfile: LinearLayout

    private lateinit var tvVehicleType: TextView
    private lateinit var tvVehicleMake: TextView
    private lateinit var tvVehicleModel: TextView
    private lateinit var imgVehicleRegistration: ImageView
    private lateinit var imgVehicleImageLicense: ImageView
    private lateinit var tvVehicleInsuranceCard: TextView
    private lateinit var imgVehicleInsuranceCard: ImageView
    private lateinit var imgDrivingLicense: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile_new, null)

        imgDriver = view.findViewById(R.id.profile_image)
        tvName = view.findViewById(R.id.tv_driver_name)
        tvAddress = view.findViewById(R.id.tv_driver_address)
        tvPhone = view.findViewById(R.id.tv_driver_phone)
        tvDrivingLicense = view.findViewById(R.id.tv_driver_license)
        progressBar = view.findViewById(R.id.progressBar)
        linearProfile = view.findViewById(R.id.linear_profile)
        linearVehicle = view.findViewById(R.id.linear_vehicle)

        tvVehicleType = view.findViewById(R.id.tv_vehicle_type)
        tvVehicleMake = view.findViewById(R.id.tv_vehicle_make)
        tvVehicleModel = view.findViewById(R.id.tv_vehicle_model)
        tvVehicleInsuranceCard = view.findViewById(R.id.tv_vehicle_insurance_card)
        imgVehicleRegistration = view.findViewById(R.id.img_vehicle_registration)
        imgVehicleImageLicense = view.findViewById(R.id.img_vehicle_lic_plate)
        imgVehicleInsuranceCard = view.findViewById(R.id.img_vehicle_ins_card)
        imgDrivingLicense = view.findViewById(R.id.img_driving_license)

        if(arguments?.getBoolean("IS_PROFILE")!!){
            linearProfile.visibility = View.VISIBLE
            linearVehicle.visibility = View.GONE
        }else{
            linearProfile.visibility = View.GONE
            linearVehicle.visibility = View.VISIBLE
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchDriverProfile()
    }



    fun fetchDriverProfile(){
//        var map= mutableMapOf<String, String>()
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.driverProfile, map, object :
            ServerResponse {
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
                Log.e(ProfileFragment.TAG, "ERROR: $e")
            }

        })
    }


    private fun setData(){
        val data = AppConstants.driverDetailModel

        tvName.text = data.name
        tvAddress.text = data.street_no +" "+ data.street_name + " "+ data.city
        tvDrivingLicense.text = data.dl_no
        tvPhone.text = data.mobile_no

        Glide.with(requireActivity())
            .load(BuildConfig.base_image_url + data.user_photo)
            .fitCenter().into(imgDriver)

        Glide.with(requireActivity())
            .load(BuildConfig.base_image_url + data.dl_photourl)
            .fitCenter().into(imgDrivingLicense)


        tvVehicleType.text = data.driver_type
        tvVehicleMake.text = data.vehicle_make
        tvVehicleModel.text = data.vehicle_model
        tvVehicleInsuranceCard.text = data.vehicle_insurence_no

        Glide.with(requireActivity())
            .load(BuildConfig.base_image_url + data.vehicle_insurence_img)
            .fitCenter().into(imgVehicleInsuranceCard)

        Glide.with(requireActivity())
            .load(BuildConfig.base_image_url + data.vehicle_registration_img)
            .fitCenter().into(imgVehicleRegistration)

        Glide.with(requireActivity())
            .load(BuildConfig.base_image_url + data.vehicle_license_img)
            .fitCenter().into(imgVehicleImageLicense)

    }

}