package com.instaapp.veggietemp1.network.responseModel

data class OrderMenuDetail(
    val count: Int,
    val results: List<OrderMenuDetailsResult>?,
    val total_cost: String
)

data class OrderMenuDetailsResult(
    val addon_content: List<OrderMenuDetailsAddonContent>?,
    val extra: String,
    val order: Int,
    val order_item_id: Int,
    val price: String,
    val product: Product,
    val product_price: String,
    val quantity: Int,
    val size: Size
)

data class OrderMenuDetailsAddonContent(
    val addon_content: OrderMenuDetailsAddonContentX?
)

data class OrderMenuDetailsAddonContentX(
    val addon_content_id: Int,
    val parent_addon: OrderMenuDetailsParentAddon,
    val price: String,
    val title: String
)

data class OrderMenuDetailsParentAddon(
    val addon_id: Int,
    val addon_title: String,
    val restaurant: Int,
    val selection_method: String
)

