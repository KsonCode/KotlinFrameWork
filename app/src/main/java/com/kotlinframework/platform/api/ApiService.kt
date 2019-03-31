package com.kotlinframework.platform.api

import com.kotlinframework.platform.bean.UserBean
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * 接口声明类
 */
interface ApiService{
    /**
     * 登录
     */
    @POST
    @FormUrlEncoded
    fun login(@Url string: String, @Field("phone") mobile:String, @Field("pwd")pwd: String): Observable<UserBean>

}