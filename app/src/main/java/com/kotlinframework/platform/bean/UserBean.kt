package com.kotlinframework.platform.bean

/**
 * 实体类：通过jsontokotlin插件生成
 */
data class UserBean(
    val message: String,
    val result: Result,
    val status: String
)

data class Result(
    val headPic: String,
    val nickName: String,
    val phone: String,
    val sessionId: String,
    val sex: Int,
    val userId: Int
)