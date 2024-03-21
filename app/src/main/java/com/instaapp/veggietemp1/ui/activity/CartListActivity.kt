package com.instaapp.veggietemp1.ui.activity


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.instaapp.veggietemp1.interface_class.RecyclerViewCartClickListener
import com.instaapp.veggietemp1.listener.CartListListener
import com.instaapp.veggietemp1.network.responseModel.CartListResponse
import com.instaapp.veggietemp1.network.responseModel.CartListResult
import com.instaapp.veggietemp1.ui.adapter.CartListDataAdapter
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.CartViewModel
import com.instaapp.veggietemp1.viewModelFactory.CartViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityCartBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CartListActivity : AppCompatActivity(), KodeinAware, RecyclerViewCartClickListener,
    CartListListener {
    override val kodein by kodein()
    private lateinit var viewModel: CartViewModel
    private val factory: CartViewModelFactory by instance()
    private var cartTotal: Double = 0.0
    private var cartCount: Int = 0
    var badge: BadgeDrawable? = null
    private var isAvailableTime = true
    private var itemName = ""
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[CartViewModel::class.java]
        viewModel.cartListener = this
        binding.toolbar.txtHeading.text = "Your Cart"
        binding.toolbar.imgBack.visibility = View.VISIBLE
        binding.toolbar.imgBack.setOnClickListener { finish() }

        binding.cartCheckoutBtn.setOnClickListener {
            if (cartCount > 0) {
                if (isAvailableTime) {
                    val intent = Intent(applicationContext, CheckoutActivity::class.java)
                    intent.putExtra("total_price", cartTotal)
                    startActivity(intent)
                } else {
                    toast("You have an item in your cart that is not available right now!")
                }
            } else {
                showAlert(
                    this,
                    applicationContext.getString(R.string.warning2),
                    applicationContext.getString(R.string.no_items_to_place_order)
                )
            }
        }
        loadCartData()
    }

    private fun loadCartData() {
        log("CartListActivity", " =loadCartData")
        isAvailableTime = true
        viewModel.getCartListData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID).toString(),
            StaticValue.REST_ID,
        )
    }

    override fun addSubDeleteItem(
        cartData: CartListResult, product_id: String, position: Int,
        sequence: String, price: Double, count: String, check: String, cartItemId: String
    ) {
        if (check != "Delete") {
            val jsonArray1 = JsonArray()
            val jsonObject = JsonObject()
            for (i in 0 until cartData.addon_content!!.size) {
                jsonArray1.add(cartData.addon_content[i].addon_content!!.addon_content_id.toString())
            }
            jsonObject.addProperty("price", price)
            jsonObject.addProperty("extra", "")
            jsonObject.addProperty("quantity", count.toInt())
            jsonObject.addProperty(
                "cart_id",
                cartItemId.toInt()
            )
            jsonObject.addProperty("product_id", product_id.toInt())
            jsonObject.addProperty("size_id", sequence.replace("null", ""))
            jsonObject.add("addon_content_list", jsonArray1)
            viewModel.updateCart(
                "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                cartItemId, jsonObject
            )
        } else {
            deleteWarning(cartItemId)
        }
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE

    }

    @SuppressLint("SetTextI18n")
    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        log("CartListActivity ", " onSuccess = $type")
        if (type == "getCartListData") {
            val cartData: CartListResponse = dataG as CartListResponse
            val layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            binding.recyclerViewCart.layoutManager = layoutManager
            binding.recyclerViewCart.setHasFixedSize(true)
            binding.recyclerViewCart.adapter =
                CartListDataAdapter(cartData.results, this, applicationContext)

            cartCount = cartData.count!!
            cartTotal = cartData.total_cost!!.toDouble()
            log("loadCartData ", " onSuccess Count ${cartData.results.size}")
            binding.cartCheckoutBtn.text =
                "Checkout " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + cartData.total_cost
            getCartAndProductID(cartData.results)
            if (cartData.results.isNotEmpty()) {
                itemName = cartData.results[0].product!!.product_name
                PreferenceProvider(applicationContext).setStringValue(
                    itemName, PreferenceKey.ITEM_NAME
                )
                PreferenceProvider(applicationContext).setIntValue(
                    cartData.results.size,
                    PreferenceKey.CART_COUNT
                )
                for (Dm in cartData.results) {
                    log("loadCartData ", " Size ${cartData.results.size}===$isAvailableTime")
                    if (isAvailableTime) {
                        // available_time
                        val catAvailTime = Dm.category_available_time
                        val time = Dm.product!!.available_time
                        val suspendedUntil = Dm.product.suspended_until
                        if ((!catAvailTime.equals("All Time", ignoreCase = true)
                                    && checkAvailableTime(convertGMTtoLocalTime(catAvailTime!!))) || (!time.equals(
                                "All Time", ignoreCase = true
                            ) && checkAvailableTime(convertGMTtoLocalTime(time)))
                            || !parseDateSuspendedMenu(suspendedUntil)
                            || Dm.category_status.equals("INACTIVE", ignoreCase = true)
                            || !Dm.product.in_stock
                            || !Dm.cart_item_in_stock
                            || !Dm.master_category_open_hour
                            || !parseDateSuspendedMenu(suspendedUntil)
                        ) {
                            isAvailableTime = false
                        }
                    }
                }
            } else
                PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_COUNT)
        } else
            loadCartData()
    }

    private fun deleteWarning(cartItemId: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtMsg = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        Glide.with(this)
            .load(ContextCompat.getDrawable(applicationContext, R.drawable.ic_logo_instaapp))
            .into(imgAppLogo)

        button.text = "Delete"
        txtHeading.text = getString(R.string.warning2)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        txtMsg.text = resources.getString(R.string.dialog_delete_item_txt)

        (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
            viewModel.deleteCartItem(
                "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                cartItemId
            )
            dialog.dismiss()
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun getCartAndProductID(results: List<CartListResult>) {
        val cartProductIds = results.map {
            Triple(it.cart_item_id, it.product?.product_id, it.quantity ?: -1)
        }
        CartManager.updateCartProductIds(cartProductIds)
    }
}