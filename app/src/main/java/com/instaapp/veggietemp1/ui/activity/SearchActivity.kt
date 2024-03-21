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
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.interface_class.RecyclerViewClickListenerForAddToCart
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.requestModel.CreateCartRequest
import com.instaapp.veggietemp1.network.requestModel.CreateCartResponse
import com.instaapp.veggietemp1.network.responseModel.CartListResponse
import com.instaapp.veggietemp1.network.responseModel.ProductSearch
import com.instaapp.veggietemp1.network.responseModel.ProductSearchResponse
import com.instaapp.veggietemp1.ui.adapter.SearchMenuAdapter
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivitySearchMenuResultBinding
import com.instaapp.vegiestemp1.databinding.ToolbarWithCartBinding
import okhttp3.MediaType
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SearchActivity : AppCompatActivity(), HomeListener, KodeinAware,
    RecyclerViewClickListenerForAddToCart {
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivitySearchMenuResultBinding
    private lateinit var toolbar: ToolbarWithCartBinding

    //For Add TO Cart
    lateinit var btnAddToCart: AppCompatButton
    lateinit var layoutEditCart: LinearLayout
    lateinit var cardViewG: CardView
    lateinit var txtQTY: TextView
    var tempProductID = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchMenuResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        //  progressBar=binding.progressBar
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        toolbar.imgBack.visibility = View.VISIBLE
        toolbar.imgBack.setOnClickListener { finish() }
        toolbar.txtHeading.text = "Search Results"

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
        initSearchView()
        binding.searchView.setText(intent.getStringExtra("searchQuery"))


        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
            checkCart()

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

    override fun onResume() {
        toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
        performSearch(intent.getStringExtra("searchQuery")!!)

        super.onResume()
    }

    private fun initSearchView() {
        binding.searchView.setOnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.text.toString().trim { it <= ' ' }.length > 2) {
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                    performSearch(v.text.toString().trim { it <= ' ' })
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performSearch(searchKey: String) {
        val sss =
            "{\"query\":\"\\nquery productSearch (\$token: String,\$productName: String,\$restaurantId: Int, \$extra: String,\$first: Int, \$skip: Int ){\\n  # Category, ProductName is optional\\n  productSearch (token : \$token, productName: \$productName, restaurantId: \$restaurantId, extra: \$extra, ,first: \$first, skip: \$skip  ) " +
                    "{\\n productName\\n  productId\\n  productUrl\\n    price\\n MRP\\n   taxExempt\\n  extra\\n  inStock\\n   availableTime\\n  deliveryType\\n suspendedUntil\\n category{\\n categoryId\\n category\\n availableTime\\n masterCategory{\\n masterCategoryId\\n status\\n MasterCategoryOpenHours{\\n day\\n startTime\\n endTime\\n status\\n}\\n}\\n}\\n }\\n}\",\"variables\":{\"productName\":\"" + searchKey + "\",\"restaurantId\":\"" + StaticValue.REST_ID + "\",\"extra\":\"\",\"first\":100,\"skip\":0,\"token\":\"0o6jcui8mfhmp56we69kcmu5rkejtock\"}}"
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sss)
        viewModel.searchItem(body)
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE

    }

    override fun onFailure(message: String) {
        binding.progress.progressLayout.visibility = View.GONE
        showAlert(this, getString(R.string.alert), message)
    }

    override fun onSuccess(data: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "checkCart") {
            if (data.length > 2) {
                PreferenceProvider(applicationContext).setIntValue(
                    data.toInt(), PreferenceKey.CART_ID
                )
            } else
                createCart()
        }
    }

    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "searchItem") {
            val data: ProductSearchResponse = dataG as ProductSearchResponse
            binding.menuRv.setHasFixedSize(true)
            if (data.data.productSearch.isEmpty())
                toast("No menu found for given search")
            else {
                val linearLayoutManager =
                    GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                binding.menuRv.layoutManager = linearLayoutManager
                binding.menuRv.adapter =
                    SearchMenuAdapter(data.data.productSearch, applicationContext, this)
            }
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
            val data: CreateCartResponse = dataG as CreateCartResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id!!, PreferenceKey.CART_ID
            )
        }
    }

    override fun <T> onRecyclerItemClick(cardView: CardView, dataG: T, type: String) {
        val data: ProductSearch = dataG as ProductSearch
        btnAddToCart = cardView.findViewById(R.id.btnAddToCart)
        layoutEditCart = cardView.findViewById(R.id.layoutEditCart)
        cardViewG = cardView
        txtQTY = cardView.findViewById(R.id.txtQTY)
        if (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID) > 0) {
            cardViewG.isClickable = false
            when (type) {
                "ADD" -> {
                    tempProductID = data.productId
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
                    jsonObject.addProperty("product_id", data.productId)
                    jsonObject.addProperty("size_id", "")
                    jsonObject.add("addon_content_list", jsonArray1)
                    viewModel.addToCart(
                        "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                        jsonObject
                    )
                }
                "Minus" -> {
                    tempProductID = data.productId
                    //  txtQTY.text = (txtQTY.text.toString().toInt() - 1).toString()
                    val qty = (txtQTY.text.toString().toInt() - 1) //txtQTY.text.toString().toInt()
                    if (qty != 0) {
                        txtQTY.text = qty.toString()
                        val jsonArray1 = JsonArray()
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("price", (data.price.toDouble() * qty))
                        jsonObject.addProperty("extra", "")
                        jsonObject.addProperty("quantity", qty)
                        jsonObject.addProperty(
                            "cart_id",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                        )
                        jsonObject.addProperty("product_id", data.productId)
                        jsonObject.addProperty("size_id", "")
                        jsonObject.add("addon_content_list", jsonArray1)
                        viewModel.updateCart(
                            "Token " + PreferenceProvider(applicationContext).getStringValue(
                                PreferenceKey.APP_TOKEN
                            ),
                            CartManager.getCartForProduct(data.productId)!!.first.toString(),
                            jsonObject
                        )
                    } else {
                        viewModel.deleteCartItem(
                            "Token " + PreferenceProvider(applicationContext).getStringValue(
                                PreferenceKey.APP_TOKEN
                            ),
                            CartManager.getCartForProduct(data.productId)!!.first.toString()
                        )
                    }
                }
                "Plus" -> {
                    tempProductID = data.productId
                    txtQTY.text = (txtQTY.text.toString().toInt() + 1).toString()
                    val qty = txtQTY.text.toString().toInt()
                    val jsonArray1 = JsonArray()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("price", (data.price.toDouble() * qty))
                    jsonObject.addProperty("extra", "")
                    jsonObject.addProperty("quantity", qty)
                    jsonObject.addProperty(
                        "cart_id",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    )
                    jsonObject.addProperty("product_id", data.productId)
                    jsonObject.addProperty("size_id", "")
                    jsonObject.add("addon_content_list", jsonArray1)
                    viewModel.updateCart(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        CartManager.getCartForProduct(data.productId)!!.first.toString(),
                        jsonObject
                    )
                }
            }

        } else
            toast("Please login first")
    }
}