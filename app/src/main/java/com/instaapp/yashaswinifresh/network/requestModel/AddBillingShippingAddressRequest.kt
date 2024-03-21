package com.instaapp.yashaswinifresh.network.requestModel

class AddBillingShippingAddressRequest(
    company_name: String,
    country: String,
    house_number: String,
    city: String,
    zip: String,
    address: String,
    customer_id: String,
    state: String,
    priority: Int,
    name: String,
    latitude: String,
    longitude: String,
) {
    private var company_name: String
    private var country: String
    private var house_number: String
    private var city: String

    private var zip: String
    private var address: String
    private var customer_id: String
    private var state: String

    private var priority: Int
    private var name: String
    private var latitude: String
    private var longitude: String

    init {
        this.company_name = company_name
        this.house_number = house_number
        this.country = country
        this.city = city
        this.zip = zip
        this.address = address
        this.customer_id = customer_id
        this.state = state
        this.priority = priority
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
    }


}