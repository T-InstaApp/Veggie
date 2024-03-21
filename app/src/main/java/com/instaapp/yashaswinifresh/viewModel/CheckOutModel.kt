package com.instaapp.yashaswinifresh.viewModel

import androidx.lifecycle.ViewModel
import com.instaapp.yashaswinifresh.listener.CheckOutListener
import com.instaapp.yashaswinifresh.network.repositories.CheckOutRepository
import com.instaapp.yashaswinifresh.network.requestModel.AddBillingShippingAddressRequest
import com.instaapp.yashaswinifresh.utils.*
import kotlinx.coroutines.Job

class CheckOutModel(private val repository: CheckOutRepository) : ViewModel() {

    private lateinit var job: Job
    var checkOutListener: CheckOutListener? = null

    fun getShippingAddressData(token: String, userID: String) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getShippingAddress(token, userID)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "getShippingAddressData")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getBillingAddressData(token: String, userID: String) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getBillingAddress(token, userID)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "getBillingAddressData")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getShippingDetails(token: String, restroID: String) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getShippingDetails(token, restroID)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "getShippingDetails")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun addShippingAddress(token: String, data: AddBillingShippingAddressRequest) {
        log("addShipping Add= ", " $data")
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addShippingAddress(token, data)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "addShippingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun updateBillingAddress(token: String, id: String, data: AddBillingShippingAddressRequest) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateBillingAddress(token, id, data)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "updateBillingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun addBillingAddress(token: String, data: AddBillingShippingAddressRequest) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addBillingAddress(token, data)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "addBillingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateShippingAddress(token: String, id: String, data: AddBillingShippingAddressRequest) {
        log("addShipping update= ", " $data")
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateShippingAddress(token, id, data)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "updateShippingAddress")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getStateList(token: String) {
        checkOutListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getStateList(token)
                mainLoginResponse.let {
                    checkOutListener?.onSuccess(mainLoginResponse, "getStateList")
                    return@main
                }
            } catch (e: ApiException) {
                checkOutListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                checkOutListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

}