package com.app.xit.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.xit.*
import com.app.xit.home.viewmodel.HomeActivityViewModel
import com.app.xit.location.BookingData
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.android.synthetic.main.app_bar_home.*
import org.json.JSONObject

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationUpdate, PageNavigation {

    companion object{
        val BROADCAST_ACTION = "com.app.xit.location"
        val TAG = "HomeActivity"
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val MY_PERMISSIONS_REQUEST_LOCATION : Int = 2109
    private val PERMISSIONS_SERVICE_LOCATION : Int = 2119

    var location: Location? = Location("Xit")
    lateinit var currentFragment: String

    lateinit var receiver: MyReceiver
    var isBooking : Boolean? = false
    var acceptFlag : Boolean? = false
    var bookingId : String? = null

    var pickLatitude : Double = 0.0
    var pickLongitude : Double = 0.0
    var dropLatitude : Double = 0.0
    var dropLongitude : Double = 0.0
    var pickAddress: String? = null
    var dropAddress: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        startLocationService()

        if(AppPrefs.isLogin()){
            receiver = MyReceiver()
            val intent = IntentFilter(BROADCAST_ACTION)
            registerReceiver(receiver, intent)

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLocationUpdate()

            val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
            val navView: NavigationView = findViewById(R.id.nav_view)
            val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener(this)

            replaceFragment(HomeFragment())
        }else{
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })

            finish()
        }

    }

    fun startLocationService(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_SERVICE_LOCATION )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, GetCurrentLocationService::class.java))
        }else{
            startService(Intent(this, GetCurrentLocationService::class.java))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        isBooking = intent?.getBooleanExtra("is_passenger_request", false)
        if(isBooking as Boolean) {
            layoutRequest.visibility = View.VISIBLE
            btnAccept.setOnClickListener {
                driverBookingRespose(true)
            }

            btnReject.setOnClickListener {
                driverBookingRespose(false)
            }

            Handler().postDelayed(runnable, 45 * 1000)
        }
    }

    fun getLocationUpdate(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION)
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc : Location? ->
                // Got last known location. In some rare situations this can be null.
                this.location = loc
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                getLocationUpdate()
            }
        }

        if(requestCode == PERMISSIONS_SERVICE_LOCATION ){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService()
            }
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if(supportFragmentManager.backStackEntryCount == 1){
                finish()
            }else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::receiver.isInitialized) {
            unregisterReceiver(receiver)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPageChange(pageName: String) {
        when(pageName){
            AppConstants.homePage ->{
                replaceFragment(HomeFragment())
                supportActionBar?.setTitle("Home")
            }
            AppConstants.profilePage ->{
                supportActionBar?.setTitle("Profile")
                replaceFragment(ProfileFragment())
            }
            AppConstants.vehicleInformationPage -> {
                supportActionBar?.setTitle("Profile")
                replaceFragment(ProfileFragment())
            }
            AppConstants.bookingHistoryPage -> {
                supportActionBar?.setTitle("History")
                replaceFragment(HistoryFragment())
            }
            AppConstants.bookingInformationPage -> {

            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment())
                supportActionBar?.setTitle("Home")
            }
            R.id.nav_dashboard -> {
                supportActionBar?.setTitle("Dashboard")
                replaceFragment(Dashboard())
            }
            R.id.nav_logout -> {
                showConfirmationDialog()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this@HomeActivity)
        val message = "Do you want to logout?"
        builder.setMessage(message)
        val positiveText = "Yes"
        builder.setPositiveButton(positiveText
        ) { dialog, _ ->
            dialog?.dismiss()
            setLogout()
            val i = Intent(this@HomeActivity, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(i)
            finish()

        }

        val negativeText = "No"
        builder.setNegativeButton(negativeText
        ) { dialog, _ ->
            dialog?.dismiss()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    fun changeScreen(){}

    fun setLogout(){
        AppPrefs.setDriverId("")
        AppPrefs.setLogin(false)
        AppPrefs.setBookingId("")
    }

    fun replaceFragment(fragment: Fragment){
        val findFragment = supportFragmentManager.findFragmentByTag(fragment.javaClass.name)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        if(findFragment != null) {
//            supportFragmentManager.popBackStackImmediate(fragment.javaClass.name, 0)
            findFragment.arguments = fragment.arguments
            transaction.replace(R.id.container, findFragment)
            transaction.commit()

            currentFragment = findFragment.javaClass.name
        }else{
            transaction.replace(R.id.container, fragment)
                .addToBackStack(fragment.javaClass.name)
            transaction.commit()

            currentFragment = fragment.javaClass.name
        }
    }


    override fun getCurrentLocation(): Location? {
       return location
    }

    override fun getCurrentLatitude():Double?{
        return location?.latitude
    }

    override fun getCurrentLongitude():Double?{
        return location?.longitude
    }

    inner class MyReceiver: BroadcastReceiver(){

        override fun onReceive(con: Context?, intent: Intent?) {
            bookingId = intent?.getStringExtra("booking_id")
            isBooking = intent?.getBooleanExtra("is_passenger_request", false)
            acceptFlag = intent?.getBooleanExtra("accept_request", false)

            try{
                val pLat = intent?.getStringExtra("pick_latitude")
                val pLong = intent?.getStringExtra("pick_longitude")
                pickAddress = intent?.getStringExtra("pick_address")
                if(!TextUtils.isEmpty(pLat)) {
                    pickLatitude = pLat?.toDouble() as Double
                }
                if(!TextUtils.isEmpty(pLong)) {
                    pickLongitude = pLong!!. toDouble ()
                }
                AppPrefs.setPickupLatitude(pickLatitude.toString())
                AppPrefs.setPickupLongitude(pickLongitude.toString())
                AppPrefs.setPickupAdress(pickAddress)

                dropLatitude = intent?.getStringExtra("drop_latitude")!!.toDouble()
                dropLongitude = intent?.getStringExtra("drop_longitude")!!.toDouble()
                dropAddress = intent?.getStringExtra("drop_address")
                AppPrefs.setDropLatitude(dropLatitude.toString())
                AppPrefs.setDropLongitude(dropLongitude.toString())
                AppPrefs.setDropAdress(dropAddress)
            }catch (ex: Exception){
                ex.printStackTrace()
            }

            if(!(isBooking as Boolean)) {
                driverBookingRespose(acceptFlag as Boolean)
            }
            else if(isBooking as Boolean){
                onNewIntent(intent)
            }
        }
    }

    val runnable = Runnable {
        layoutRequest.visibility = View.GONE
    }


    private fun driverBookingRespose(flag: Boolean){
//        var map= mutableMapOf<String, String>()
//        https://websitexperts.co/demo/xit/rstapi/driver_response.php for receive or reject
//         order-idSt?ring,@"driver_id",orderString,@"order_id",statusString,@"status"
//         for accept status-2 or for rejected status-3

        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        map.put("order_id", AppPrefs.getDriverId())
        if(flag){
            map.put("status", "2")
        }else{
            map.put("status", "3")
        }
        progressBar.visibility = View.VISIBLE

        HitApi.hitPostJsonRequest(this, AppConstants.driverResponse, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                Handler().postDelayed(runnable, 1 * 1000)

                val homeFragment = HomeFragment()
                val bundle = Bundle().apply {
                    putString("booking_id", bookingId)
                    putString("pick_lat", bookingId)
                    putString("pick_long", bookingId)
                    putString("pick_address", bookingId)
                }

                homeFragment.arguments = bundle

                replaceFragment(homeFragment)
                AppPrefs.setBookingId(bookingId)

                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
                    val json: JSONObject = JSONObject(data).optJSONObject("data")
                    var gson = Gson()
//                    val driverModel : DriverModel = gson.fromJson(json.toString(), DriverModel::class.java)
//                    AppConstants.driverDetailModel = driverModel

                }

            }

            override fun error(e: Exception) {
                super.error(e)
                progressBar.visibility = View.GONE
                Log.e(ProfileFragment.TAG, "ERROR: $e")
                Handler().postDelayed(runnable, 2 * 1000)
            }

        })
    }

}
