package com.kotlinframework.platform.app

import android.app.Application
import com.kotlinframework.platform.api.Api
import com.kotlinframework.net.network.RetrofitManager

class App:Application(){
    override fun onCreate() {
        super.onCreate()
        RetrofitManager.instance.init(Api.BASE_URL)
    }
}