package com.instaapp.veggietemp1.network.responseModel


data class StateListRequest(
    val count: Int?,
    val next: String?,  // It can be a String or null
    val previous: String?,  // It can be a String or null
    val results: ArrayList<StateListResponse>?
)

data class StateListResponse(
    val id: Int?,
    val state: String?,
    val country: String?,
    val country_code: String?,
    val tax: String?
)

