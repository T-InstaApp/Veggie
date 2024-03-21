package com.instaapp.veggietemp1.network.requestModel

class userModel(user: String, restaurant: String) {

    private var user: String
    var restaurant: String

    init {
        this.user = user
        this.restaurant = restaurant
    }
}