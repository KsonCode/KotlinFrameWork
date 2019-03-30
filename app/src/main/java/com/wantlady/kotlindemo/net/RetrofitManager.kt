package com.wantlady.kotlindemo.net

class RetrofitManager private constructor(){

    companion object {
        val instance:RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            RetrofitManager()
        }
    }



}
