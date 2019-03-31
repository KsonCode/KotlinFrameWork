package com.kotlinframework.platform.contract

import android.content.Context
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.platform.bean.UserBean
import java.util.*

/**
 * 契约类统一管理
 */
interface LoginContract{
    interface LoginPresenter{

       fun login(hashMap: HashMap<String,String>,context: Context)
    }

    interface ILoginModel{

        fun login(context: Context,hashMap: HashMap<String, String>,modelCallback:IModelCallback<UserBean>)


    }

    interface ILoginView{

        fun success(userBean: UserBean)
        fun failure(string: String)

    }
}