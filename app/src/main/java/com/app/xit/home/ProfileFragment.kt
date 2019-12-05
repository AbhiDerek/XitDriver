package com.app.xit.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.BuildConfig
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.loginsignup.SignupActivity
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.AppUtill
import com.app.xit.utill.HitApi
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.io.FileDescriptor
import java.io.IOException

class ProfileFragment : Fragment(){

    companion object{
        val TAG = "ProfileFragment"
    }

    private lateinit var etFullName : EditText
    private lateinit var etFullAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDrivingLicense: EditText
    private lateinit var etvehicleMark: EditText
    private lateinit var etVehileModel: EditText
    private lateinit var etInsuranceNos: EditText

    private lateinit var tv_driver_profile: TextView
    private lateinit var tv_vehicle_information: TextView

    private lateinit var imgDriver: ImageView
//    lateinit var imgSocialSecurityCard: ImageView
    private lateinit var imgDrivingLicense: ImageView
    private lateinit var imgVehicleRegister: ImageView
    private lateinit var imgVehicleLicensePlate: ImageView
    private lateinit var imgVehicleInsuranceCard: ImageView
    private lateinit var linearVehicle: LinearLayout
    private lateinit var linearPersonal: LinearLayout

    private lateinit var progressBar: ProgressBar

    private lateinit var btnNext: Button

    lateinit var fullName: String
    lateinit var fullAddress: String
    lateinit var phone: String
    lateinit var email: String
    lateinit var licenseNos: String
    lateinit var vehicleMark: String
    lateinit var vehicleModel: String
    lateinit var insuranceNos: String

    private val REQUEST_PROFILE_PIC: Int = 22
    private val REQUEST_SOCIAL_SECURITY: Int = 32
    private val REQUEST_DRIVING_LICENSE: Int = 42
    private val REQUEST_VEHICLE_REG: Int = 52
    private val REQUEST_VEHICLE_LICENSE: Int = 62
    private val REQUEST_VEHICLE_INSURANCE: Int = 72

    lateinit var driverProfileBase64: String
    lateinit var socialSecurityBase64: String
    lateinit var drivingLicenseBase64: String
    lateinit var vehicleRegisterBase64: String
    lateinit var vehicleLicensePlateBase64: String
    lateinit var vehicleInsuranceCardBase64: String


    private val REQUEST_TAKE_PHOTO = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, null)

        etFullName = view.findViewById(R.id.etFullName)
        etFullAddress = view.findViewById(R.id.etFullAddress)
        etPhone = view.findViewById(R.id.etPhone)
        etEmail = view.findViewById(R.id.etEmail)
        etDrivingLicense = view.findViewById(R.id.etDrivingLicense)
        etvehicleMark = view.findViewById(R.id.etvehicleMark)
        etVehileModel = view.findViewById(R.id.etVehileModel)
        tv_driver_profile = view.findViewById(R.id.tv_driver_profile)
        tv_vehicle_information = view.findViewById(R.id.tv_vehicle_information)
        imgDriver = view.findViewById(R.id.img_driver)
//        imgSocialSecurityCard = view.findViewById(R.id.imgSocialSecurityCard)
        imgDrivingLicense = view.findViewById(R.id.imgDrivingLic)
        imgVehicleRegister = view.findViewById(R.id.imgVehicleRegister)
        imgVehicleLicensePlate = view.findViewById(R.id.imgVehicleLicensePlate)
        imgVehicleInsuranceCard = view.findViewById(R.id.imgVehicleInsuranceCard)
        linearVehicle = view.findViewById(R.id.linear_Vehicle)
        linearPersonal = view.findViewById(R.id.linear_Personal)
        etInsuranceNos = view.findViewById(R.id.etInsuranceNos)

        if(arguments?.getBoolean("IS_PROFILE")!!){
            linearPersonal.visibility = View.VISIBLE
            linearVehicle.visibility = View.GONE
        }else{
            linearPersonal.visibility = View.GONE
            linearVehicle.visibility = View.VISIBLE
        }

        progressBar = view.findViewById(R.id.progressBar)

        btnNext = view.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            updateUserProfile()
        }

        setEditable(false)

        tv_driver_profile.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= tv_driver_profile.right - tv_driver_profile.totalPaddingRight){
                        setEditable(true)
                        btnNext.visibility = View.VISIBLE
                    }
                }
                return false
            }

        })

        tv_vehicle_information.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if(event?.action == MotionEvent.ACTION_DOWN){
                    if(event.getRawX() >= tv_driver_profile.right - tv_driver_profile.totalPaddingRight){
                        setEditable(true)
                        btnNext.visibility = View.VISIBLE
                    }
                }
                return false
            }

        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchDriverProfile()
    }


    fun setEditable(boolean: Boolean){
        etFullName.isEnabled = boolean
        etFullAddress.isEnabled = boolean
        etPhone.isEnabled = boolean
        etEmail.isEnabled = boolean
        etDrivingLicense.isEnabled = boolean
        etvehicleMark.isEnabled = boolean
        etVehileModel.isEnabled = boolean
        etInsuranceNos.isEnabled = boolean

        if(boolean) {
          /*  imgSocialSecurityCard.setOnClickListener {
                performFileSearch(REQUEST_SOCIAL_SECURITY)
//                showPictureDialog(REQUEST_SOCIAL_SECURITY)
            }*/
            imgDriver.setOnClickListener{
                performFileSearch(REQUEST_PROFILE_PIC)
            }
            imgDrivingLicense.setOnClickListener{
                showPictureDialog(REQUEST_DRIVING_LICENSE)
            }

            imgVehicleRegister.setOnClickListener {
                performFileSearch(REQUEST_VEHICLE_REG)
//                showPictureDialog(REQUEST_VEHICLE_REG)
            }
            imgVehicleLicensePlate.setOnClickListener {
                performFileSearch(REQUEST_VEHICLE_LICENSE)
//                showPictureDialog(REQUEST_VEHICLE_LICENSE)
            }
            imgVehicleInsuranceCard.setOnClickListener {
                performFileSearch(REQUEST_VEHICLE_INSURANCE)
//                showPictureDialog(REQUEST_VEHICLE_INSURANCE)
            }
        }
    }

    private fun showPictureDialog(requestCode : Int) {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> performFileSearch(requestCode)
                1 -> takePhoto(requestCode)
            }
        }
        pictureDialog.show()
    }

    fun takePhoto(requestCode : Int) {
        val permission = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode)
            return
        }
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent1, requestCode)
        }
    }

    fun performFileSearch(requestCode : Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        startActivityForResult(intent, requestCode)
    }

    fun setData(){
        val data = AppConstants.driverDetailModel
        etFullName.setText(data.name)
        etFullAddress.setText(data.street_no +" "+ data.street_name + " "+ data.city)
        etPhone.setText(data.mobile_no)
        etEmail.setText(data.email)
        etDrivingLicense.setText(data.dl_no)
        etvehicleMark.setText(data.vehicle_make)
        etVehileModel.setText(data.vehicle_model)
        etInsuranceNos.setText(data.vehicle_insurence_no)

        if(isAdded) {
            Glide.with(requireActivity())
                    .load(BuildConfig.base_image_url + data.user_photo)
                    .fitCenter().into(imgDriver)

        /*    Glide.with(requireActivity())
                    .load(BuildConfig.base_image_url + data.social_security_card)
                    .fitCenter().into(imgSocialSecurityCard)*/

            Glide.with(requireActivity())
                    .load(BuildConfig.base_image_url + data.social_security_card)
                    .fitCenter().into(imgDrivingLicense)

            Glide.with(requireActivity())
                    .load(BuildConfig.base_image_url + data.vehicle_registration_img)
                    .fitCenter().into(imgVehicleRegister)

            Glide.with(this)
                    .load(BuildConfig.base_image_url + data.vehicle_license_img)
                    .fitCenter().into(imgVehicleLicensePlate)

            Glide.with(requireActivity())
                    .load(BuildConfig.base_image_url + data.vehicle_insurence_img)
                    .fitCenter().into(imgVehicleInsuranceCard)
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) && permissions[0].equals(Manifest.permission.CAMERA)) {
                takePhoto(requestCode)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Log.i(TAG, "Uri: $uri")
                try {
//                    imageUri = uri
                    ImageLoader(requestCode, uri).execute()
                }catch (ex: java.lang.Exception){
                    ex.printStackTrace()
                }

            }
        }
    }


    fun setImageInData(requestField : Int, base64image: String, bitmap: Bitmap){
//        val base64image = AppUtill.bitmapToBase64(bitmap)
        when(requestField){
            REQUEST_SOCIAL_SECURITY -> {
               /* socialSecurityBase64 = base64image
                imgSocialSecurityCard.setImageBitmap(bitmap)*/
            }
            REQUEST_PROFILE_PIC -> {
                driverProfileBase64 = base64image
                imgDriver.setImageBitmap(bitmap)
                updateDriverImage(driverProfileBase64)
            }
            REQUEST_DRIVING_LICENSE -> {
                drivingLicenseBase64 = base64image
                imgDrivingLicense.setImageBitmap(bitmap)
            }
            REQUEST_VEHICLE_REG -> {
                vehicleRegisterBase64 = base64image
                imgVehicleRegister.setImageBitmap(bitmap)
            }
            REQUEST_VEHICLE_LICENSE -> {
                vehicleLicensePlateBase64 = base64image
                imgVehicleLicensePlate.setImageBitmap(bitmap)
            }
            REQUEST_VEHICLE_INSURANCE -> {
                vehicleInsuranceCardBase64 = base64image
                imgVehicleInsuranceCard.setImageBitmap(bitmap)
            }
        }
    }


    @Throws(IOException::class)
    fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = requireContext().contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        var image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()

        var width = image.width / 2
        var height = image.height / 2
        image = Bitmap.createScaledBitmap(image, width, height, true)

        return image
    }


    inner class ImageLoader(var requestCode: Int, var bitmapUri: Uri) : AsyncTask<Void, Void, String>() {

        lateinit var bitmap: Bitmap

        override fun doInBackground(vararg params: Void?): String? {

            bitmap = getBitmapFromUri(bitmapUri)
            val base64image = AppUtill.bitmapToBase64(bitmap)
            return  base64image
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setImageInData(requestCode, result as String, bitmap)
            progressBar.visibility = View.GONE
        }
    }


    fun updateUserProfile(){
        fullName = etFullName.text.toString()
        fullAddress = etFullAddress.text.toString()
        email = etEmail.text.toString()
        licenseNos = etDrivingLicense.text.toString()
        phone = etPhone.text.toString()
        vehicleMark = etvehicleMark.text.toString()
        vehicleModel = etVehileModel.text.toString()
        insuranceNos = etInsuranceNos.text.toString()

        var map = JSONObject()
        map.put("name", fullName)
        map.put("mobile_no", phone)
//        map.put("unit_no", fullAddress)
//        map.put("street_no", fullAddress)
//        map.put("street_name", fullAddress)
//        map.put("city", fullAddress)
//        map.put("state", fullAddress)
//        map.put("zipcode", fullAddress)
        map.put("vehicle_make", vehicleMark)
        map.put("vehicle_model", vehicleModel)
        map.put("driving_license", licenseNos)
//        map.put("social_security_card", socialSecurityBase64)
        if(::vehicleRegisterBase64.isInitialized) {
            map.put("vehicle_registration", vehicleRegisterBase64)
        }
        if(::vehicleLicensePlateBase64.isInitialized) {
            map.put("vehicle_license_plate", vehicleLicensePlateBase64)
        }
        if(::vehicleInsuranceCardBase64.isInitialized) {
            map.put("vehicle_insurance_card", vehicleInsuranceCardBase64)
        }
        map.put("driver_id", AppPrefs.getDriverId())

        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.driverProfileUpdate, map, object : ServerResponse {
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

    fun updateDriverImage(driverImage: String){
        var map = JSONObject()
        map.put("user_photo", driverImage)
        map.put("driver_id", AppPrefs.getDriverId())

        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.driverProfilePicUpdate, map, object : ServerResponse {
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