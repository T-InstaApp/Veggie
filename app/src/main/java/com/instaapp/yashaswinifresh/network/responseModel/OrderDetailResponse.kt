package com.instaapp.yashaswinifresh.network.responseModel

data class OrderDetailResponse(
    val order_id: Int?,
    val currency: String?,
    val subtotal: String?,
    val tip: String?,
    val service_fee: String?,
    val tax: String?,
    val shipping_fee: String?,
    val discount: String?,
    val total: String?,
    val created_at: String?,
    val updated_at: String?,
    val extra: String?,
    val restaurant_request: String?,
    val shipping_address_text: String?,
    val billing_address_text: String?,
    val token: String?,
    val customer: Int?,
)
