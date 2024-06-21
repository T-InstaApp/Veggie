package com.instaapp.yashaswinifresh.listener


interface AuthListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}