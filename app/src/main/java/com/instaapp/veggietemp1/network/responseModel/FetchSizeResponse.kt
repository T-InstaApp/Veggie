package com.instaapp.veggietemp1.network.responseModel

class FetchSizeResponse : ArrayList<FetchSizeItem>()

data class FetchSizeItem(
    val price: String,
    val product: Product,
    val product_size_price_id: Int,
    val size: FetchSizeSize
)

data class FetchSizeSize(
    val category: Int?,
    val category_size_id: Int?,
    val size: String?
)
