package com.app.xit

import org.json.JSONObject

interface ServerResponse {

    public fun success(t: String){}
    public fun error(e: Exception){}

}