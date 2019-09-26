package com.app.xit

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.app.xit.utill.VolleySingletion

class XitApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        VolleySingletion.initConfi(this)
        AppPrefs.defaultPrefs(this)
    }

}