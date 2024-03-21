package com.instaapp.yashaswinifresh.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.instaapp.yashaswinifresh.listener.HomeListener
import com.instaapp.yashaswinifresh.network.repositories.OrderRepository
import com.instaapp.yashaswinifresh.network.responseModel.OrderListResponse
import com.instaapp.yashaswinifresh.network.responseModel.OrderMenuDetail
import com.instaapp.yashaswinifresh.utils.ApiException
import com.instaapp.yashaswinifresh.utils.Coroutines
import com.instaapp.yashaswinifresh.utils.NoInternetException
import com.instaapp.yashaswinifresh.utils.log
import kotlinx.coroutines.Job

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private lateinit var job: Job
    var homeListener: HomeListener? = null

    private val _orderListData = MutableLiveData<List<OrderListResponse>>()
    val orderListData: LiveData<List<OrderListResponse>>
        get() = _orderListData

    private val _orderMenuData = MutableLiveData<OrderMenuDetail>()
    val orderMenuData: LiveData<OrderMenuDetail>
        get() = _orderMenuData

    fun getOrderListData(token: String, restID: String, userID: String) {
        job = Coroutines.ioThenMain({ repository.getOrderListData(token, restID, userID) },
            { _orderListData.value = it })
    }

    fun getOrderMenuData(token: String, orderID: String) {
        job = Coroutines.ioThenMain({ repository.getOrderMenuData(token, orderID) },
            { _orderMenuData.value = it })
    }
    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

    fun getOrderedMenu(token: String, orderID: String) {
        log("getOrderedMenu", "Token =$token id=$orderID")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getOrderMenuData(token, orderID)
                response.let {
                    log("getMasterCat ", " $it")
                    homeListener?.onSuccessData(it, "getOrderedMenu")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }
}