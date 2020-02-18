package com.app.xit.home

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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

import java.util.*
import android.widget.TimePicker
import kotlin.math.min


class ProofFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {


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

        view.et_proof_date.setOnClickListener{
            showDatePicker()
        }

        view.et_proof_time.setOnClickListener{
            showTimePicker()
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


    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val yy = calendar.get(Calendar.YEAR)
        val mm = calendar.get(Calendar.MONTH)
        val dd = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), this, yy, mm, dd).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mView.et_proof_date.setText(dayOfMonth.toString() + ":" + month + ":" + year)
    }

    private fun showTimePicker(){
        val myCalender = Calendar.getInstance()
        val hour = myCalender.get(Calendar.HOUR_OF_DAY)
        val minute = myCalender.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        mView.et_proof_time.setText(hourOfDay.toString() + ":" + minute)
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