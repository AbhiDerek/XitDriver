package com.app.xit.utill


import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.app.xit.BuildConfig
import com.app.xit.ServerResponse
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy



object HitApi {

    fun hitGetRequest(mContext: Context, url: String, resultResponse: ServerResponse){
//        val queue = Volley.newRequestQueue(mContext)
        val queue = VolleySingletion.requestQueque

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                resultResponse.success(response)
            },
            Response.ErrorListener {
                resultResponse.error(it)
            })

        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }


    fun hitPostRequest(mContext: Context, url: String, postData: MutableMap<String, String>, resultResponse: ServerResponse){
        val queue = VolleySingletion.requestQueque
        Log.i("HitApi", "RESPONSE : ${url.trim()}")
        val req = object : StringRequest(Request.Method.POST, url, { res ->

            // Creating JSON object from the response string
            // and passing it to result: (JSONObject) -> Unit function
            Log.i("HitApi", "RESPONSE : ${res.toString().trim()}")
            resultResponse.success(res.toString().trim())
        }, { volleyError ->

            // Getting error message and passing it
            // to val error: (String) -> Unit function
            resultResponse.error(volleyError)
        }) {
            // Overriding getParams() to pass our parameters
            override fun getParams(): MutableMap<String, String> {
                Log.i("HitApi", "REQUEST Param ${postData.toString()}")
                return postData
            }

            override fun getHeaders(): MutableMap<String, String> {
                var header= mutableMapOf<String, String>()
                header.put("Content-Type", "application/json")
                return header
            }

        }

        queue.add(req)

    }

    fun hitPostJsonRequest(mContext: Context, url: String, postData: JSONObject, resultResponse: ServerResponse){
        val queue = VolleySingletion.requestQueque
        if(BuildConfig.DEBUG) {
            Log.i("HitApi", "URL : ${url.trim()}")
            Log.i("HitApi", "Param : ${postData.toString()  }")
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, postData,
            Response.Listener { response ->postData
                if(BuildConfig.DEBUG) {
                    Log.i("HitApi", "RESPONSE : ${response.toString().trim()}")
                }
                resultResponse.success(response.toString().trim())
            },
            Response.ErrorListener { error ->
                Log.e("HitApi", "ERROR : ${error.message}")
                resultResponse.error(error)
            }
        )
        jsonObjectRequest.setRetryPolicy(
            DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        queue.add(jsonObjectRequest)


    }





}