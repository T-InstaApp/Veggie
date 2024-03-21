package com.instaapp.yashaswinifresh.network.requestModel

class userModel(user: String, restaurant: String) {

    private var user: String
    var restaurant: String

    init {
        this.user = user
        this.restaurant = restaurant
    }
}