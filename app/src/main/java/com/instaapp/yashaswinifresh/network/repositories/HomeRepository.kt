package com.instaapp.yashaswinifresh.network.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.instaapp.yashaswinifresh.network.MyApi
import com.instaapp.yashaswinifresh.network.SafeApiRequest
import com.instaapp.yashaswinifresh.network.requestModel.MainLogin
import com.instaapp.yashaswinifresh.network.responseModel.*
import com.example.instauserapp.paging.DataPagingSource
import com.google.gson.JsonObject
import com.instaapp.yashaswinifresh.network.requestModel.CreateCartRequest
import com.instaapp.yashaswinifresh.utils.log
import okhttp3.RequestBody

class HomeRepository(private val api: MyApi) : SafeApiRequest() {

    suspend fun mainLogin(user: String, pass: String, restID: String): MainLoginResponse? {
        return apiRequest {
            api.mainLogin(MainLogin(user, pass, restID))
        }
    }

    suspend fun getHours(token: String, restID: String): HourResponse? {
        log("Home ", "HomeRepository catch1  ${"$token Rest $restID"}")
        return apiRequest {
            api.getHour(token, restID.toInt())
        }
    }

    suspend fun checkCart(token: String, restID: String, custId: String): CheckCartResponse? {
        return apiRequest {
            api.checkCart(token, restID, custId)
        }
    }
    suspend fun createCart(token: String, data: CreateCartRequest) = apiRequest {
        api.createCart(token, data)
    }

    suspend fun getCart(token: String, cartID: String, restID: String): CartListResponse? {
        return apiRequest {
            api.getCartList(token, cartID, restID)
        }
    }

    suspend fun getCategory(token: String, restaurantID: String, catID: Int) =
        apiRequest { api.getCategory(token, restaurantID, catID) }

    suspend fun getAboutUS(token: String, restID: String) =
        apiRequest { api.getAboutUs(token, restID) }


    fun getMenuData(catId: Int, restID: String, token: String) = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 100),
        pagingSourceFactory = { DataPagingSource(api, catId, restID, token) }
    ).liveData

    suspend fun getMenuData(token: String, restID: String, catID: Int, page: Int) =
        apiRequest { api.getMenuList(token, restID, catID, page) }

    suspend fun searchCart(body: RequestBody): ProductSearchResponse? {
        return apiRequest { api.searchCart(body) }
    }


    suspend fun getAppDetails(restorID: Int, type: String) =
        apiRequest { api.getAppDetails(restorID, type) }

    suspend fun getMasterCat(token: String, restroID: String) =
        apiRequest { api.getMasterCat(token, restroID) }

    suspend fun deleteAccount(token: String, userID: String) =
        apiRequest { api.deleteAccount(token, userID) }

    suspend fun addToCart(token: String, data: JsonObject) =
        apiRequest { api.addToCart(token, data) }

    suspend fun updateCart(token: String, cartItemID: String, data: JsonObject) =
        apiRequest { api.updateCart(token, cartItemID, data) }

    suspend fun deleteCartItem(token: String, cartItemID: String) =
        apiRequest { api.deleteCartItem(token, cartItemID) }
}