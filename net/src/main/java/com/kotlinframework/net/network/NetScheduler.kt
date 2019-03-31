package com.kotlinframework.net.network

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 线程调度器
 */
object NetScheduler{
    fun <T> compose():ObservableTransformer<T,T>{

        return ObservableTransformer {
            observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}