package com.instaapp.veggietemp1.network.responseModel

import java.io.Serializable


data class OrderListResponse(
    val payment_id: Int?,
    val order: Order?,
    val shippingmethod: OrderListShippingDetails,
    val restaurant: OrderListRestroDetails,
    val amount: String?,
    val transaction_id: String?,
    val status: String?,
    val created_at: String?,
    val updated_at: String?,
    val payment_method: String?,
    val payout_status: String?,
    val payout_message: String?,
    val payout_id: String?,
    val correlation_id: String?,
    val signature: Any?,
) : Serializable

data class Order(
    val order_id: Int?,
    val borzo_point_data: BorzoPointData?,
    val currency: String?,
    val subtotal: String?,
    val tip: String?,
    val service_fee: String?,
    val tax: String?,
    val shipping_fee: String?,
    val discount: String?,
    val total: String?,
    val created_at: String?,
    val updated_at: Any?,
    val extra: String?,
    val restaurant_request: String?,
    val shipping_address_text: String?,
    val billing_address_text: String?,
    val token: String?,
    val customer: Int?,

    ) : Serializable

data class OrderListShippingDetails(
    val id: Int?,
    val name: String?,
    val carrier: String?,
    val min_weight: String?,
    val max_weight: String?,
    val price: String?,
    val updated_at: String?,
    val status: String?,
    val restaurant: Int?,
) : Serializable

data class OrderListRestroDetails(
    val restaurant_id: Int?,
    val name: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val zip: String?,
    val nearby_zip: String?,
    val country: String?,
    val restaurant_url: String?,
    val image: Any?,
    val email: String?,
    val phone: String?,
    val status: String?,
    val currency: String?,
    val payment_account: String?,
    val desc: String?,
    val working_hours: String?,
    val is_clover: Boolean?,
    val mid: String?,
    val token: String?,
    val group_name: String?
) : Serializable

data class BorzoPointData(
    val id: Int?,
    val point_type: String?,
    val point_id: Int?,
    val delivery_id: Int?,
    val client_order_id: String?,
    val address: String?,
    val latitude: String?,
    val longitude: String?,
    val note: String?,
    val previous_point_driving_distance_meters: String?,
    val building_number: String?,
    val entrance_number: String?,
    val intercom_code: String?,
    val floor_number: String?,
    val apartment_number: String?,
    val tracking_url: String?,
    val customer: String?
) : Serializable
