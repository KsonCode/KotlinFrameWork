package com.kotlinframework.platform.bean
data class ProductEntity(
    val message: String,
    val result: List<Result>,
    val status: String
) {
    data class Result(
        val commodityId: Int,
        val commodityName: String,
        val masterPic: String,
        val price: Int,
        val saleNum: Int
    )
}