package com.instaapp.veggietemp1.network.repositories

import com.instaapp.veggietemp1.network.MyApi
import com.instaapp.veggietemp1.network.SafeApiRequest
import com.instaapp.veggietemp1.utils.log

class OrderRepository(private val api: MyApi) : SafeApiRequest() {

    suspend fun getOrderListData(token: String, restID: String, userID: String) =
        apiRequest {
            log("OrderRepository", " $token $restID $userID")
            api.getOrderList(token, restID, userID)
        }

    suspend fun getOrderMenuData(token: String, orderID: String) =
        apiRequest {
            api.orderItemDetails(token, orderID)
        }
}