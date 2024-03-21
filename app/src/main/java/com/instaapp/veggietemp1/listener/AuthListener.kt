package com.instaapp.veggietemp1.listener


interface AuthListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}