package com.instaapp.veggietemp1.network.requestModel

class MobileVerificationRequest(phone_number: String?, verification_code: String?) {
    private var phone_number: String? = null
    var verification_code: String? = null

    init {
        this.phone_number = phone_number
        this.verification_code = verification_code
    }
}

