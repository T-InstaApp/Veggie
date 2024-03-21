package com.instaapp.yashaswinifresh.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActivityDetails2Binding
import com.instaapp.yashaswinifresh.interface_class.RecyclerViewClickListenerForSubAddons
import com.instaapp.yashaswinifresh.interface_class.SizeAdapterOnClick
import com.instaapp.yashaswinifresh.listener.AddToCartListener
import com.instaapp.yashaswinifresh.network.requestModel.CreateCartRequest
import com.instaapp.yashaswinifresh.network.requestModel.CreateCartResponse
import com.instaapp.yashaswinifresh.network.responseModel.*
import com.instaapp.yashaswinifresh.ui.adapter.MultipleCheckBoxListAdapter
import com.instaapp.yashaswinifresh.ui.adapter.SizeAdapter
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.AddToCartViewModel
import com.instaapp.yashaswinifresh.viewModelFactory.AddToCartViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class AddToCartActivity : AppCompatActivity(), KodeinAware, SizeAdapterOnClick, AddToCartListener,
    RecyclerViewClickListenerForSubAddons {

    override val kodein by kodein()
    private lateinit var viewModel: AddToCartViewModel
    private val factory: AddToCartViewModelFactory by instance()
    private lateinit var binding: ActivityDetails2Binding
    private var productId: String = ""
    private var productUrl: String = ""
    private var productCost: String = ""
    private var productName: String = ""
    private var catId: String = ""
    private var extra: String = ""
    private var currencyType: String = ""
    private var sizeApi = 0
    private var productAddOn = 0
    private var checkCount = 0
    private lateinit var productAddon: ArrayList<ProductAddonItem>
    private var selectedPosition: ArrayList<String> = ArrayList()
    private lateinit var stateNameList: ArrayList<String>
    private var sizeSelect = false
    private var sizeId: String = ""
    private var sizePrice = 0.0
    private var count: Int = 1
    private var finalProductPrice = 0.0
    private var toppingProductPrice = 0.0
    private var multiSelection: HashMap<Int, Any>? = null
    private var singleSelection: HashMap<Int, Any>? = null
    private var multiTextSelection: HashMap<Int, String>? = null
    private lateinit var productContain: ArrayList<ProductContainItem>
    private lateinit var removedAddOnsData: ArrayList<RemovedAddOnsModel>
    private var layoutPosition = 0
    private var addOnMultiCollection: String = ""
    private var addOnSingleCollection: String = ""
    private var alreadyInCart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_add_cart)

        // binding = ActivityAddCartBinding.inflate(layoutInflater)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details2)

        viewModel = ViewModelProvider(this, factory)[AddToCartViewModel::class.java]
        viewModel.addToCartListener = this


        binding.toolbar.cartlayout.setOnClickListener {
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


        binding.toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()

        binding.toolbar.imgBack.setOnClickListener { finish() }
        removedAddOnsData = ArrayList()
        currencyType =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        productId = intent.getStringExtra("product_id")!!
        // limitToppings = intent.getStringExtra("limit_toppings")!!
        // freLimitToppings = intent.getStringExtra("free_limit_toppings")!!
        productUrl = intent.getStringExtra("product_url")!!
        productCost = intent.getStringExtra("product_price")!!
        productName = intent.getStringExtra("product_name")!!
        catId = intent.getStringExtra("category_id")!!
        extra = intent.getStringExtra("product_desc")!!

        sizePrice = productCost.toDouble()

        binding.toolbar.txtHeading.text = productName
        binding.menuDetailMenuDescText.text = extra
        binding.txtUnitDetail.text = "Unit Price : $currencyType$productCost"
        PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + productCost
        binding.txtSubTotal.text =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + productCost

        Glide.with(this).load(productUrl).into(binding.imgProductdetail)

        stateNameList = ArrayList()
        multiSelection = HashMap()
        singleSelection = HashMap()
        multiTextSelection = HashMap()
        productContain = ArrayList()
        productAddon = ArrayList()

        fetchSize()

        alreadyInCart = CartManager.isProductInCart(productId.toInt())

        binding.imgAdd.setOnClickListener {
            if (alreadyInCart)
                plusButton()
            else
                plusButtonOld()
        }

        binding.imgMinus.setOnClickListener {
            if (alreadyInCart)
                minusButton()
            else
                minusButtonOld()
        }

        binding.btnAddToCart.setOnClickListener {
            if (!PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                showLoginDialog(this, getString(R.string.alert), getString(R.string.logincheck))
            else
                onCartButtonClick()
        }

    }

    override fun onResume() {
        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
            checkCart()
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun plusButton() {
        val data = CartManager.getCartForProduct(productId.toInt())
        val qty = ((data!!.third) + 1)
        binding.txtQuantity.text = qty.toString()
        binding.txtSubTotal.text =
            currencyType + convertToUSDFormat((productCost.toDouble() * qty).toString())
        val jsonArray1 = JsonArray()
        val jsonObject = JsonObject()
        jsonObject.addProperty("price", (productCost.toDouble() * qty))
        jsonObject.addProperty("extra", "")
        jsonObject.addProperty("quantity", qty)
        jsonObject.addProperty(
            "cart_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
        )
        jsonObject.addProperty("product_id", productId)
        jsonObject.addProperty("size_id", "")
        jsonObject.add("addon_content_list", jsonArray1)
        viewModel.updateCart(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            CartManager.getCartForProduct(productId.toInt())!!.first.toString(),
            jsonObject
        )
    }

    @SuppressLint("SetTextI18n")
    private fun minusButton() {
        val data = CartManager.getCartForProduct(productId.toInt())
        val qty = ((data!!.third) - 1)
        if (qty != 0) {
            binding.txtQuantity.text = qty.toString()
            binding.txtSubTotal.text =
                currencyType + convertToUSDFormat((productCost.toDouble() * qty).toString())
            val jsonArray1 = JsonArray()
            val jsonObject = JsonObject()
            jsonObject.addProperty("price", (productCost.toDouble() * qty))
            jsonObject.addProperty("extra", "")
            jsonObject.addProperty("quantity", qty)
            jsonObject.addProperty(
                "cart_id",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
            )
            jsonObject.addProperty("product_id", productId)
            jsonObject.addProperty("size_id", "")
            jsonObject.add("addon_content_list", jsonArray1)
            viewModel.updateCart(
                "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                CartManager.getCartForProduct(productId.toInt())!!.first.toString(), jsonObject
            )
        } else {
            viewModel.deleteCartItem(
                "Token " + PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                ), CartManager.getCartForProduct(productId.toInt())!!.first.toString()
            )
        }
    }

    @SuppressLint("SetTextI18n")
    fun plusButtonOld() {
        if (sizeApi != 0 && !sizeSelect) {
            Toast.makeText(applicationContext, "Please select size", Toast.LENGTH_SHORT).show()
        } else if (sizeApi == 0 && productAddOn == 0) {
            if (count in 1..99) {
                count += 1
                binding.txtQuantity.text = count.toString()
                val c: Double = productCost.toDouble()
                val b: Double = java.lang.Double.valueOf(
                    binding.txtSubTotal.text.toString()
                        .replace(currencyType, "")
                )
                val prise = c + b
                binding.txtSubTotal.text =
                    currencyType + convertToUSDFormat(
                        prise.toString()
                    )
            } else {
                if (count > 9)
                    toast(
                        "The maximum quantity that can be ordered at this time is 100"
                    )
            }
        } else {
            if (count in 1..99) {
                count += 1
                binding.txtQuantity.text = count.toString()
                val c: Double = productCost.toDouble()
                if (finalProductPrice == 0.0) {
                    val aa = toppingProductPrice + c
                    val prise = count * aa
                    binding.txtSubTotal.text =
                        currencyType + convertToUSDFormat(
                            prise.toString()
                        )
                } else {
                    val aa = toppingProductPrice + finalProductPrice
                    val prise = count * aa
                    binding.txtSubTotal.text =
                        currencyType + convertToUSDFormat(
                            prise.toString()
                        )
                }
            } else {
                if (count > 9)
                    toast(
                        "The maximum quantity that can be ordered at this time is 100"
                    )
            }
        }

        /* if (sizeApi != 0 && !sizeSelect) {
             toast("Please select size")
         } else if (sizeApi == 0 && productAddOn == 0) {
             if (count > 0) {
                 count += 1
                 txtQuantity.text = count.toString()
                 val c: Double = productCost.toDouble()
                 val b: Double = java.lang.Double.valueOf(
                     txtSubTotal.text.toString()
                         .replace(
                             PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                             ""
                         )
                 )
                 val prise = c + b
                 txtSubTotal.text =
                     PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + convertToUSDFormat(
                         prise.toString()
                     )
             }
         } else {
             if (count > 0) {
                 count += 1
                 txtQuantity.text = count.toString()
                 val c: Double = productCost.toDouble()
                 if (finalProductPrice == 0.0) {
                     val aa = toppingProductPrice + c
                     val prise = count * aa
                     txtSubTotal.text =
                         PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + convertToUSDFormat(
                             prise.toString()
                         )
                 } else {
                     val aa = toppingProductPrice + finalProductPrice
                     val prise = count * aa
                     txtSubTotal.text =
                         PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + convertToUSDFormat(
                             prise.toString()
                         )
                 }
             }
         }*/
    }

    @SuppressLint("SetTextI18n")
    fun minusButtonOld() {

        if (sizeApi != 0 && !sizeSelect) {
            Toast.makeText(
                applicationContext,
                "Please select size",
                Toast.LENGTH_SHORT
            ).show()
        } else if (sizeApi == 0 && productAddOn == 0) {
            if (count != 1) {
                count -= 1
                binding.txtQuantity.text = count.toString()

                val c: Double = productCost?.toDouble()!!

                val b: Double = java.lang.Double.valueOf(
                    binding.txtSubTotal.text.toString()
                        .replace(currencyType, "")
                )


                val prise = b - c
                binding.txtSubTotal.text =
                    currencyType + convertToUSDFormat(
                        prise.toString()
                    )
//                Log.d("TAG", "init: " + count.toString())
            }
        } else {
            if (count != 1) {
                count -= 1
                binding.txtQuantity.text = count.toString()
                val c: Double = productCost?.toDouble()!!
                val b: Double = java.lang.Double.valueOf(
                    binding.txtSubTotal.text.toString()
                        .replace(currencyType, "")
                )
                if (finalProductPrice == 0.0) {
                    val aa = toppingProductPrice + c
                    val prise = b - aa
                    binding.txtSubTotal.text =
                        currencyType + convertToUSDFormat(
                            prise.toString()
                        )
                } else {

                    val aa = toppingProductPrice + finalProductPrice
                    val prise = b - aa
                    binding.txtSubTotal.text =
                        currencyType + convertToUSDFormat(
                            prise.toString()
                        )
                }
            }
        }
    }

    private fun fetchSize() {
        binding.rlcSize.layoutManager =
            GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
        viewModel.fetchSize(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productId
        )
    }

    private fun checkCart() {
        viewModel.checkCart(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            StaticValue.REST_ID,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString(),
        )
    }

    // Get Cart Count
    private fun checkCartCount() {
        viewModel.getCartCount(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID).toString(),
            StaticValue.REST_ID,
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

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        log("onFailure DetailScreen2 ", " $message type $type")
        binding.progress.progressLayout.visibility = View.GONE
        when (type) {
            CommonKey.ERROR_CODE_401 -> {
                dialogSessionExpire(
                    applicationContext,
                )
            }
            else -> showAlert(
                this,
                getString(R.string.alert),
                message
                //  getString(R.string.no_response)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "checkCart") {
            val data: CheckCartResponse = dataG as CheckCartResponse
            if (data.results!!.isNotEmpty()) {
                PreferenceProvider(applicationContext).setIntValue(
                    data.results[0].id!!, PreferenceKey.CART_ID
                )
                checkCartCount()
            } else
                createCart()
        } else if (type == "createCart") {
            val data: CreateCartResponse = dataG as CreateCartResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id!!, PreferenceKey.CART_ID
            )
        } else if (type == "getCartCount") {
            binding.toolbar.homeCartCount.text = dataG as String
            PreferenceProvider(applicationContext).setIntValue(
                dataG.toString().toInt(), PreferenceKey.CART_COUNT
            )
        } else if (type == "fetchSize") {
            val data: FetchSizeResponse = dataG as FetchSizeResponse
            if (data.size != 0) {
                val sizeAdapter = SizeAdapter(this@AddToCartActivity, " ", applicationContext)
                sizeApi = data.size
                sizeAdapter.submitList(data)
                binding.rlcSize.adapter = sizeAdapter
            } else {
                sizeApi = 0
                binding.txtIngredient.visibility = View.GONE
                getProductAddOnWithOutSize()
            }
        } else if (type == "getProductAddOnWithOutSize") {
            val data: ProductAddOnResponse = dataG as ProductAddOnResponse
            productAddOn = 0
            productAddon = ArrayList(data)
            if (data.size != 0) {
                showAddToCartButton()
                productAddOn = 1
                checkCount = 0
                for (i in 0 until data.size) {
                    if (data[i].status && (data[i].available_time!!.equals(
                            "All Time",
                            ignoreCase = true
                        ) || !checkAvailableTime(data[i].available_time!!)
                                )
                    ) {
                        var freeS = data[i].free_topping_count
                        var minS = data[i].min_selection_toppings
                        var maxS = data[i].max_selection_toppings
                        if (freeS == null)
                            freeS = 0
                        if (minS == null)
                            minS = 0
                        if (maxS == null)
                            maxS = 1

                        if (data[i].addon.selection_method.equals(
                                "any one",
                                ignoreCase = true
                            )
                        ) {
                            checkCount++
                            createDynamicLayoutForAnyOne(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS
                            )
                        } else {
                            createDynamicLayoutForMultiSelect(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS,
                                data[i].available_time!!,
                                2
                            )
                        }
                    }
                }
            } else
                showAddToCartButton()

        } else if (type == "getSubAddonsForAddons") {
            val data: AddonContentSubAddon = dataG as AddonContentSubAddon
            if (data.size != 0) {
                productAddOn = 1
                checkCount = 0
                for (i in 0 until data.size) {
                    if (data[i].status && (data[i].available_time!!.equals(
                            "All Time",
                            ignoreCase = true
                        ) || !checkAvailableTime(data[i].available_time!!)
                                )
                    ) {
                        var freeS = data[i].free_topping_count
                        var minS = data[i].min_selection_toppings
                        var maxS = data[i].max_selection_toppings
                        if (freeS == null)
                            freeS = 0
                        if (minS == null)
                            minS = 0
                        if (maxS == null)
                            maxS = 1

                        if (data[i].addon.selection_method.equals(
                                "any one",
                                ignoreCase = true
                            )
                        ) {
                            checkCount++
                            createDynamicLayoutForAnyOne(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS
                            )
                        } else {
                            createDynamicLayoutForMultiSelect(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS,
                                data[i].available_time!!,
                                i
                            )
                        }
                    }
                }
            }
        } else if (type == "productAddOnWithSize") {
            val data: ProductAddOnResponse = dataG as ProductAddOnResponse
            productAddOn = 0
            productAddon = ArrayList(data)
            if (data.size != 0) {
                showAddToCartButton()
                productAddOn = 1
                checkCount = 0
                for (i in 0 until data.size) {
                    if (data[i].status && (data[i].available_time!!.equals(
                            "All Time",
                            ignoreCase = true
                        ) || !checkAvailableTime(data[i].available_time!!))
                    ) {
                        var freeS = data[i].free_topping_count
                        var minS = data[i].min_selection_toppings
                        var maxS = data[i].max_selection_toppings
                        if (freeS == null)
                            freeS = 0
                        if (minS == null)
                            minS = 0
                        if (maxS == null)
                            maxS = 1
                        if (data[i].addon.selection_method.equals(
                                "any one",
                                ignoreCase = true
                            )
                        ) {
                            checkCount++
                            createDynamicLayoutForAnyOne(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS,
                            )
                        } else {
                            createDynamicLayoutForMultiSelect(
                                data[i].addon.addon_title!!,
                                data[i].addon.addon_id!!,
                                data[i].addon.selection_method!!,
                                minS,
                                maxS,
                                freeS,
                                data[i].available_time!!,
                                2
                            )
                        }
                    }
                }
            } else
                showAddToCartButton()

        } else if (type == "addToCart") {
            log("Message ", "addToCart")
            PreferenceProvider(applicationContext).setIntValue(
                (PreferenceProvider(
                    applicationContext
                ).getIntValue(PreferenceKey.CART_COUNT) + 1), PreferenceKey.CART_COUNT
            )
            binding.toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
                PreferenceKey.CART_COUNT
            ).toString()
            showAlertWithAndCloseActivity(
                this,
                getString(R.string.congratulations),
                getString(R.string.product_added_into_cart)
            )

            val data: CartListResponse = dataG as CartListResponse
            val matchingResult = data.results.find { it.product?.product_id == productId.toInt() }
            if (matchingResult != null)
                CartManager.updateOrAddCartItem(
                    matchingResult.cart_item_id,
                    matchingResult.product!!.product_id, matchingResult.quantity!!
                )
        } else if (type == "updateCart") {
            val data: CartListResponse = dataG as CartListResponse
            val matchingResult = data.results.find { it.product?.product_id == productId.toInt() }
            if (matchingResult != null)
                CartManager.updateOrAddCartItem(
                    matchingResult.cart_item_id,
                    matchingResult.product!!.product_id, matchingResult.quantity!!
                )
        } else if (type == "deleteCartItem") {
            CartManager.removeCartItem(productId.toInt())
            binding.btnAddToCart.visibility = View.VISIBLE
            alreadyInCart = false
            PreferenceProvider(applicationContext).setIntValue(
                (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT) - 1),
                PreferenceKey.CART_COUNT
            )
            binding.toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
                PreferenceKey.CART_COUNT
            ).toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAddToCartButton() {

        if (alreadyInCart) {
            binding.txtQuantity.text =
                (CartManager.getCartForProduct(productId.toInt())!!.third).toString()

            binding.txtSubTotal.text =
                currencyType + convertToUSDFormat(
                    (productCost.toDouble() *
                            (CartManager.getCartForProduct(productId.toInt())!!.third)).toString()
                )
        } else
            binding.btnAddToCart.visibility = View.VISIBLE
    }

    override fun <T> onSuccessForProductAddON(
        dataG: T, type: String, multiSelectText: TextView, strTitle: String, textID: TextView,
        minS: Int, maxS: Int, freeS: Int, availTime: String, txtExtraParentAddOnHeading: TextView,
        txtParentAddOnID: TextView, txtExtTotalPrice: TextView, position: Int
    ) {

        val data: ProductContain = dataG as ProductContain
        val listItems = arrayListOf<String>()
        val productContainData = ArrayList(data)
        for (i in 0 until data.size) {
            listItems.add(
                " ${productContainData[i].title}(${
                    PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.CURRENCY_TYPE
                    )!!
                }${
                    getAddOnPrice(
                        applicationContext,
                        productContainData[i].price, 1, sizeId
                    )
                })"
            )
        }
        setData(
            multiSelectText,
            strTitle,
            listItems,
            productContainData,
            textID,
            minS,
            maxS,
            freeS,
            availTime,
            txtExtraParentAddOnHeading, txtParentAddOnID, txtExtTotalPrice, position
        )
    }

    @SuppressLint("SetTextI18n")
    override fun <T> onSuccessForProductAddON2(
        dataG: T, type: String, autoCompleteTextView: AutoCompleteTextView, textID: TextView
    ) {
        val data: ProductContain = dataG as ProductContain
        val addOnNameList = arrayListOf<String>()

        val productContainData = ArrayList(data)

        for (i in 0 until data.size) {
            if (productContainData[i].price.toDouble() > 0)
                addOnNameList.add(productContainData[i].title + "(" + currencyType + productContainData[i].price + ")")
            else
                addOnNameList.add(productContainData[i].title)
        }

        autoCompleteTextView.setAdapter(
            ArrayAdapter(
                applicationContext,
                R.layout.autocomplete_item_layout,
                addOnNameList
            )
        )
        autoCompleteTextView.onItemClickListener = AdapterView
            .OnItemClickListener { _, _, position, _ ->
                try {
                    /* val selectedItem = productContainData[position].addon_content_id
                     textID.text = selectedItem.toString()*/
                    var extraToppingPrice = "0.0"
                    val b: Double = java.lang.Double.valueOf(
                        binding.txtSubTotal.text.toString()
                            .replace(currencyType, "")
                    )
                    try {
                        if (textID.text.toString().trim().isNotEmpty()) {
                            //remove old price
                            extraToppingPrice = getOldPrice(
                                productContainData,
                                textID.text.toString().toInt()
                            )
                            toppingProductPrice -= extraToppingPrice.toDouble()
                            var subTotal = (b - (extraToppingPrice.toDouble() * count))
                            Log.d(
                                "TAG",
                                "OLDPrice: SingleSelectionPrice price = $extraToppingPrice subTo $subTotal"
                            )
                            //Add new price
                            val selectedItem = productContainData[position].addon_content_id
                            textID.text = selectedItem.toString()

                            extraToppingPrice = getOldPrice(
                                productContainData, textID.text.toString().toInt()
                            )
                            subTotal = (subTotal + (extraToppingPrice.toDouble() * count))

                            toppingProductPrice += extraToppingPrice.toDouble()
                            binding.txtSubTotal.text =
                                currencyType + convertToUSDFormat(
                                    (subTotal.toString())
                                )
                        } else {
                            val selectedItem = productContainData[position].addon_content_id
                            textID.text = selectedItem.toString()
                            extraToppingPrice = getOldPrice(
                                productContainData,
                                textID.text.toString().toInt()
                            )
                            toppingProductPrice += extraToppingPrice.toDouble()
                            binding.txtSubTotal.text =
                                currencyType + convertToUSDFormat(
                                    (b + (extraToppingPrice.toDouble() * count)).toString()
                                )
                        }
                    } catch (Ex: Exception) {
                        Log.d("TAG", "SingleSelectionPrice: Exception $Ex")
                    }


                } catch (Ex: Exception) {
                    Log.d("TAG", "onResponse: Exception $Ex")
                }

            }
        autoCompleteTextView.showDropDown()
    }

    override fun <T> onSuccessForProductAddONEdit(
        dataG: T, type: String, multiSelectText: TextView,
        strTitle: String, textID: TextView, testCount: Int, extraToppingPriceO: Double
    ) {

    }

    private fun getProductAddOnWithOutSize() {
        log("getProductAddOnWithOutSize ", "Called ")
        viewModel.productAddOnWithoutSize(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productId
        )
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun createDynamicLayoutForMultiSelect(
        strTitle: String, addOnId: Int, strSelectionType: String, minS: Int, maxS: Int,
        freeS: Int, availTime: String, position: Int
    ) {
        val inflater =
            LayoutInflater.from(this).inflate(R.layout.multi_selection_layout, null)

        binding.layoutAddons.addView(inflater, binding.layoutAddons.childCount)

        val v = binding.layoutAddons.getChildAt(layoutPosition)
        val textH = v.findViewById<TextView>(R.id.txtHeadingText)
        val textID = v.findViewById<TextView>(R.id.txtValueId)
        val txtSelectionType = v.findViewById<TextView>(R.id.txtSelectionType)
        val txtMinSelection = v.findViewById<TextView>(R.id.txtMinSelection)
        val multiSelectText = v.findViewById<TextView>(R.id.textSelectToppings)
        val txtConditions = v.findViewById<TextView>(R.id.txtConditions)
        val txtExtTotalPrice = v.findViewById<TextView>(R.id.txtExtTotalPrice)

        //For Sub Addons
        val txtExtraParentAddOnHeading = v.findViewById<TextView>(R.id.txtExtraParentAddOnHeading);
        val txtParentAddOnID = v.findViewById<TextView>(R.id.txtParentAddOnID)


        txtMinSelection.text = minS.toString()
        txtSelectionType.text = strSelectionType
        textH.text = strTitle

        txtConditions.text = "(Max $maxS |Free $freeS | Required $minS)"

        multiSelectText.hint = "Select $strTitle"
        productContainAddOnDataForMultiSelect(
            addOnId,
            multiSelectText,
            strTitle,
            textID,
            minS,
            maxS,
            freeS,
            availTime,
            txtExtraParentAddOnHeading,
            txtParentAddOnID,
            txtExtTotalPrice, position
        )
        layoutPosition += 1
    }


    private fun productContainAddOnDataForMultiSelect(
        addOnId: Int, multiSelectText: TextView, strTitle: String, textID: TextView,
        minS: Int, maxS: Int, freeS: Int, availTime: String, txtExtraParentAddOnHeading: TextView,
        txtParentAddOnID: TextView, txtExtTotalPrice: TextView, position: Int
    ) {
        viewModel.productContents2(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            addOnId, multiSelectText, strTitle, textID, minS, maxS, freeS, availTime,
            txtExtraParentAddOnHeading, txtParentAddOnID, txtExtTotalPrice, position
        )
    }

    @SuppressLint("SetTextI18n")
    fun setData(
        multiSelectText: TextView, strTitle: String, listItems12: ArrayList<String>,
        addOnDataCollection: ArrayList<ProductContainItem>, textID: TextView,
        minS: Int, maxS: Int, freeS: Int, availTime: String, txtExtraParentAddOnHeading: TextView,
        txtParentAddOnID: TextView, txtExtTotalPrice: TextView, position: Int
    ) {
        val listItems: Array<String> = listItems12.toTypedArray()
        val checkedItems = BooleanArray(listItems.size)
        val selectedItems: List<String> = listItems.asList()
        var selectionCount = 0
        var extraToppingPrice = 0.0
        var localCollectPrice = ArrayList<CollectPrice>()
        var b: Double


        //New Module with list view
        //Collect Data
        val collectData: ArrayList<AddonsSelectedDataModel> = arrayListOf()
        if (addOnDataCollection.size > 0) {

            for (data in addOnDataCollection) {
                collectData.add(
                    AddonsSelectedDataModel(
                        data.title,
                        data.price,
                        data.addon_content_id,
                        0,
                        false,
                        0,
                        data.is_subaddons,
                        data.parent_addon.addon_id,
                        data.suspended_until
                    )
                )
            }

            if (position == 0) {
                txtExtraParentAddOnHeading.text = subAddonName
                txtExtraParentAddOnHeading.visibility = View.VISIBLE
            }

            txtParentAddOnID.text = subAddonId.toString()

            multiSelectText.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                val customLayout: View =
                    layoutInflater.inflate(R.layout.activity_list_view_with_checkbox, null)
                builder.setView(customLayout)
                builder.setCancelable(false)
                val listView = customLayout.findViewById<View>(R.id.listView1) as ListView
                val textViewAlertHeading = customLayout.findViewById<TextView>(R.id.txtHeading)
                val btnClosedButton = customLayout.findViewById<AppCompatButton>(R.id.btnClosed)

                textViewAlertHeading.text = "Select $strTitle"

                val dataAdapter =
                    MultipleCheckBoxListAdapter(
                        this,
                        R.layout.country_info,
                        collectData,
                        maxS,
                        localCollectPrice, this, currencyType
                    )

                listView.adapter = dataAdapter

                val dialog = builder.create()
                dialog.show()
                btnClosedButton.setOnClickListener {
                    textID.text = ""
                    dialog.dismiss()
                    b = java.lang.Double.valueOf(
                        binding.txtSubTotal.text.toString().replace(currencyType, "")
                    )
                    val responseText = StringBuffer()
                    val sData: ArrayList<AddonsSelectedDataModel> =
                        dataAdapter.addonContentList
                    for (data in sData) {
                        if (data.isSelected) {
                            textID.text = "${textID.text}${data.addonContentID},"
                            if (data.totalCountInt > 0) {
                                responseText.append(
                                    "${data.addonContentName} (${
                                        currencyType
                                    }${data.addonContentPrice}) + ${data.totalCountInt}, "
                                )
                                var p = 0
                                while (p < data.totalCountInt) {
                                    textID.text = "${textID.text}${data.addonContentID},"
                                    ++p
                                }
                            } else
                                responseText.append(
                                    "${data.addonContentName} (${
                                        currencyType
                                    }${data.addonContentPrice}),"
                                )
                        }
                    }

                    var selectedText = responseText.toString()
                    // Log.d("TAG", "setDataContentName: "+selectedText.trim()+"== "+selectedText.trim().endsWith(","))
                    if (selectedText.trim().endsWith(",")) {
                        if (selectedText.contains("+"))
                            selectedText = selectedText.trim().substring(0, selectedText.length - 2)
                        else
                            selectedText = selectedText.trim().substring(0, selectedText.length - 1)
                    }
                    multiSelectText.text = selectedText

                    toppingProductPrice -= extraToppingPrice
                    b -= count * extraToppingPrice
                    localCollectPrice = dataAdapter.collectPrice
                    extraToppingPrice = updatePrice(localCollectPrice, freeS)
                    binding.txtSubTotal.text =
                        currencyType + convertToUSDFormat(
                            (b + (extraToppingPrice * count)).toString()
                        )
                    toppingProductPrice += extraToppingPrice
                    txtExtTotalPrice.text = extraToppingPrice.toString()
                }
            }

            multiSelectText.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                val customLayout: View =
                    layoutInflater.inflate(R.layout.activity_list_view_with_checkbox, null)
                builder.setView(customLayout)
                builder.setCancelable(false)
                val listView = customLayout.findViewById<View>(R.id.listView1) as ListView
                val textViewAlertHeading = customLayout.findViewById<TextView>(R.id.txtHeading)
                val btnClosedButton = customLayout.findViewById<AppCompatButton>(R.id.btnClosed)

                textViewAlertHeading.text = "Select $strTitle"
                val dataAdapter =
                    MultipleCheckBoxListAdapter(
                        this,
                        R.layout.country_info,
                        collectData,
                        maxS,
                        localCollectPrice, this,
                        currencyType
                    )
                listView.adapter = dataAdapter
                val dialog = builder.create()
                dialog.show()
                btnClosedButton.setOnClickListener {
                    textID.text = ""
                    dialog.dismiss()
                    b = java.lang.Double.valueOf(
                        binding.txtSubTotal.text.toString().replace(
                            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                            ""
                        )
                    )
                    val responseText = StringBuffer()
                    val sData: ArrayList<AddonsSelectedDataModel> =
                        dataAdapter.addonContentList
                    for (data in sData) {
                        if (data.isSelected) {
                            textID.text = "${textID.text}${data.addonContentID},"
                            if (data.totalCountInt > 0) {
                                responseText.append(
                                    "${data.addonContentName} (${
                                        PreferenceProvider(
                                            applicationContext
                                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!!
                                    }${data.addonContentPrice}) + ${data.totalCountInt}, "
                                )
                                var p = 0
                                while (p < data.totalCountInt) {
                                    textID.text = "${textID.text}${data.addonContentID},"
                                    ++p
                                }
                            } else
                                responseText.append(
                                    "${data.addonContentName} (${
                                        PreferenceProvider(
                                            applicationContext
                                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!!
                                    }${data.addonContentPrice}),"
                                )
                        }
                    }
                    multiSelectText.text = responseText

                    toppingProductPrice -= extraToppingPrice
                    b -= count * extraToppingPrice
                    localCollectPrice = dataAdapter.collectPrice
                    extraToppingPrice = updatePrice(localCollectPrice, freeS)
                    binding.txtSubTotal.text =
                        PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + convertToUSDFormat(
                            (b + (extraToppingPrice * count)).toString()
                        )
                    toppingProductPrice += extraToppingPrice
                    txtExtTotalPrice.text = extraToppingPrice.toString()
                }
            }
        }
    }

    private fun updatePrice(priceData: ArrayList<CollectPrice>, freeS: Int): Double {

        log("updatePrice ", " Size ${priceData.size}")
        var price = 0.0
        for (i in 0 until priceData.size) {
            price += if (i < freeS)
                0.0
            else
                priceData[i].price!!
        }
        return price
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams", "SetTextI18n")
    private fun createDynamicLayoutForAnyOne(
        strTitle: String, addOnId: Int,
        strSelectionType: String, minS: Int?, maxS: Int?, freeS: Int?
    ) {
        val inflater =
            LayoutInflater.from(applicationContext).inflate(R.layout.single_selection_layout, null)

        binding.layoutAddons.addView(inflater, binding.layoutAddons.childCount)

        val v = binding.layoutAddons.getChildAt(layoutPosition)
        val textH = v.findViewById<TextView>(R.id.txtHeadingText)
        val textID = v.findViewById<TextView>(R.id.txtValueId)
        val txtMinSelection = v.findViewById<TextView>(R.id.txtMinSelection)
        val txtSelectionType = v.findViewById<TextView>(R.id.txtSelectionType)
        val txtConditions = v.findViewById<TextView>(R.id.txtConditions)
        val autoTextView = v.findViewById<AutoCompleteTextView>(R.id.singleSelection)
        val txtParentAddOnID = v.findViewById<TextView>(R.id.txtParentAddOnID)
        val txtExtTotalPrice = v.findViewById<TextView>(R.id.txtExtTotalPrice)

        txtMinSelection.text = minS.toString()

        textH.text = strTitle
        // txtConditions.text = "(max $maxS | Required $minS)"

        autoTextView.hint = "Select $strTitle"
        txtSelectionType.text = strSelectionType
        autoTextView.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) autoTextView.showDropDown() }
        autoTextView.setOnTouchListener { _, _ ->
            autoTextView.showDropDown()
            false
        }
        autoTextView.setAdapter(
            ArrayAdapter(
                applicationContext,
                R.layout.autocomplete_item_layout,
                arrayListOf<String>()
            )
        )
        autoTextView.setOnClickListener {
            try {
                val c = autoTextView.adapter.count
                if (c == 0) {
                    productContainAddOnData(addOnId, autoTextView, textID)
                }
            } catch (ex: Exception) {
                productContainAddOnData(addOnId, autoTextView, textID)
            }
        }
        layoutPosition += 1
    }

    private fun productContainAddOnData(
        addOnId: Int, autoComplete: AutoCompleteTextView, textID: TextView
    ) {
        viewModel.productContents2(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            addOnId,
            autoComplete,
            textID
        )
    }

    //Size Selection Operation
    var tempSizeID = 0

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun sizeOnClick(item: Any, name: String, prise: String, autoCall: Boolean) {
        if (name == "size" && tempSizeID != item as Int) {
            selectedPosition.clear()
            sizeSelect = true
            sizeId = item.toString()
            sizePrice = prise.toDouble()
            binding.txtSubTotal.text =
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + prise
            binding.txtQuantity.text = "1"
            count = 1
            toppingProductPrice = 0.0
            checkCount = 0
            finalProductPrice = java.lang.Double.valueOf(prise)
            productAddon.clear()
            layoutPosition = 0
            binding.layoutAddons.removeAllViews()
            getProductAddOnWithSize(item as Int)
        }
    }

    private fun getProductAddOnWithSize(catSizeId: Int) {
        viewModel.productAddOnWithSize(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productId, catSizeId.toString()
        )
    }

    private fun retrieveData(): Boolean {
        var i = 0
        addOnSingleCollection = ""
        addOnMultiCollection = ""

        Log.d("TAG", "retrieveData: Type 444 ")
        while (i < binding.layoutAddons.childCount) {
            val v = binding.layoutAddons.getChildAt(i)
            val textSelectionType = v.findViewById<TextView>(R.id.txtSelectionType)
            val textID = v.findViewById<TextView>(R.id.txtValueId)
            val txtHeadingText = v.findViewById<TextView>(R.id.txtHeadingText)
            val txtMinSelection = v.findViewById<TextView>(R.id.txtMinSelection)
            val txtParentAddOnID = v.findViewById<TextView>(R.id.txtParentAddOnID)
            val minSelectionCount = (txtMinSelection.text.toString()).toInt()

            var s = textID.text.toString().trim()
            var parentAddonForThirdLayer = txtParentAddOnID.text.toString().trim()

            if (s.trim().endsWith(",")) {
                s = s.substring(0, s.trim().length - 1)
            }

            if (textSelectionType.text.toString().trim().equals("any one", ignoreCase = true)) {
                if (s.trim().isEmpty() || s.trim().isBlank()) {
                    Toast.makeText(
                        applicationContext,
                        "Please select ${txtHeadingText.text}",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                } else {
                    addOnSingleCollection = "$addOnSingleCollection$s,"
                }
            } else if (textSelectionType.text.toString().trim()
                    .equals("multiple", ignoreCase = true)
            ) {
                if (minSelectionCount == 0 || (s.trim().isNotEmpty() && s.trim()
                        .isNotBlank() && s.trim()
                        .split(",").size >= minSelectionCount)
                ) {
                    // addOnMultiCollection = "$addOnMultiCollection$s,"
                    var idArray = s.trim().split(",")
                    for (n in idArray) {
                        if (n.length > 1)
                            addOnMultiCollection =
                                "$addOnMultiCollection$n@$parentAddonForThirdLayer,"
                    }
                } else {
                    if (minSelectionCount > 1) {
                        Toast.makeText(
                            applicationContext,
                            "Min $minSelectionCount '${txtHeadingText.text}' are required",
                            Toast.LENGTH_LONG
                        ).show()
                    } else
                        Toast.makeText(
                            applicationContext,
                            "Please select ${txtHeadingText.text}",
                            Toast.LENGTH_LONG
                        ).show()
                    return false
                }
            }
            i++
        }
        try {
            if (binding.layoutAddons.childCount > 0) {
                addOnSingleCollection =
                    addOnSingleCollection.substring(0, addOnSingleCollection.length - 1)
                addOnMultiCollection =
                    addOnMultiCollection.substring(0, addOnMultiCollection.length - 1)
            }
        } catch (ex: Exception) {
            Log.d("TAG", "retrieveData: Exception $ex")
        }

        return true
    }

    private fun addToCart() {

        val b: Double = java.lang.Double.valueOf(
            binding.txtSubTotal.text.toString()
                .replace(
                    PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                    ""
                )
        )

        val qty = binding.txtQuantity.text.toString().trim().toDouble()
        val jsonArray1 = JsonArray()
        val jsonObject = JsonObject()

        for (i in addOnSingleCollection.split(",").indices) {
            if (addOnSingleCollection.split(",")[i].trim().isNotEmpty())
                jsonArray1.add(addOnSingleCollection.split(",")[i])
        }
        for (i in addOnMultiCollection.split(",").indices) {
            if (addOnMultiCollection.split(",")[i].trim().isNotEmpty())
                jsonArray1.add(addOnMultiCollection.split(",")[i])
        }

        jsonObject.addProperty("price", b)
        jsonObject.addProperty("extra", binding.edtSpclInstruction.text.toString())
        jsonObject.addProperty("quantity", qty.toInt())
        jsonObject.addProperty(
            "cart_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
        )
        jsonObject.addProperty("product_id", productId)
        jsonObject.addProperty("size_id", sizeId)
        jsonObject.add("addon_content_list", jsonArray1)

        Log.d("DetailScreen2 ", " addToCart: $jsonObject")

        viewModel.addToCart(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            jsonObject
        )
    }

    private fun onCartButtonClick() {
        val checkValidation = retrieveData()
        if (checkValidation)
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.ONLY_DELIVERY))
                toast("Note: Since you have a Delivery Deal in your cart you cannot choose Collection deal too.")
            else if ((PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.ONLY_PICKUP)))
                toast("Note: Since you have a Collection Deal in your cart you cannot have Delivery deal too.")
            else if (sizeApi != 0 && !sizeSelect) {
                Toast.makeText(applicationContext, "Please select size", Toast.LENGTH_SHORT).show()
            } else
                addToCart()
    }

    private fun showLoginDialog(activity: Activity, title: String, message: String) {
        android.app.AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog?.dismiss()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.putExtra("callFrom", "AddToCart")
                activity.startActivity(intent)
                dialog!!.dismiss()
            }.show()
    }

    var subAddonId = 0
    var subAddonName = ""

    @SuppressLint("SetTextI18n")
    override fun onRecyclerViewItemSelect(
        check: Boolean, addonId: Int, addonContentId: Int, addonName: String
    ) {
        subAddonId = addonContentId
        subAddonName = addonName
        if (check) {
            viewModel.getSubAddonsForAddons(
                "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                addonContentId, addonContentId, addonName
            )
        } else {
            val layoutCount = binding.layoutAddons.childCount
            Log.d("LayoutCount ==", "layoutCount $layoutCount")
            for (i in layoutCount downTo 1) {
                val v = binding.layoutAddons.getChildAt(i - 1)
                val txtParentAddOnID = v.findViewById<TextView>(R.id.txtParentAddOnID)
                val txtExtTotalPrice = v.findViewById<TextView>(R.id.txtExtTotalPrice)

                if (txtParentAddOnID.text.isNotEmpty() && txtParentAddOnID.text.toString().trim()
                        .toInt() == addonContentId as Int
                ) {
                    var tempPrice = 0.0
                    if (txtExtTotalPrice.text.trim().isNotEmpty())
                        tempPrice = txtExtTotalPrice.text.toString().toDouble()

                    toppingProductPrice -= tempPrice
                    val b = java.lang.Double.valueOf(
                        binding.txtSubTotal.text.toString().replace(
                            currencyType,
                            ""
                        )
                    )
                    binding.txtSubTotal.text = currencyType + convertToUSDFormat(
                        (b - (tempPrice * count)).toString()
                    )
                    binding.layoutAddons.removeViewAt(i - 1)
                    layoutPosition -= 1
                }
            }
        }
    }

    private fun getOldPrice(productContainData: ArrayList<ProductContainItem>, id: Int): String {
        //   Log.d("TAG", "SingleSelectionPrice: ID= $id")
        val price = "0.0"
        for (element in productContainData) {
            if (element.addon_content_id == id) {
                return element.price
            }
        }
        return price
    }
}

data class CollectPrice(
    val name: String?,
    val price: Double?
)