package com.instaapp.veggietemp1.network.repositories

import com.instaapp.veggietemp1.network.MyApi
import com.instaapp.veggietemp1.network.SafeApiRequest
import com.instaapp.veggietemp1.network.requestModel.CreateCartRequest
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.network.responseModel.CartListResponse

class AddToCartRepository(private val api: MyApi) : SafeApiRequest() {
    suspend fun checkCart(token: String, restID: String, userID: String) = apiRequest {
        api.checkCart(token, restID, userID)
    }

    suspend fun createCart(token: String, data: CreateCartRequest) = apiRequest {
        api.createCart(token, data)
    }

    suspend fun fetchSize(token: String, productID: String) = apiRequest {
        api.fetchSize(token, productID)
    }

    suspend fun productAddOnWithoutSize(token: String, productID: String) = apiRequest {
        api.productAddOnWithoutSize(token, productID)
    }


    suspend fun getSubAddonsForAddons(token: String, addonContentIS: Int) = apiRequest {
        api.getSubAddonsForAddons(token, addonContentIS)
    }

    suspend fun productAddOnWithSize(token: String, productID: String, sizeID: String) =
        apiRequest {
            api.productAddOnWithSize(token, productID, sizeID.toInt())
        }

    suspend fun productContents(token: String, parentAddOnId: Int) =
        apiRequest {
            api.productContents(token, parentAddOnId)
        }

    suspend fun addToCart(token: String, data: JsonObject) =
        apiRequest { api.addToCart(token, data) }

    suspend fun updateCartDetails(token: String, id: String, data: JsonObject) =
        apiRequest {
            api.updateCartDetails(token, id, data)
        }

    suspend fun getCart(token: String, cartID: String, restID: String): CartListResponse? {
        return apiRequest {
            api.getCartList(token, cartID, restID)
        }
    }

    suspend fun updateCart(token: String, cartItemID: String, data: JsonObject) =
        apiRequest { api.updateCart(token, cartItemID, data) }

    suspend fun deleteCartItem(token: String, cartItemID: String) =
        apiRequest { api.deleteCartItem(token, cartItemID) }

}