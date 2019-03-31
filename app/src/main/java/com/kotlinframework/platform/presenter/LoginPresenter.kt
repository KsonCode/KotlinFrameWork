package com.kotlinframework.platform.presenter

import android.content.Context
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.platform.bean.UserBean
import com.kotlinframework.platform.contract.LoginContract
import com.kotlinframework.platform.model.LoginModel
import java.util.*

/**
 * presenter层
 */
class LoginPresenter : LoginContract.LoginPresenter {


    lateinit var loginModel: LoginModel
    lateinit var iLoginView: LoginContract.ILoginView


    /**
     * 绑定view
     */
    fun attach(iLoginView: LoginContract.ILoginView) {
        this.iLoginView = iLoginView
        loginModel = LoginModel()

    }

    override fun login(hashMap: HashMap<String, String>, context: Context) {

        loginModel.login(context, hashMap, object : IModelCallback<UserBean> {
            override fun failure(string: String) {
                iLoginView?.failure(string)
            }

            override fun sucess(data: UserBean) {
                iLoginView?.success(data)
            }

        })
    }

    /**
     * 解绑
     */
    fun detach() {
        if (iLoginView != null) {
            iLoginView == null
        }
    }


}