package com.instaapp.veggietemp1.listener

interface PaymentListener {
    fun onStarted()
    fun onFailure(message: String, type: String,errorBody:String?)
    fun <T> onSuccess(dataG: T, type: String)
}
