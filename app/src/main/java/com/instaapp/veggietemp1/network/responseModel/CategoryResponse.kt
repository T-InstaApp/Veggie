package com.instaapp.veggietemp1.network.responseModel

data class CategoryResponse(
    val category_id: Int?,
    val category: String?,
    val restaurant_id: Int?,
    val category_url: String?,
    val image: Any?,
    val available_time: String?
)
