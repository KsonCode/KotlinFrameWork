package com.kotlinframework.platform.model

import android.content.Context
import com.kotlinframework.net.network.ApiErrorModel
import com.kotlinframework.net.network.IModelCallback
import com.kotlinframework.net.network.NetResponseObserver
import com.kotlinframework.net.network.NetScheduler
import com.kotlinframework.net.network.RetrofitManager
import com.kotlinframework.platform.api.Api
import com.kotlinframework.platform.api.ProductApiService
import com.kotlinframework.platform.api.UserApiService
import com.kotlinframework.platform.bean.ProductEntity
import com.kotlinframework.platform.bean.UserBean
import com.kotlinframework.platform.contract.LoginContract
import com.kotlinframework.platform.contract.ProductContract
import java.util.HashMap

/**
 * 数据模型层
 */
class ProductModel:ProductContract.IProductModel{
    override fun getProducts(
        context: Context,
        hashMap: HashMap<String, String>,
        modelCallback: IModelCallback<ProductEntity>
    ) {
        RetrofitManager.instance.createService(ProductApiService::class.java).getProducts(Api.PRODUCT_URL,hashMap)
            .compose(NetScheduler.compose())
            .subscribe(object : NetResponseObserver<ProductEntity>(context){
                override fun success(data: ProductEntity) {

                    modelCallback?.sucess(data)

                }

                override fun failure(statusCode: Int, apiErrorModel: ApiErrorModel) {

                    modelCallback?.failure(apiErrorModel.message)
                }

            })
    }


}


