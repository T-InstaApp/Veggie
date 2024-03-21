package com.instaapp.veggietemp1.network.responseModel

data class CheckCartResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<CheckCartResponseDataList>?
)

data class CheckCartResponseDataList(
    val id: Int?,
    val created_at: String?,
    val updated_at: String?,
    val extra: String?,
    val billing_address_id: Int?,
    val customer_id: Int?,
    val shipping_address_id: String?,
    val restaurant: Int?
)
