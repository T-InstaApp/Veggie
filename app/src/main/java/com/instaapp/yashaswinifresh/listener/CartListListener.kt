package com.instaapp.yashaswinifresh.listener

interface CartListListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}