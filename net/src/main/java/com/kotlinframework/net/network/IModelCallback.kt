package com.kotlinframework.net.network

interface IModelCallback<T>{

    fun sucess(data: T)
    fun failure(string: String)

}