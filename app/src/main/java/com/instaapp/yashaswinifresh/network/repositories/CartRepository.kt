package com.instaapp.yashaswinifresh.network.repositories

import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.SafeApiRequest
import com.google.gson.JsonObject

class CartRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun getCartListData(token: String, cartID: String, restroID: String) =
        apiRequest {
            api.getCartList(token, cartID, restroID)
        }

    suspend fun updateCart(token: String, cartItemID: String, data: JsonObject) =
        apiRequest {
            api.updateCart(token, cartItemID, data)
        }

    suspend fun deleteCartItem(token: String, cartItemID: String) =
        apiRequest {
            api.deleteCartItem(token, cartItemID)
        }
}