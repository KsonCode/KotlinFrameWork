package com.kotlinframework.platform.contract

interface LoginContract{
    abstract class LoginPresenter{

        abstract fun login(hashMap: HashMap<String,String>)
    }

    interface ILoginModel{


    }

    interface ILoginView{

    }
}