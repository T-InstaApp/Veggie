package com.instaapp.yashaswinifresh.network.requestModel

class MainLogin(user: String, pass: String, rest_id: String) {

    private var username: String
    var password: String
    private var restaurant_id: String

    init {
        username = user
        password = pass
        restaurant_id = rest_id
    }
}



