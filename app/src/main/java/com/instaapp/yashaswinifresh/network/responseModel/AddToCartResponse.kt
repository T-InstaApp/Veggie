package com.instaapp.yashaswinifresh.network.responseModel

data class AddToCartResponse(
    val count: Int?,
    val results: List<AddToCartResultResponse>?,
    val total_cost: String?
)

data class AddToCartResultResponse(
    val ingredient: List<AddToCartIngredientResponse>?,
    val cart_item_id: Int?,
    val cart_id: Int?,
    val product_id: Int?,
    val product_name: String?,
    val product_url: String?,
    val image: String?,
    val price: String?,
    val sequence_id: Int?,
    val quantity: Int?,
    val extra: String?,
    val line_total: String?,
    )

data class AddToCartIngredientResponse(
    val cart_item_id: Int?,
    val ingredient_id: Int?,
    val ingredient_name: String?,
    val ingredient_url: String?,
    val image: String?,
    val price: String?,
    val quantity: Int?,
    val line_total: String?,
)