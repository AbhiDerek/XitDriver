package com.app.xit.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.location.Polyline
import com.app.xit.location.RouteData
import com.app.xit.location.Steps
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.AppUtill
import com.app.xit.utill.HitApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_home_map.view.*
import org.json.JSONObject


class HomeFragment : Fragment(), OnMapReadyCallback {

    val TAG = "HomeFragment"
    lateinit var location: Location
    lateinit var locationUpdate: LocationUpdate
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var currentAddress: String
    lateinit var supportMapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var fab: FloatingActionButton
    lateinit var layout_booking_address: LinearLayout
    lateinit var btn_status_change: Button

    private val MY_PERMISSIONS_REQUEST_LOCATION : Int = 2109
    var zoomLevel: Float = 11.0f
    lateinit var marker: Marker
    var bookinId: String? = null
    var pickLat: String? = null
    var pickLong: String? = null
    var pickAddress: String? = null
    var routeUrl: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        locationUpdate = requireContext() as LocationUpdate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home_map, null)

        layout_booking_address = view.findViewById(R.id.layout_booking_address)
        btn_status_change = view.btn_status_change

        fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            latitude = AppPrefs.getCurrentLatitude().toDouble()
            longitude = AppPrefs.getCurrentLongitude().toDouble()
            val address = AppUtill.getAddressFromLatLong(requireContext(), latitude = latitude, longitude = longitude)
            if(TextUtils.isEmpty(address)) {
                Snackbar.make(view, "Searching Current Location... ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                Snackbar.make(view, "Current Location : $address", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                setCurrentLocation(address)
            }
            //Route to agra
            routeUrl = getURL(LatLng(latitude, longitude), LatLng(28.72153,77.463759))
            getRoute(routeUrl)
        }
        supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        checkBookingStatus()

        return view
    }

    private fun checkBookingStatus(){
        bookinId = arguments?.getString("booking_id")
        pickLat = arguments?.getString("pick_lat")
        pickLong = arguments?.getString("pick_long")
        pickAddress = arguments?.getString("pick_address")

        if(!TextUtils.isEmpty(bookinId) && !TextUtils.isEmpty(pickLat) && !TextUtils.isEmpty(pickLong) && !TextUtils.isEmpty(pickAddress)){
            layout_booking_address.visibility = View.VISIBLE
            btn_status_change.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        location = Location(locationUpdate.getCurrentLocation())
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map as GoogleMap
        setCurrentLocation()

        if(!TextUtils.isEmpty(AppPrefs.getCurrentLatitude())) {
            latitude = AppPrefs.getCurrentLatitude().toDouble()
            longitude = AppPrefs.getCurrentLongitude().toDouble()
        }
    }

    fun setMapData(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION)
            return
        }
        googleMap.isMyLocationEnabled = true

    }

    fun setCurrentLocation(address: String = ""){
        currentAddress = address
        if(latitude != 0.0 || longitude != 0.0){
            val latlng = LatLng(latitude, longitude)
            if(::googleMap.isInitialized) {
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoomLevel))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel))

                if (::marker.isInitialized) {
                    marker.position = latlng
                } else {
                    marker = googleMap.addMarker(MarkerOptions().position(latlng))
                }
                if (!TextUtils.isEmpty(address)) {
                    marker.title = address
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setMapData()
            }
        }
    }


    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val key = "key="+ resources.getString(R.string.google_api_key)
        val params = "$origin&$dest&$sensor&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }


    private fun getRoute(routeUrl: String?){
//        Log.i(TAG, "ROUTE URL: $routeUrl")
        HitApi.hitGetRequest(requireContext(), routeUrl!!, object : ServerResponse{

            override fun success(t: String) {
                super.success(t)
//                Log.i(TAG, "ROUTE DATA : $t")
//                setPolyline()

                var gson = Gson()
                var pointsList = arrayListOf<String?>()
                var routeModel = gson.fromJson(t, RouteData::class.java)
                lateinit var polyline: Polyline
                var stepsList = arrayListOf<Steps?>()

                routeModel.routes?.forEach {
                    it.legs?.forEach {
                        it.steps?.forEach {
                            stepsList.add(it)
                        }
                    }
                }

                val points = stepsList.map {
                    it?.polyline?.points!!
                }

                val polypts = points.flatMap {
                    decodePoly(it)
                }

                val options = PolylineOptions()
                options.color(Color.parseColor("#0D4210"))

                options.width(5f)
                options.add(LatLng(latitude, longitude))
                polypts.forEach {
                    options.add(it)
                }

                options.add(LatLng(28.72153,77.463759))

                if(::googleMap.isInitialized){
                    googleMap.addPolyline(options)
                }
            }

            override fun error(e: Exception) {
                super.error(e)
                Log.i(TAG, "ROUTE DATA : $e")
            }
        })
    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }



}