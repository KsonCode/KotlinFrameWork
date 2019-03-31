package com.kotlinframework.platform.contract

import android.content.Context
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.platform.bean.ProductEntity
import com.kotlinframework.platform.bean.UserBean
import java.util.*

/**
 * 契约类统一管理
 */
interface ProductContract{
    interface IProductPresenter{

       fun getProducts(hashMap: HashMap<String,String>,context: Context)
    }

    interface IProductModel{

        fun getProducts(context: Context,hashMap: HashMap<String, String>,modelCallback:IModelCallback<ProductEntity>)


    }

    interface IProductView{

        fun success(productEntity: ProductEntity)
        fun failure(string: String)

    }
}