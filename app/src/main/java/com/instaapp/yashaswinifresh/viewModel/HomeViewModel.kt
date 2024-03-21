package com.instaapp.yashaswinifresh.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.instaapp.yashaswinifresh.listener.HomeListener
import com.instaapp.yashaswinifresh.network.repositories.HomeRepository
import com.instaapp.yashaswinifresh.network.requestModel.CreateCartRequest
import com.instaapp.yashaswinifresh.network.responseModel.*
import com.instaapp.yashaswinifresh.utils.*
import kotlinx.coroutines.Job
import okhttp3.RequestBody

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    var homeListener: HomeListener? = null
    private lateinit var job: Job

    private val _categoryData = MutableLiveData<List<CategoryResponse>>()
    val categoryData: LiveData<List<CategoryResponse>>
        get() = _categoryData

    private val _searchListData = MutableLiveData<ProductSearchResponse>()
    val searchListData: LiveData<ProductSearchResponse>
        get() = _searchListData


    fun getUserToken(user: String, pass: String, restID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.mainLogin(user, pass, restID)
                mainLoginResponse.let {
                    homeListener?.onSuccess(it!!.token, "MainLogin")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun getRestHours(token: String, restID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getHours(token, restID)
                mainLoginResponse.let {
                    homeListener?.onSuccess(it!!.status!!, "getRestHours")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun checkCart(token: String, restID: String, custID: String) {
        log(" onSuccessData ", "checkCart Called ")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkCart(token, restID, custID)
                mainLoginResponse.let {
                    log(" onSuccessData ", "checkCart Respomse $it")
                    if (it!!.count!! > 0) {
                        log(" onSuccessData ", "checkCart Respomse $it")
                        homeListener?.onSuccess(it.results?.get(0)?.id.toString(), "checkCart")
                    }
                    return@main
                }
            } catch (e: ApiException) {
                log(" addToCart ", "checkCart Ex=$e")
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                log(" addToCart ", "checkCart Ex=$e")
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun checkCartForMenu(token: String, restID: String, custID: String) {
        log(" onSuccessData ", "checkCart Called ")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.checkCart(token, restID, custID)
                mainLoginResponse.let {
                    log(" onSuccessData ", "checkCart Respomse $it")
                    if (it!!.count!! > 0) {
                        homeListener?.onSuccess(it.results?.get(0)?.id.toString(), "checkCart")
                    } else
                        homeListener?.onSuccess("0", "checkCart")
                    return@main
                }
            } catch (e: ApiException) {
                log(" addToCart ", "checkCart Ex=$e")
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                log(" addToCart ", "checkCart Ex=$e")
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun createCart(token: String, data: CreateCartRequest) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.createCart(token, data)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "createCart")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }


    fun getCartCount(token: String, cartID: String, restID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getCart(token, cartID, restID)
                mainLoginResponse.let {
                    if (it!!.count!! > 0) {
                        homeListener?.onSuccess(it.count.toString(), "getCartCount")
                        homeListener?.onSuccessData(it, "getCartCount")
                    } else {
                        homeListener?.onSuccess("", "getCartCount")
                    }
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun getCategory(token: String, restID: String, catID: Int) {
        job = Coroutines.ioThenMain({ repository.getCategory(token, restID, catID) },
            { _categoryData.value = it })
    }

    fun searchCategory(body: RequestBody) {
        job = Coroutines.ioThenMain({ repository.searchCart(body) },
            { _searchListData.value = it })
    }

    fun searchItem(body: RequestBody) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.searchCart(body)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "searchItem")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun getMenuData2(
        token: String,
        restID: String,
        catID: Int
    ): LiveData<PagingData<MenuResponseData>> {
        log("MenuData", "Model Lib")
        return repository.getMenuData(catID, restID, token).cachedIn(viewModelScope)
    }

    fun getMenuData(token: String, restID: String, catID: Int, page: Int) {
        log("getMenuData ", " =$token = $restID = $catID = $page")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getMenuData(token, restID, catID, page)
                mainLoginResponse.let {
                    log("getMenuData ", " =$it")
                    homeListener?.onSuccessData(it, "getMenuData")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }


    fun getAboutUS(token: String, restID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.getAboutUS(token, restID)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "MainLogin")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun getAppDetails(type: String, restroID: Int) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getAppDetails(restroID, type)
                response.let {
                    homeListener?.onSuccessData(it, "getAppDetails")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun getMasterCat(token: String, restroID: String) {
        log("getMasterCat", "Token =$token id=$restroID")
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.getMasterCat(token, restroID)
                response.let {
                    log("getMasterCat ", " $it")
                    homeListener?.onSuccessData(it, "getMasterCat")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun deleteAccount(token: String, userID: String) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val response = repository.deleteAccount(
                    token, userID
                )
                response.let {
                    homeListener?.onSuccess("Success", "deleteAccount")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun addToCart(token: String, data: JsonObject) {
        homeListener?.onStarted()
        Coroutines.main {
            try {
                val mainLoginResponse = repository.addToCart(token, data)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "addToCart")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun updateCart(token: String, cartItemID: String, data: JsonObject) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.updateCart(token, cartItemID, data)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "updateCart")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }

    fun deleteCartItem(token: String, cartItemID: String) {
        Coroutines.main {
            try {
                val mainLoginResponse = repository.deleteCartItem(token, cartItemID)
                mainLoginResponse.let {
                    homeListener?.onSuccessData(it, "deleteCartItem")
                    return@main
                }
            } catch (e: ApiException) {
                homeListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                homeListener?.onFailure(e.message!!)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }

}