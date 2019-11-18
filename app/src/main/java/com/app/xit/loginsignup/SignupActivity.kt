package com.app.xit.loginsignup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.xit.AppPrefs
import com.app.xit.BaseActivity
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.utill.AppConstants
import com.app.xit.utill.AppUtill
import com.app.xit.utill.HitApi
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import java.io.FileDescriptor
import java.io.IOException
import java.lang.Exception

class SignupActivity : BaseActivity(){

    private val TAG: String = "Signup Activity"
    var counter : Int = 0
    lateinit var name:String
    lateinit var email:String
    lateinit var phone:String
    lateinit var password:String
    lateinit var confirmPassword:String
    lateinit var street:String
    lateinit var streetNo:String
    lateinit var unitNo:String
    lateinit var city:String
    lateinit var state:String
    lateinit var vehicleYear:String
    lateinit var vehicleMake:String
    lateinit var vehicleModel:String

    private val REQUEST_DRIVING_LIC: Int = 42
    private val REQUEST_SOCIAL_SECURITY: Int = 52
    private val REQUEST_VEHICLE_LICENSE: Int = 62
    private val REQUEST_VEHICLE_REG: Int = 72
    private val REQUEST_VEHICLE_INSURANCE: Int = 82

    private val CAMERA_REQUEST_CODE: Int = 1092

    lateinit var drivingLicBase64: String
    lateinit var socialSecurityBase64: String
    lateinit var vehicleLicenseBase64: String
    lateinit var vehicleRegBase64: String
    lateinit var vehicleInsuranceBase64: String
    var requestField: Int = 0
    lateinit var imageUri: Uri

    lateinit var progressBar: ProgressBar

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_signup)

        progressBar = findViewById(R.id.progressBar)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutPersonal.visibility = View.VISIBLE
        layoutAddress.visibility = View.GONE
        layoutVehicleDetail.visibility = View.GONE

        setChooseView()
        btnNext.setOnClickListener {
            when(counter){
                0 -> {
                    if(validate(counter)) {
                        layoutPersonal.visibility = View.GONE
                        layoutAddress.visibility = View.VISIBLE
                        counter++
                    }
                }
                1 -> {
                    if(validate(counter)) {
                        counter++
                        layoutAddress.visibility = View.GONE
                        layoutVehicleDetail.visibility = View.VISIBLE
                        btnNext.setText("SIGN UP")
                    }
                }
                2 -> {
                    signup()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
//    self.name_text.text,@"name",self.email_text.text,@"email",
//    self.mobile_text.text,@"mobile_no",
//    self.pass_text.text,@"password", self.unitNo_text.text, @"unit_no",
//    self.streetnio_text.text,@"street_no", self.steeetName_txt.text, @"street_name" ,
//    self.city_txt.text,@"city", self.state_txt.text, @"state"
//    ,self.zipcode_txt.text,@"zipcode", _vecMake_txt.text, @"vehicle_make",
//    self.vehMoel_txt.text,@"vehicle_model", dLString, @"driving_license",
//    socialSecurityString,@"social_security_card", registrationString, @"vehicle_registration",
//    plateString,@"vehicle_license_plate", InsuranceString, @"vehicle_insurance_card",deviceTokenStr,@"access_token"
    fun signup(){
        if(validate(counter)){
//            var map= mutableMapOf<String, String>()
            var map = JSONObject()
            map.put("name", name)
            map.put("email", email)
            map.put("mobile_no", phone)
            map.put("password", password)
            map.put("unit_no", unitNo)
            map.put("street_no", streetNo)
            map.put("street_name", street)
            map.put("city", city)
            map.put("state", state)
            map.put("vehicle_make", vehicleMake)
            map.put("vehicle_model", vehicleModel)
            map.put("driving_license", drivingLicBase64)
            map.put("social_security_card", socialSecurityBase64)
            map.put("vehicle_registration", vehicleRegBase64)
            map.put("vehicle_license_plate", vehicleLicenseBase64)
            map.put("vehicle_insurance_card", vehicleInsuranceBase64)
            if(!TextUtils.isEmpty(AppPrefs.getFcmToken())) {
                map.put("deviceTokenStr", AppPrefs.getFcmToken())
            }else {
                map.put("deviceTokenStr", "FCM TOKEN NOT FOUND")
            }
            progressBar.visibility = View.VISIBLE
            HitApi.hitPostJsonRequest(this, AppConstants.driverRegisteration, map, object : ServerResponse {
                override fun success(data: String) {
                    super.success(data)
                    progressBar.visibility = View.GONE
                    val json = JSONObject(data).optJSONObject("data")

                    AppConstants.driverLoginModel = LoginModel(json.optString("driver_id"), json.optString("name"), json.optString("business_id"))
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                override fun error(e: Exception) {
                    super.error(e)
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "ERROR: $e")
                }

            })
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun validate(counter: Int): Boolean{
        if(counter == 0) {
            name = etName?.text.toString().trim()
            email = etEmail?.text.toString().trim()
            phone = etMobile?.text.toString().trim()
            password = etPassword?.text.toString().trim()
            confirmPassword = etConfirmPassword?.text.toString().trim()

            if(TextUtils.isEmpty(name) || name.length < 2)
            {
                etName?.error = "Name can't be empty or less than 2 character"
                return false
            }
            if(TextUtils.isEmpty(email) || email.length < 5){
                etEmail.error = "Not a valid email"
                return false
            }
            if(TextUtils.isEmpty(phone)){
                etMobile.error = "Mobile Number is mandatory"
                return false
            }
            if(phone.length < 10){
                etMobile.error = "Not a valid Mobile Number"
                return false
            }
            if(TextUtils.isEmpty(password) || !password.equals(confirmPassword)){
                etPassword.error = "Empty Password or Password mismatch"
                return false
            }

            return true
        }
        if(counter == 1) {
            street = etStreetName?.text.toString().trim()
            streetNo = etStreetNo?.text.toString().trim()
            unitNo = etUnitnum?.text.toString().trim()
            city = etCity?.text.toString().trim()
            state = etState?.text.toString().trim()

            if(TextUtils.isEmpty(street)){
                etMobile.error = "Street is mandatory"
                return false
            }
            if(TextUtils.isEmpty(streetNo)){
                etStreetNo.error = "streetNo is mandatory"
                return false
            }

            if(TextUtils.isEmpty(unitNo)){
                etMobile.error = "Unit number is mandatory"
                return false
            }

            if(TextUtils.isEmpty(city)){
                etMobile.error = "City is mandatory"
                return false
            }

            if(TextUtils.isEmpty(state)){
                etMobile.error = "State is mandatory"
                return false
            }

            return true
        }

        if(counter == 2) {
//            drivingLicense = etDrivingLicense?.text.toString().trim()
//            socialSecurityCard = etSocialSecurity?.text.toString().trim()
            vehicleYear = etVehicleYear?.text.toString().trim()
            vehicleMake = etVehicleMake?.text.toString().trim()
            vehicleModel = etVehicleModel?.text.toString().trim()
//            vehicleRegistration = etVehicleRegistration?.text.toString().trim()
//            vehicleInsurance = etVehicleInsurance?.text.toString().trim()
            if(TextUtils.isEmpty(vehicleYear)){
                etVehicleYear.error = "Year is mandatory"
                return false
            }
            if(TextUtils.isEmpty(vehicleMake)){
                etVehicleMake.error = "Mandatory field"
                return false
            }
            if(TextUtils.isEmpty(vehicleModel)){
                etVehicleModel.error = "Model is mandatory"
                return false
            }

            if(TextUtils.isEmpty(drivingLicBase64)){
                etDrivingLicense.error = "Select Image"
                return false
            }

            if(TextUtils.isEmpty(socialSecurityBase64)){
                etSocialSecurity.error = "Select Image"
                return false
            }

            if(TextUtils.isEmpty(vehicleLicenseBase64)){
                etVehicleLicense.error = "Select Image"
                return false
            }

            if(TextUtils.isEmpty(vehicleRegBase64)){
                etVehicleRegistration.error = "Select Image"
                return false
            }

            if(TextUtils.isEmpty(vehicleInsuranceBase64)){
                etVehicleInsurance.error = "Select Image"
                return false
            }

        }
            return true
    }

    fun setChooseView(){
        etDrivingLicense.setOnClickListener{
            performFileSearch(REQUEST_DRIVING_LIC)
        }

        etSocialSecurity.setOnClickListener{
            performFileSearch(REQUEST_SOCIAL_SECURITY)
        }

        etVehicleLicense.setOnClickListener{
            performFileSearch(REQUEST_VEHICLE_LICENSE)
        }

        etVehicleRegistration.setOnClickListener{
            performFileSearch(REQUEST_VEHICLE_REG)
        }
        etVehicleInsurance.setOnClickListener{
            performFileSearch(REQUEST_VEHICLE_INSURANCE)
        }
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    fun performFileSearch(requestCode : Int) {
        requestField = requestCode
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as a
            // file (as opposed to a list of contacts or timezones)
            addCategory(Intent.CATEGORY_OPENABLE)

            // Filter to show only images, using the image MIME data type.
            // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
            // To search for all documents available via installed storage providers,
            // it would be "*/*".
            type = "image/*"
        }

        startActivityForResult(intent, requestCode)
//        showPictureDialog()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

   /*    if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM)
        {
            if (resultData != null)
            {
                val contentURI = resultData!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
                    Toast.makeText(this@SignupActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
//                    imageview!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@SignupActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == REQUEST_TAKE_PHOTO)
        {
            val thumbnail = resultData!!.extras!!.get("data") as Bitmap
//            imageview!!.setImageBitmap(thumbnail)
//            saveImage(thumbnail)
            Toast.makeText(this@SignupActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }*/

        if (/*requestCode == REQUEST_DRIVING_LIC &&*/ resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.also { uri ->
                Log.i(TAG, "Uri: $uri")
                try {
//                    imageUri = uri
                   ImageLoader(requestCode, uri).execute()
                }catch (ex: Exception){
                    ex.printStackTrace()
                }

            }
        }
    }



    fun setImageInData(requestField : Int, base64image: String){
//        val base64image = AppUtill.bitmapToBase64(bitmap)
        when(requestField){
            REQUEST_DRIVING_LIC -> {
                drivingLicBase64 = base64image
                etDrivingLicense.setText("image selected")
            }
            REQUEST_SOCIAL_SECURITY -> {
                socialSecurityBase64 = base64image
                etSocialSecurity.setText("image selected")
            }
            REQUEST_VEHICLE_LICENSE -> {
                vehicleLicenseBase64 = base64image
                etVehicleLicense.setText("image selected")
            }
            REQUEST_VEHICLE_REG -> {
                vehicleRegBase64 = base64image
                etVehicleRegistration.setText("image selected")
            }
            REQUEST_VEHICLE_INSURANCE -> {
                vehicleInsuranceBase64 = base64image
                etVehicleInsurance.setText("image selected")
            }
        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
    fun takePhoto() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
            return
        }
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> selectImageInAlbum()
                1 -> takePhoto()
            }
        }
        pictureDialog.show()
    }

    @Throws(IOException::class)
    fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        var image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()

        var width = image.width / 2
        var height = image.height / 2
        image = Bitmap.createScaledBitmap(image, width, height, true)

        return image
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_REQUEST_CODE){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePhoto()
            }
        }
    }

    inner class ImageLoader(var requestCode: Int, var bitmapUri: Uri) : AsyncTask<Void, Void, String>() {

//        lateinit var progressBar: ProgressBar

        override fun doInBackground(vararg params: Void?): String? {

            val bitmap = getBitmapFromUri(bitmapUri)
            val base64image = AppUtill.bitmapToBase64(bitmap)
            return  base64image
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            setImageInData(requestCode, result as String)
            progressBar.visibility = View.GONE
        }
    }
}