package com.wantlady.kotlindemo.contract

interface LoginContract{
    abstract class LoginPresenter{

        abstract fun login(hashMap: HashMap<String,String>)
    }

    interface ILoginModel{


    }

    interface ILoginView{

    }
}