package com.instaapp.veggietemp1.network.responseModel

import java.util.*

data class UserCreateResponse(
    val salutation: String?,
    val last_access: String?,
    val first_name: String?,
    val email: String?,
    val last_name: Objects?,
    val phone_number: String?,
    val customer_id: Int?,
    val extra: String
)
