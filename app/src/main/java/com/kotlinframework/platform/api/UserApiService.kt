package com.kotlinframework.platform.api

import com.kotlinframework.platform.bean.UserBean
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 * 接口声明类
 */
interface UserApiService{

    @POST
    @FormUrlEncoded
    fun login(@Url string: String, @FieldMap hashMap: HashMap<String,String>):Observable<UserBean>

}