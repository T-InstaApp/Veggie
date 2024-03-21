package com.instaapp.veggietemp1.network.responseModel

data class FreeResponse(
    val sub_total: String?,
    val tax: String?,
    val tip: String?,
    val service_fee: String?,
    val shipping_fee: String?,
    val discount: String?,
    val total: String?
)
