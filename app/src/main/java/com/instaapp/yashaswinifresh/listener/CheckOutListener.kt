package com.instaapp.yashaswinifresh.listener

interface CheckOutListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}