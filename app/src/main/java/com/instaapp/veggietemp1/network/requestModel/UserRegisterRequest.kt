package com.instaapp.veggietemp1.network.requestModel

class UserRegisterRequest(
    username: String?, email: String?, first_name: String?, last_name: String?,
    password: String?, restaurant_id: String?,verified:String?, customer_id: String?, last_access: String?,
    extra: String?, salutation: String?, phone_number: String?
) {

    var username: String? = null
    var email: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var password: String? = null
    var restaurant_id: String? = null
    var verified: String? = null

    //For Create Customer
    var customer_id: String? = null
    var last_access: String? = null
    var extra: String? = null
    var salutation: String? = null
    var phone_number: String? = null

    init {
        this.username = username
        this.email = email
        this.first_name = first_name
        this.last_name = last_name
        this.password = password
        this.restaurant_id = restaurant_id
        this.verified=verified
        this.customer_id = customer_id
        this.last_access = last_access
        this.extra = extra
        this.salutation = salutation
        this.phone_number = phone_number
    }
}
