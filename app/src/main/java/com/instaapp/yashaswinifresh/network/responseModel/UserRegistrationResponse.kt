package com.instaapp.yashaswinifresh.network.responseModel

import java.util.*

data class UserRegistrationResponse(
    val id: Int?,
    val username: String?,
    val email: String?,
    val is_active: Boolean?,
    val last_login: Objects?,
    val verified: String?,
)
