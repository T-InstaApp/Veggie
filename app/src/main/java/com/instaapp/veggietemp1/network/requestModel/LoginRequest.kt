package com.instaapp.veggietemp1.network.requestModel

class LoginRequest(email: String?, password: String?, username: String?) {
    var email: String?
    var password: String?
    var username: String?

    init {
        this.email = email
        this.password = password
        this.username = username
    }
}