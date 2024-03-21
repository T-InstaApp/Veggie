package com.instaapp.yashaswinifresh.listener

interface HomeListener {

    fun onStarted()
    fun onFailure(message: String)
    fun onSuccess(data: String, type: String)
    fun <T> onSuccessData(dataG: T, type: String)
}