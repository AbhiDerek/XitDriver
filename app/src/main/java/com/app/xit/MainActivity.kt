package com.app.xit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.xit.home.HomeActivity
import com.app.xit.loginsignup.LoginActivity

class MainActivity : BaseActivity() {

    val SPLASH_CODE : Int = 1212
    val LOGIN_CODE : Int = 1215

    lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout(R.layout.activity_splash)

        startActivityForResult(Intent(this, SplashActivity::class.java), SPLASH_CODE)
        handler = Handler()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SPLASH_CODE){
            if(AppPrefs.isLogin()){
                startHomeActivity()
            }else{
                startActivityForResult(Intent(this, LoginActivity::class.java), LOGIN_CODE)
            }
        }

        if(requestCode == LOGIN_CODE){
            if(resultCode == Activity.RESULT_CANCELED){
                finish()
            }else if(resultCode == Activity.RESULT_OK) {
                startHomeActivity()
            }
        }
    }

    val runnableCode = object: Runnable {
        override fun run() {
            backPress = false
        }
    }

    fun startHomeActivity(){
        startActivity(Intent(this, HomeActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }


    override fun onBackPressed() {
        if(backPress)
            super.onBackPressed()
        else{
            backPress = true
            Toast.makeText(this@MainActivity, "Press again to exit", Toast.LENGTH_SHORT).show()
            handler.postDelayed(runnableCode, 2000)
        }
    }
}
