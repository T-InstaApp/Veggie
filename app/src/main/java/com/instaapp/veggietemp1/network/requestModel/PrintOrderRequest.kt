package com.instaapp.veggietemp1.network.requestModel

class PrintOrderRequest(restaurant_id: String, order_id: String, device_info: String?) {

    val restaurant_id: String
    val order_id: String
    val device_info: String?

    init {
        this.restaurant_id = restaurant_id
        this.order_id = order_id
        this.device_info = device_info
    }
}