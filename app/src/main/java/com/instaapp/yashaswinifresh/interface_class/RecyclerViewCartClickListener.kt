package com.instaapp.yashaswinifresh.interface_class

import com.instaapp.yashaswinifresh.network.responseModel.CartListResult

interface RecyclerViewCartClickListener {
    fun addSubDeleteItem(
        cartData: CartListResult,
        product_id: String,
        position: Int,
        sequence: String,
        price: Double,
        count: String,
        check: String,
        cartItemId: String
    )
}