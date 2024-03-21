package com.instaapp.yashaswinifresh.network.requestModel

class ChangePassword(
    old_password: String,
    username: String,
    new_password1: String,
    new_password2: String
) {
    private var old_password: String
    private var username: String
    private var new_password1: String
    private var new_password2: String

    init {
        this.old_password = old_password
        this.username = username
        this.new_password1 = new_password1
        this.new_password2 = new_password2
    }

}