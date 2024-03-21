package com.instaapp.yashaswinifresh.network.repositories

import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.SafeApiRequest
import com.instaapp.yashaswinifresh.network.requestModel.AddBillingShippingAddressRequest

class CheckOutRepository(private val api: MyApi) : SafeApiRequest() {

    suspend fun getShippingAddress(token: String, userID: String) =
        apiRequest {
            api.getShippingAddress(token, userID)
        }

    suspend fun getBillingAddress(token: String, userID: String) =
        apiRequest {
            api.getBillingAddress(token, userID)
        }

    suspend fun getShippingDetails(token: String, restroID: String) =
        apiRequest {
            api.getShippingDetails(token, restroID)
        }

    suspend fun addShippingAddress(token: String, data: AddBillingShippingAddressRequest) =
        apiRequest {
            api.addShippingAddress(token, data)
        }

    suspend fun updateBillingAddress(
        token: String,
        id: String,
        data: AddBillingShippingAddressRequest
    ) =
        apiRequest {
            api.updateBillingAddress(token, id, data)
        }

    suspend fun addBillingAddress(token: String, data: AddBillingShippingAddressRequest) =
        apiRequest {
            api.addBillingAddress(token, data)
        }

    suspend fun getStateList(token: String) =
        apiRequest {
            api.getStateList(token)
        }

    suspend fun updateShippingAddress(
        token: String,
        id: String,
        data: AddBillingShippingAddressRequest
    ) =
        apiRequest {
            api.updateShippingAddress(token, id, data)
        }
}