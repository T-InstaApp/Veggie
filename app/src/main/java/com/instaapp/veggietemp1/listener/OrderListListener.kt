package com.instaapp.veggietemp1.listener

interface OrderListListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
}