package com.wantlady.kotlindemo.api

import com.wantlady.kotlindemo.bean.UserBean
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * 接口声明类
 */
interface UserApiService{

    @POST
    @FormUrlEncoded
    fun login(@Url string: String,@Field("phone") mobile:String,@Field("pwd")pwd: String):Call<UserBean>

}