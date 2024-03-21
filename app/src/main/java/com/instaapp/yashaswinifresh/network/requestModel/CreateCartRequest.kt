package com.instaapp.yashaswinifresh.network.requestModel

class CreateCartRequest(customer_id: String, restaurant: String) {
    val restaurant: String?
    val customer_id: String?

    init {
        this.restaurant = restaurant
        this.customer_id = customer_id
    }
}