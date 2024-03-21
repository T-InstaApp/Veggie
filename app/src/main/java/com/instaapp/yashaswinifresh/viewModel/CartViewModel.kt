package com.instaapp.yashaswinifresh.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.instaapp.yashaswinifresh.network.repositories.CartRepository
import com.instaapp.yashaswinifresh.network.responseModel.CartListResponse
import com.instaapp.yashaswinifresh.utils.*
import com.google.gson.JsonObject
import com.instaapp.yashaswinifresh.listener.CartListListener
import kotlinx.coroutines.Job

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    private lateinit var job: Job
    var cartListener: CartListListener? = null

    private val _cartListData = MutableLiveData<CartListResponse>()
    val cartListData: LiveData<CartListResponse>
        get() = _cartListData

    fun getCartListData2(token: String, cartID: String, restID: String) {
        job = Coroutines.ioThenMain({ repository.getCartListData(token, cartID, restID) },
            { _cartListData.value = it })
    }


    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }


    fun getCartListData(token: String, cartID: String, restID: String) {
        cartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getCartListData(token, cartID, restID)
                mainLoginResponse.let {
                    cartListener?.onSuccess(mainLoginResponse, "getCartListData")
                    return@main
                }
            } catch (e: ApiException) {
                cartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                cartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateCart(token: String, cartItemID: String, data: JsonObject) {
        cartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateCart(token, cartItemID, data)
                mainLoginResponse.let {
                    cartListener?.onSuccess(mainLoginResponse, "mainLogin")
                    return@main
                }
            } catch (e: ApiException) {
                cartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                cartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun deleteCartItem(token: String, cartItemID: String) {
        log("deleteCartItem: ", " $token -- $cartItemID")
        cartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.deleteCartItem(token, cartItemID)
                mainLoginResponse.let {
                    cartListener?.onSuccess(mainLoginResponse, "deleteCartItem")
                    return@main
                }
            } catch (e: ApiException) {
                cartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                cartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }
}