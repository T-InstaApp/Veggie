package com.instaapp.veggietemp1.network.responseModel

data class ShipmentResponse(
    val count: Int?,
    val next: Any?,
    val previous: Any?,
    val results: List<ShipmentResultResponse>?
)

data class ShipmentResultResponse(
    val id: Int?,
    val name: String?,
    val carrier: String?,
    val min_weight: String?,
    val max_weight: String?,
    val price: String?,
    val updated_at: String?,
    val status: String?,
    val restaurant: Int?,
    )
