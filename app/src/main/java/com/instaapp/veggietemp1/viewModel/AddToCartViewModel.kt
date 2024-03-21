package com.instaapp.veggietemp1.viewModel

import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.instaapp.veggietemp1.network.repositories.AddToCartRepository
import com.instaapp.veggietemp1.network.requestModel.CreateCartRequest
import com.instaapp.veggietemp1.utils.*
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.listener.AddToCartListener

class AddToCartViewModel(private val repository: AddToCartRepository) : ViewModel() {

    var addToCartListener: AddToCartListener? = null

    fun checkCart(token: String, restID: String, custID: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkCart(token, restID, custID)
                mainLoginResponse.let {
                    //   if (it!!.count!! > 0) {
                    addToCartListener?.onSuccess(it, "checkCart")
                    //  }
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun createCart(token: String, data: CreateCartRequest) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.createCart(token, data)
                mainLoginResponse.let {
                    addToCartListener?.onSuccess(it, "createCart")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getCartCount(token: String, cartID: String, restID: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getCart(token, cartID, restID)
                mainLoginResponse.let {
                    addToCartListener?.onSuccess(it!!.count.toString(), "getCartCount")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun fetchSize(token: String, productID: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.fetchSize(token, productID)
                mainLoginResponse.let {
                    log("fetchSize ", " " + it.toString())
                    addToCartListener?.onSuccess(it, "fetchSize")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun productAddOnWithoutSize(token: String, productID: String) {
        log("getProductAddOnWithOutSize ", " Product id == $productID")
        Coroutines.main {
            try {
                val mainLoginResponse = repository.productAddOnWithoutSize(token, productID)
                mainLoginResponse.let {
                    log("getProductAddOnWithOutSize ", " " + it.toString())
                    addToCartListener?.onSuccess(it, "getProductAddOnWithOutSize")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun getSubAddonsForAddons(
        token: String, addonContentID: Int, addonContentId: Int, addonName: String
    ) {
        log("a"," $addonContentId $addonName")
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getSubAddonsForAddons(token, addonContentID)
                mainLoginResponse.let {
                    addToCartListener?.onSuccess(it, "getSubAddonsForAddons")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun productAddOnWithSize(token: String, productID: String, sizeID: String) {
        log("productAddOnWithSize ", " id =$productID size ID= $sizeID")
        Coroutines.main {
            try {
                val mainLoginResponse = repository.productAddOnWithSize(token, productID, sizeID)
                mainLoginResponse.let {
                    log("productAddOnWithSize ", " " + it.toString())
                    addToCartListener?.onSuccess(it, "productAddOnWithSize")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    //Call form productContainAddOnDataForMultiSelect
    fun productContents2(
        token: String, parentAddOnId: Int, multiSelectText: TextView, strTitle: String,
        textID: TextView, minS: Int, maxS: Int, freeS: Int, availTime: String,
        txtExtraParentAddOnHeading: TextView, txtParentAddOnID: TextView,
        txtExtTotalPrice: TextView, position: Int
    ) {
        Coroutines.main {
            try {
                log("productContents ", " $parentAddOnId")
                val mainLoginResponse = repository.productContents(token, parentAddOnId)
                mainLoginResponse.let {
                    log("productContents ", " " + it.toString())
                    addToCartListener?.onSuccess(it, "productContents")
                    addToCartListener?.onSuccessForProductAddON(
                        it, "productContents", multiSelectText,
                        strTitle, textID, minS, maxS, freeS, availTime,
                        txtExtraParentAddOnHeading, txtParentAddOnID, txtExtTotalPrice, position
                    )
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun productContents2(
        token: String, parentAddOnId: Int, multiSelectText: AutoCompleteTextView, textID: TextView
    ) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.productContents(token, parentAddOnId)
                mainLoginResponse.let {
                    log("productContents ", " " + it.toString())
                    addToCartListener?.onSuccess(it, "productContents")
                    addToCartListener?.onSuccessForProductAddON2(
                        it,
                        "productContents",
                        multiSelectText,
                        textID
                    )
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }


    fun addToCart(token: String, data: JsonObject) {
        addToCartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addToCart(token, data)
                mainLoginResponse.let {
                    log("addToCart ", "  DetailScreen2 " + it.toString())
                    addToCartListener?.onSuccess(it, "addToCart")
                    return@main
                }
            } catch (e: ApiException) {
                log(
                    "DetailScreen2 ",
                    " ex ${e.message!!.split("@")[0]}  99 ${e.message!!.split("@")[1]}"
                )
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun updateCart(token: String, cartItemID: String, data: JsonObject) {
        addToCartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateCart(token, cartItemID, data)
                mainLoginResponse.let {
                    addToCartListener?.onSuccess(it, "updateCart")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

    fun deleteCartItem(token: String, cartItemID: String) {
        addToCartListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.deleteCartItem(token, cartItemID)
                mainLoginResponse.let {
                    addToCartListener?.onSuccess(it, "deleteCartItem")
                    return@main
                }
            } catch (e: ApiException) {
                addToCartListener?.onFailure(e.message!!.split("@")[0], e.message!!.split("@")[1])
            } catch (e: NoInternetException) {
                addToCartListener?.onFailure(e.message!!, CommonKey.NO_INTERNET)
            }
        }
    }

}