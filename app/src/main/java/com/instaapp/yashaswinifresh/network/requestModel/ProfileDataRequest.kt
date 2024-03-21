package com.instaapp.yashaswinifresh.network.requestModel

class ProfileDataRequest(
    salutation: String,
    first_name: String,
    last_name: String,
    username: String,
    email: String,
    last_access: String,
    phone_number: String
) {
    private var salutation: String
    private var first_name: String
    private var last_name: String
    private var username: String
    private var email: String
    private var last_access: String
    private var phone_number: String

    init {
        this.salutation = salutation
        this.first_name = first_name
        this.last_name = last_name
        this.username = username
        this.email = email
        this.last_access = last_access
        this.phone_number = phone_number
    }

}
