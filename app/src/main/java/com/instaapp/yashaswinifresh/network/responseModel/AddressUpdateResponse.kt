package com.instaapp.yashaswinifresh.network.responseModel

data class AddressUpdateResponse(
    val id: Int?,
    val priority: Int?,
    val name: String?,
    val company_name: String?,
    val address: String?,
    val house_number: String?,
    val zip: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val customer_id: String?,
)
