package com.instaapp.yashaswinifresh.network.requestModel

data class CreateCartResponse(
    val id: Int?,
    val created_at: String?,
    val updated_at:String?,
    val extra: String?,
    val billing_address_id:Any?,
    val customer_id: Int?,
    val shipping_address_id:Any?,
    val restaurant: Int?,
)
