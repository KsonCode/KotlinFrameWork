package com.kotlinframework.platform.presenter

import android.content.Context
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.platform.bean.ProductEntity
import com.kotlinframework.platform.bean.UserBean
import com.kotlinframework.platform.contract.LoginContract
import com.kotlinframework.platform.contract.ProductContract
import com.kotlinframework.platform.model.LoginModel
import com.kotlinframework.platform.model.ProductModel
import java.util.*

/**
 * presenter层
 */
class ProductPresenter : ProductContract.IProductPresenter {
    override fun getProducts(hashMap: HashMap<String, String>, context: Context) {
        productModel.getProducts(context,hashMap,object :IModelCallback<ProductEntity>{
            override fun failure(string: String) {
                iProductView?.failure(string)
            }

            override fun sucess(data: ProductEntity) {
                iProductView.success(data)
            }

        })
    }


    lateinit var productModel: ProductModel
    lateinit var iProductView: ProductContract.IProductView


    /**
     * 绑定view
     */
    fun attach(iProductView: ProductContract.IProductView) {
        this.iProductView = iProductView
        productModel = ProductModel()

    }



    /**
     * 解绑
     */
    fun detach() {
        if (iProductView != null) {
            iProductView == null
        }
    }


}