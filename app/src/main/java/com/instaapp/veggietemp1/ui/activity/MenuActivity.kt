package com.instaapp.veggietemp1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.interface_class.RecyclerViewClickListenerForAddToCart
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.requestModel.CreateCartRequest
import com.instaapp.veggietemp1.network.requestModel.CreateCartResponse
import com.instaapp.veggietemp1.network.responseModel.*
import com.instaapp.veggietemp1.ui.adapter.MenuListAdapter
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityMenuBinding
import com.instaapp.vegiestemp1.databinding.ToolbarWithCartBinding
import okhttp3.MediaType
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.reflect.Type

class MenuActivity : AppCompatActivity(), HomeListener, KodeinAware,
    RecyclerViewClickListenerForAddToCart {
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityMenuBinding
    private lateinit var toolbar: ToolbarWithCartBinding
    private var catID = 0
    private var pageNo = 1
    private var catName = ""
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var mMenuDataModel: ArrayList<MenuResponseData>? = null
    private lateinit var mMenuAdapter: MenuListAdapter
    var searchQuery = ""
    lateinit var btnAddToCart: AppCompatButton
    lateinit var layoutEditCart: LinearLayout
    lateinit var cardViewG: CardView
    lateinit var txtQTY: TextView
    var tempProductID = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        //  progressBar=binding.progressBar
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        toolbar.imgBack.visibility = View.VISIBLE
        toolbar.imgBack.setOnClickListener { finish() }
        toolbar.txtHeading.visibility = View.VISIBLE
        initSearchView()

        catID = intent.getIntExtra("cat_id", 0)
        catName = intent.getStringExtra("cat_name")!!
        toolbar.txtHeading.text = intent.getStringExtra("cat_name")

        toolbar.cartlayout.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS)) {
                if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                    if (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT) > 0) {
                        startActivity(Intent(applicationContext, CartListActivity::class.java))
                    } else
                        toast("Your cart is empty")
                } else
                    toast("Please login first")
            } else
                toast("Sorry, we are closed")
        }

        /*  val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
          binding.menuRv.layoutManager = linearLayoutManager*/
        val linearLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.menuRv.layoutManager = linearLayoutManager

        //Recycler View Scroll
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                pageNo = page
                getCategoryMenuList(catID, pageNo)
            }
        }

        mMenuDataModel = arrayListOf()
        binding.menuRv.addOnScrollListener(scrollListener!!)
        mMenuAdapter = MenuListAdapter(mMenuDataModel!!, this, "Menu", catID, catName, this)
        binding.menuRv.adapter = mMenuAdapter

        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
            checkCart()


    }

    override fun onResume() {
        scrollListener!!.resetState()
        binding.searchView.setText("")
        toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
        pageNo = 1
        mMenuDataModel = arrayListOf()
        binding.menuRv.addOnScrollListener(scrollListener!!)
        mMenuAdapter = MenuListAdapter(mMenuDataModel!!, this, "Menu", catID, catName, this)
        binding.menuRv.adapter = mMenuAdapter
        getCategoryMenuList(catID, pageNo)

        super.onResume()
    }

    private fun checkCart() {
        viewModel.checkCartForMenu(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            StaticValue.REST_ID,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString(),
        )
    }

    private fun createCart() {
        viewModel.createCart(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            CreateCartRequest(
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                StaticValue.REST_ID
            )
        )
    }

    fun getCategoryMenuList(catID: Int, page: Int) {
        if (pageNo == 1)
            mMenuDataModel = arrayListOf()
        viewModel.getMenuData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID, catID, page
        )
    }

    override fun onStarted() {
        if (pageNo == 1)
            binding.progress.progressLayout.visibility = View.VISIBLE

    }

    override fun onFailure(message: String) {
        log("onFailure", " $message")
        binding.progress.progressLayout.visibility = View.GONE
        if (pageNo == 1)
            showAlert(this, getString(R.string.alert), message)

    }

    override fun onSuccess(data: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        log("onSuccessData ", " onSuccess called $data ")
        if (type == "checkCart") {
            if (data.length > 2) {
                PreferenceProvider(applicationContext).setIntValue(
                    data.toInt(), PreferenceKey.CART_ID
                )
            } else
                createCart()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "searchItem") {
            val data: ProductSearchResponse = dataG as ProductSearchResponse
            if (data.data.productSearch.isNotEmpty()) {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                intent.putExtra("searchQuery", searchQuery)
                startActivity(intent)
            } else
                toast("Oh Sorry! No search result has found")
        } else if (type == "addToCart") {
            cardViewG.isClickable = true
            btnAddToCart.visibility = View.GONE
            layoutEditCart.visibility = View.VISIBLE
            val data: CartListResponse = dataG as CartListResponse
            val matchingResult = data.results.find { it.product?.product_id == tempProductID }
            if (matchingResult != null)
                CartManager.updateOrAddCartItem(
                    matchingResult.cart_item_id,
                    matchingResult.product!!.product_id, matchingResult.quantity!!
                )

            PreferenceProvider(applicationContext).setIntValue(
                (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT) + 1),
                PreferenceKey.CART_COUNT
            )
            toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
                PreferenceKey.CART_COUNT
            ).toString()
            toast("Product successfully added in your cart")
        } else if (type == "updateCart") {
            cardViewG.isClickable = true
            val data: CartListResponse = dataG as CartListResponse
            // Filter CartListResult items based on product_id
            val matchingResult = data.results.find { it.product?.product_id == tempProductID }
            if (matchingResult != null)
                CartManager.updateOrAddCartItem(
                    matchingResult.cart_item_id,
                    matchingResult.product!!.product_id, matchingResult.quantity!!
                )
        } else if (type == "deleteCartItem") {
            CartManager.removeCartItem(tempProductID)
            cardViewG.isClickable = true
            btnAddToCart.visibility = View.VISIBLE
            layoutEditCart.visibility = View.GONE

            PreferenceProvider(applicationContext).setIntValue(
                (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT) - 1),
                PreferenceKey.CART_COUNT
            )
            toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
                PreferenceKey.CART_COUNT
            ).toString()
        } else if (type == "createCart") {
            log("onSuccessData ", "  called createCart ")
            val data: CreateCartResponse = dataG as CreateCartResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id!!, PreferenceKey.CART_ID
            )
        } else {
            val data: MenuResponse = dataG as MenuResponse
            if (pageNo == 1) {
                mMenuDataModel!!.addAll(data.results!!)
                mMenuAdapter.updateAdapter(
                    mMenuDataModel!!
                )
            } else {
                mMenuDataModel!!.addAll(data.results!!)
                mMenuAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initSearchView() {
        log("initSearchView ", " Called")
        binding.searchView.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            log("initSearchView ", " Called $actionId ==${EditorInfo.IME_ACTION_SEARCH}")
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                log("initSearchView ", " Called ${v.text.toString().trim()}")
                if (v.text.toString().trim { it <= ' ' }.length > 2) {
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                    if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS)) {
                        loadSearchData(v.text.toString().trim { it <= ' ' })
                    } else toast(getString(R.string.rest_closed))
                }
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun loadSearchData(searchKey: String) {
        searchQuery = searchKey
        val sss =
            "{\"query\":\"\\nquery productSearch (\$token: String,\$productName: String,\$restaurantId: Int, \$extra: String,\$first: Int, \$skip: Int ){\\n  # Category, ProductName is optional\\n  productSearch (token : \$token, productName: \$productName, restaurantId: \$restaurantId, extra: \$extra, ,first: \$first, skip: \$skip  ) " +
                    "{\\n productName\\n  productId\\n  productUrl\\n    price\\n    taxExempt\\n  extra\\n  inStock\\n   availableTime\\n  deliveryType\\n suspendedUntil\\n category{\\n categoryId\\n category\\n availableTime\\n masterCategory{\\n masterCategoryId\\n status\\n MasterCategoryOpenHours{\\n day\\n startTime\\n endTime\\n status\\n}\\n}\\n}\\n }\\n}\",\"variables\":{\"productName\":\"" + searchKey + "\",\"restaurantId\":\"" + StaticValue.REST_ID + "\",\"extra\":\"\",\"first\":100,\"skip\":0,\"token\":\"0o6jcui8mfhmp56we69kcmu5rkejtock\"}}"

        log("loadSearchData ", " SSS $sss")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sss)

        viewModel.searchItem(body)
    }

    override fun <T> onRecyclerItemClick(cardView: CardView, dataG: T, type: String) {
        val data: MenuResponseData = dataG as MenuResponseData
        btnAddToCart = cardView.findViewById(R.id.btnAddToCart)
        layoutEditCart = cardView.findViewById(R.id.layoutEditCart)
        cardViewG = cardView
        txtQTY = cardView.findViewById(R.id.txtQTY)
        if (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID) > 0) {
            cardViewG.isClickable = false
            when (type) {
                "ADD" -> {
                    tempProductID = data.product_id!!
                    btnAddToCart.visibility = View.GONE
                    layoutEditCart.visibility = View.VISIBLE
                    txtQTY.text = "1"
                    val jsonObject = JsonObject()
                    val jsonArray1 = JsonArray()
                    jsonObject.addProperty("price", data.price)
                    jsonObject.addProperty("extra", "")
                    jsonObject.addProperty("quantity", 1)
                    jsonObject.addProperty(
                        "cart_id",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    )
                    jsonObject.addProperty("product_id", data.product_id)
                    jsonObject.addProperty("size_id", "")
                    jsonObject.add("addon_content_list", jsonArray1)
                    viewModel.addToCart(
                        "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                        jsonObject
                    )
                }

                "Minus" -> {
                    tempProductID = data.product_id!!
                    //  txtQTY.text = (txtQTY.text.toString().toInt() - 1).toString()
                    val qty = (txtQTY.text.toString().toInt() - 1) //txtQTY.text.toString().toInt()
                    if (qty != 0) {
                        txtQTY.text = qty.toString()
                        val jsonArray1 = JsonArray()
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("price", (data.price!!.toDouble() * qty))
                        jsonObject.addProperty("extra", "")
                        jsonObject.addProperty("quantity", qty)
                        jsonObject.addProperty(
                            "cart_id",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                        )
                        jsonObject.addProperty("product_id", data.product_id)
                        jsonObject.addProperty("size_id", "")
                        jsonObject.add("addon_content_list", jsonArray1)
                        viewModel.updateCart(
                            "Token " + PreferenceProvider(applicationContext).getStringValue(
                                PreferenceKey.APP_TOKEN
                            ),
                            CartManager.getCartForProduct(data.product_id)!!.first.toString(),
                            jsonObject
                        )
                    } else {
                        viewModel.deleteCartItem(
                            "Token " + PreferenceProvider(applicationContext).getStringValue(
                                PreferenceKey.APP_TOKEN
                            ),
                            CartManager.getCartForProduct(data.product_id)!!.first.toString()
                        )
                    }
                }

                "Plus" -> {
                    tempProductID = data.product_id!!
                    txtQTY.text = (txtQTY.text.toString().toInt() + 1).toString()
                    val qty = txtQTY.text.toString().toInt()
                    val jsonArray1 = JsonArray()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("price", (data.price!!.toDouble() * qty))
                    jsonObject.addProperty("extra", "")
                    jsonObject.addProperty("quantity", qty)
                    jsonObject.addProperty(
                        "cart_id",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    )
                    jsonObject.addProperty("product_id", data.product_id)
                    jsonObject.addProperty("size_id", "")
                    jsonObject.add("addon_content_list", jsonArray1)
                    viewModel.updateCart(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        CartManager.getCartForProduct(data.product_id)!!.first.toString(),
                        jsonObject
                    )
                }
            }

        } else
            toast("Please login first")
    }

}