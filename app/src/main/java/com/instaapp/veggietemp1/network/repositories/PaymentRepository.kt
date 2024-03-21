package com.instaapp.veggietemp1.network.repositories

import com.instaapp.veggietemp1.network.MyApi
import com.instaapp.veggietemp1.network.SafeApiRequest
import com.instaapp.veggietemp1.network.requestModel.AddFreeRequest
import com.instaapp.veggietemp1.network.requestModel.PrintOrderRequest
import com.instaapp.veggietemp1.network.requestModel.UpdateOrderRequest
import com.google.gson.JsonObject

class PaymentRepository(private val api: MyApi) : SafeApiRequest() {

    suspend fun getFees(token: String, data: AddFreeRequest) = apiRequest {
        api.getFees(token, data)
    }

    suspend fun getShipmentMethod(token: String, restID: String) = apiRequest {
        api.getShipmentMethod(token, restID)
    }

    suspend fun getUserProfile(token: String, userID: String) = apiRequest {
        api.getProfile(token, userID)
    }

    suspend fun updateOrderDetails(token: String, data: UpdateOrderRequest) = apiRequest {
        api.updateOrderDetail(token, data)
    }

    suspend fun placeOrder(token: String, data: JsonObject) = apiRequest {
        api.placeOrder(token, data)
    }
    suspend fun placeOrderForOtherCountry(token: String, data: JsonObject) = apiRequest {
        api.placeOrderForOtherCountry(token, data)
    }

    suspend fun printOrder(token: String, data: PrintOrderRequest) = apiRequest {
        api.printOrder(token, data)
    }
}