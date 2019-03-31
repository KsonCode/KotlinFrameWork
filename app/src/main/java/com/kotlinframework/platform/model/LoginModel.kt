package com.kotlinframework.platform.model

import android.content.Context
import com.kotlinframework.net.network.ApiErrorModel
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.net.network.NetResponseObserver
import com.kotlinframework.net.network.NetScheduler
import com.kotlinframework.net.network.RetrofitManager
import com.kotlinframework.platform.api.Api
import com.kotlinframework.platform.api.UserApiService
import com.kotlinframework.platform.bean.UserBean
import com.kotlinframework.platform.contract.LoginContract
import java.util.HashMap

/**
 * 数据模型层
 */
class LoginModel:LoginContract.ILoginModel{
    override fun login(context: Context,hashMap: HashMap<String, String>, modelCallback: IModelCallback<UserBean>) {
        RetrofitManager.instance.createService(UserApiService::class.java).login(Api.LOGIN_URL,hashMap)
            .compose(NetScheduler.compose())
            .subscribe(object : NetResponseObserver<UserBean>(context){
                override fun success(data: UserBean) {

                    modelCallback?.sucess(data)

                }

                override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {

                    modelCallback?.failure(apiErrorModel.message)
                }

            })
    }

}


