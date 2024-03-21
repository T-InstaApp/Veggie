package com.instaapp.yashaswinifresh.network.repositories

import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.SafeApiRequest
import com.instaapp.yashaswinifresh.utils.log

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