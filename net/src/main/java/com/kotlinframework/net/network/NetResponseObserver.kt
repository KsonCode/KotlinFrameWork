package com.kotlinframework.net.network

import android.content.Context
import com.google.gson.Gson
import com.kotlinframework.net.widget.LoadingDialog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 封装响应数据，统一异常处理
 */
abstract class NetResponseObserver<T>(private val context: Context):Observer<T>{

    /**
     * 事件接收完毕
     */
    override fun onComplete() {
        LoadingDialog.cancel()
    }

    /**
     * 订阅事件的回调
     */
    override fun onSubscribe(d: Disposable) {
        LoadingDialog.show(context)
    }

    /**
     * 接收事件
     */
    override fun onNext(t: T) {
        success(t)
    }

    /**
     * 成功的回调
     */
    abstract fun success(data: T)

    /**
     * 失败的回调
     */
    abstract fun failure(statusCode: Int, apiErrorModel: ApiErrorModel)

    /**
     * 异常处理
     */
    override fun onError(e: Throwable) {
        LoadingDialog.cancel()
        if (e is HttpException) {
            val apiErrorModel: ApiErrorModel = when (e.code()) {
                ApiErrorType.INTERNAL_SERVER_ERROR.code ->
                    ApiErrorType.INTERNAL_SERVER_ERROR.getApiErrorModel(context)
                ApiErrorType.BAD_GATEWAY.code ->
                    ApiErrorType.BAD_GATEWAY.getApiErrorModel(context)
                ApiErrorType.NOT_FOUND.code ->
                    ApiErrorType.NOT_FOUND.getApiErrorModel(context)
                else -> otherError(e)

            }
            failure(e.code(), apiErrorModel)
            return
        }

        val apiErrorType: ApiErrorType = when (e) {
            is UnknownHostException -> ApiErrorType.NETWORK_NOT_CONNECT
            is ConnectException -> ApiErrorType.NETWORK_NOT_CONNECT
            is SocketTimeoutException -> ApiErrorType.CONNECTION_TIMEOUT
            else -> ApiErrorType.UNEXPECTED_ERROR
        }
        failure(apiErrorType.code, apiErrorType.getApiErrorModel(context))

    }
    private fun otherError(e: HttpException) =
        Gson().fromJson(e.response().errorBody()?.charStream(), ApiErrorModel::class.java)

}