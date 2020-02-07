package com.app.xit.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.userprofile.DriverModel
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bank_info_row.view.*
import org.json.JSONObject




class BankDetailFragment : Fragment(){

    companion object{
        val TAG = "BankDetailFragment"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var tvBankName: TextView
    private lateinit var tvAccountNumber: TextView
    private lateinit var tvRouting: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnSaveUpdate: Button
    private lateinit var layoutDetail: LinearLayout
    private lateinit var layoutEdit: LinearLayout
    private lateinit var etBankName: EditText
    private lateinit var etAccountNum: EditText
    private lateinit var etRouting: EditText


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEdit = view.btnEdit
        btnSaveUpdate = view.btnUpdateBankDetail
        etBankName = view.et_edit_bank_name
        etAccountNum = view.et_edit_account_num
        etRouting = view.et_edit_routing_num
        layoutDetail = view.layout_banking_detail
        layoutEdit = view.layout_edit_banking_detail

        btnEdit.setOnClickListener {
            btnEdit.visibility = View.GONE
            layoutDetail.visibility = View.GONE
            layoutEdit.visibility = View.VISIBLE
        }

        btnSaveUpdate.setOnClickListener {
                updateBankingDetail()
        }

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


    fun updateBankingDetail(){
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        val bankName = etBankName.text.toString()
        val accountName = etAccountNum.text.toString()
        val routing = etRouting.text.toString()
        if(TextUtils.isEmpty(bankName))
        {
            Toast.makeText(requireContext(), "Bank name is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        if(TextUtils.isEmpty(accountName))
        {
            Toast.makeText(requireContext(), "Account number is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        if(TextUtils.isEmpty(routing))
        {
            Toast.makeText(requireContext(), "Routing is mandatory", Toast.LENGTH_SHORT).show()
            return
        }

        map.put("bank_name", bankName)
        map.put("ac_no", accountName)
        map.put("routing_no", routing)

        progressBar.visibility = View.VISIBLE

        HitApi.hitPostJsonRequest(requireContext(), AppConstants.updateBankDetail, map, object : ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
//                    val json: JSONObject = JSONObject(data).optJSONObject("data")
//                    var gson = Gson()
//                    val driverModel : DriverModel = gson.fromJson(json.toString(), DriverModel::class.java)
//                    AppConstants.driverDetailModel = driverModel
//                    setData()
                    btnEdit.visibility = View.VISIBLE
                    layoutDetail.visibility = View.VISIBLE
                    layoutEdit.visibility = View.GONE
                    fetchDriverProfile()

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