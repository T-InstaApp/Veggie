package com.instaapp.yashaswinifresh.network.responseModel

data class MainLoginResponse(
    val status: String,
    val msg: String,
    val message: String,
    val token: String,
    val id: String
)