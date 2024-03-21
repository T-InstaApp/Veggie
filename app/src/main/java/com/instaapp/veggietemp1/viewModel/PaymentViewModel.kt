package com.instaapp.veggietemp1.viewModel

import androidx.lifecycle.ViewModel
import com.instaapp.veggietemp1.network.repositories.PaymentRepository
import com.instaapp.veggietemp1.network.requestModel.AddFreeRequest
import com.instaapp.veggietemp1.network.requestModel.PrintOrderRequest
import com.instaapp.veggietemp1.network.requestModel.UpdateOrderRequest
import com.instaapp.veggietemp1.utils.*
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.listener.PaymentListener

class PaymentViewModel(private val repository: PaymentRepository) : ViewModel() {
    var paymentListener: PaymentListener? = null

    fun getFees(token: String, data: AddFreeRequest, type: String) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getFees(token, data)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, type)
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                    e.message!!.split("@")[2] + " Get Fee 1111 "
                )
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun getShipmentMethod(token: String, restID: String) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getShipmentMethod(token, restID)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, "getShipmentMethod")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                    " getShipmentMethod "
                )
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun getUserProfile(token: String, custID: String) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getUserProfile(token, custID)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, "getUserProfile")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1], "")
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun updateOrder(token: String, data: UpdateOrderRequest) {
         paymentListener?.onStarted()
        log(" PaymentActivity ", " ${data}")
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateOrderDetails(token, data)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, "updateOrder")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1], "")
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun placeOrder(token: String, data: JsonObject) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.placeOrder(token, data)
                mainLoginResponse.let {
                    log("PaymentSuccess ", " $mainLoginResponse")
                    log("PaymentSuccess ", " ${mainLoginResponse.toString()}")
                    paymentListener?.onSuccess(mainLoginResponse, "placeOrder")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                    "placeOrder"
                )
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun placeOrderForOtherCountry(token: String, data: JsonObject) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.placeOrderForOtherCountry(token, data)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, "placeOrder")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                    "placeOrder"
                )
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }

    fun printOrder(token: String, data: PrintOrderRequest) {
        paymentListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.printOrder(token, data)
                mainLoginResponse.let {
                    paymentListener?.onSuccess(mainLoginResponse, "printOrder")
                    return@main
                }
            } catch (e: ApiException) {
                paymentListener?.onFailure(
                    e.message!!.split("@")[0],
                    e.message!!.split("@")[1],
                    "placeOrder"
                )
            } catch (e: NoInternetException) {
                paymentListener?.onFailure(e.message!!, CommonKey.NO_INTERNET, "")
            }
        }
    }
}