package com.app.xit.home

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.xit.AppPrefs
import com.app.xit.R
import com.app.xit.ServerResponse
import com.app.xit.home.HomeActivity.Companion.SIGNATURE_REQUEST
import com.app.xit.home.signature.SignatureDialog
import com.app.xit.utill.AppConstants
import com.app.xit.utill.HitApi
import kotlinx.android.synthetic.main.fragment_proof.view.*
import org.json.JSONObject


class ProofFragment : Fragment(){

    private lateinit var mView: View
    private var imgBase64: String? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_proof, null)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.progressBar
        view.btn_signature.setOnClickListener {
            val intent = Intent(requireActivity(), SignatureDialog::class.java)
            startActivityForResult(intent, SIGNATURE_REQUEST)
        }

        view.btn_submit.setOnClickListener {
            proofSubmit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGNATURE_REQUEST && resultCode == Activity.RESULT_OK){
            imgBase64 = data?.getStringExtra(SignatureDialog.IMAGE_BASE_64)


            val decodedString = Base64.decode(imgBase64, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            mView.img_signature.setImageBitmap(decodedByte)

        }
    }

    private fun proofSubmit(){
        var map = JSONObject()
        map.put("driver_id", AppPrefs.getDriverId())
        if(!TextUtils.isEmpty(mView.et_proof_name.text.toString())) {
            map.put("proof_name", mView.et_proof_name.text.toString())
        }else{
            Toast.makeText(requireContext(), "User name cannot be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if(!TextUtils.isEmpty(mView.et_proof_date.text.toString())) {
             map.put("proof_date", mView.et_proof_date.text.toString())
        }else{
            Toast.makeText(requireContext(), "Date cannot be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if(!TextUtils.isEmpty(mView.et_proof_time.text.toString())) {
             map.put("proof_time", mView.et_proof_time.text.toString())
        }else{
            Toast.makeText(requireContext(), "Time cannot be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if(!TextUtils.isEmpty(imgBase64)) {
            map.put("proof_time", mView.et_proof_time.text.toString())
        }else{
            Toast.makeText(requireContext(), "User signature is not submitted", Toast.LENGTH_SHORT).show()
        }

        map.put("proof_attachment", imgBase64)

        progressBar.visibility = View.VISIBLE
        HitApi.hitPostJsonRequest(requireContext(), AppConstants.paymentDetail, map, object :
            ServerResponse {
            override fun success(data: String) {
                super.success(data)
                progressBar.visibility = View.GONE
                val success = JSONObject(data).optString("success")
                if(success.equals("1")) {
                    Log.i(PaymentFragment.TAG, "BOOKING COMPLETE RESPONSE : $data")
                    (requireActivity() as HomeActivity).replaceFragment(HomeFragment())
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
                Log.e(PaymentFragment.TAG, "ERROR: $e")
            }

        })
    }
}