package com.instaapp.yashaswinifresh.network.requestModel

class UpdateOrderRequest(
    extra: String, tip: String, shipping_fee: String, cart_id: String,
    status: String, total: String, customer: String, tax: String,
    subtotal: String, currency: String, service_fee: String, discount: String,
    shipping_address_text: String, billing_address_text: String
) {
    private var extra: String
    var tip: String
    private var shipping_fee: String
    var cart_id: String
    private var status: String
    var total: String
    private var customer: String
    var tax: String
    private var subtotal: String
    var currency: String
    private var service_fee: String
    var discount: String
    private var shipping_address_text: String
    var billing_address_text: String

    init {
        this.extra = extra
        this.tip = tip
        this.shipping_fee = shipping_fee
        this.cart_id = cart_id
        this.status = status
        this.total = total
        this.customer = customer
        this.tax = tax
        this.subtotal = subtotal
        this.currency = currency
        this.service_fee = service_fee
        this.discount = discount
        this.shipping_address_text = shipping_address_text
        this.billing_address_text = billing_address_text
    }
}