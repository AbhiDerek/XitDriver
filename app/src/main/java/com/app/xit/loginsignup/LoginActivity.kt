package com.app.xit.loginsignup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import com.app.xit.AppPrefs
import com.app.xit.BaseActivity
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.w3c.dom.Text

class LoginActivity : BaseActivity(){

    val TAG : String = "LoginActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_login)


        buttonSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }


    public fun login(view: View){
        var email = textInputLayoutEmail.text.toString()
//        if(validateEmail() && validatePassword()){
//            var map= mutableMapOf<String, String>()
        email = "siraj@wserve.com"
        progressBar.visibility = View.VISIBLE
           var map = JSONObject()
            map.put("email", email)
            map.put("password", "test123")
            if(!TextUtils.isEmpty(AppPrefs.getFcmToken())) {
                map.put("access_token", AppPrefs.getFcmToken())
            }else {
                map.put("access_token", "FCM TOKEN NOT FOUND")
            }

            HitApi.hitPostJsonRequest(this, AppConstants.driverLogin, map, object : ServerResponse{
                override fun success(data: String) {
                    super.success(data)
                    progressBar.visibility = View.GONE
                    val json = JSONObject(data).optJSONObject("data")
                    AppConstants.driverLoginModel = LoginModel(json.optString("driver_id"), json.optString("name"), json.optString("business_id"))
                    AppPrefs.setLogin(true)
                    AppPrefs.setDriverId(json.optString("driver_id"))
                    AppPrefs.setDriverEmail(email)
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                override fun error(e: Exception) {
                    super.error(e)
                    Log.e(TAG, "ERROR: $e")
                    progressBar.visibility = View.GONE
                }

            })
//        }
    }

    private fun validateEmail(email: String): Boolean{
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true
    }

    private fun validatePassword(): Boolean{
        val password = textInputLayoutPassword.text.toString()
        if(TextUtils.isEmpty(password) || password.length < 4){
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


}
