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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.adapter.HistoryAdapter
import com.app.xit.userprofile.DriverHistoryModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

class HistoryFragment : Fragment(){

    lateinit var tvMsg: TextView
    lateinit var progressBar: ProgressBar
    lateinit var recycler: RecyclerView
    var driverHistoy: MutableList<DriverHistoryModel> = ArrayList()
    lateinit var adapter: HistoryAdapter
    val TAG = HistoryFragment::class.java.simpleName

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, null)
        tvMsg = view.findViewById(R.id.tv_msg)
        progressBar = view.findViewById(R.id.progressBar)
        recycler = view.findViewById(R.id.recycler)
//        itemDecor = new DividerItemDecoration(getContext, HORIZONTAL)

        fetchDriverHistory()
        return view
    }

    override fun onResume() {
        super.onResume()

    }


    fun fetchDriverHistory(){
//        var map= mutableMapOf<String, String>()
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        progressBar.visibility = View.VISIBLE

        HitApi.hitPostJsonRequest(requireContext(), AppConstants.driverOrderHistory, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val response = JSONObject(data)
                val success = response.optString("success")
                if(success.equals("1")) {
                    val jsonArray: JSONArray? = response.optJSONArray("data")
                    if(jsonArray != null && jsonArray.length() > 0) {
//                        var gson = Gson()
                        val gson = GsonBuilder().create()
                        driverHistoy= gson.fromJson(jsonArray.toString(), Array<DriverHistoryModel>::class.java).toMutableList()

                        Log.i(TAG, "SIZE : ${driverHistoy.size}")

                        recycler.apply {
                            layoutManager = LinearLayoutManager(activity)
                            adapter = HistoryAdapter(driverHistoy)
                        }

                      /*  val driverModel: DriverModel = gson.fromJson(json?.toString(), DriverHistoryModel::class.java)?
                        AppConstants.driverDetailModel = driverModel*/
                    }else{
                        tvMsg.visibility = View.VISIBLE
                        tvMsg.setText("History Not Available")
                    }
                }

            }

            override fun error(e: Exception) {
                super.error(e)
                progressBar.visibility = View.GONE
                Log.e(ProfileFragment.TAG, "ERROR: $e")
            }

        })
    }


}