package com.instaapp.yashaswinifresh.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActivityOrderDetailScreenBinding
import com.instaapp.yashaswinifresh.listener.HomeListener
import com.instaapp.yashaswinifresh.network.responseModel.OrderListResponse
import com.instaapp.yashaswinifresh.network.responseModel.OrderMenuDetail
import com.instaapp.yashaswinifresh.ui.adapter.OrderDetailsMenuAdapter
import com.instaapp.yashaswinifresh.utils.PreferenceKey
import com.instaapp.yashaswinifresh.utils.PreferenceProvider
import com.instaapp.yashaswinifresh.utils.log
import com.instaapp.yashaswinifresh.utils.toast
import com.instaapp.yashaswinifresh.viewModel.OrderViewModel
import com.instaapp.yashaswinifresh.viewModelFactory.OrderViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class OrderDetailsActivity : AppCompatActivity(), KodeinAware, HomeListener {

    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    private lateinit var derivedObject: OrderListResponse
    private lateinit var binding: ActivityOrderDetailScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail_screen)
        // binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        try {
            derivedObject = intent.getSerializableExtra("order_data") as OrderListResponse
            binding.orderDetailsData = derivedObject
        } catch (ignore: Exception) {
        }
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        viewModel.homeListener = this
        loadOrderData(derivedObject.order!!.order_id)
        binding.toolbar.txtHeading.text = getString(R.string.order_details)
        binding.toolbar.imgBack.setOnClickListener { finish() }

        binding.toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
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

        if (derivedObject.shippingmethod.name!!.contains("Delivery")) {
            binding.txtAddress.text = derivedObject.order!!.shipping_address_text
            binding.txtAddressHeading.text = "Delivery To :"
        } else {
            binding.txtAddressHeading.text = "Pickup At :"
            binding.txtAddress.text = derivedObject.restaurant.address
        }

    }


    private fun loadOrderData(orderID: Int?) {
        log("loadOrderData :", " $orderID")
        viewModel.getOrderedMenu(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            orderID.toString()
        )
    }

    override fun onStarted() {

    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(data: String, type: String) {

    }

    override fun <T> onSuccessData(dataG: T, type: String) {
        val data: OrderMenuDetail = dataG as OrderMenuDetail
        if (data.results!!.isNotEmpty()) {
            val layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            binding.recyclerMyOrderDetails.layoutManager = layoutManager
            binding.recyclerMyOrderDetails.setHasFixedSize(true)
            log("OrderMenuData ", " " + data.results.size)
            binding.recyclerMyOrderDetails.adapter = OrderDetailsMenuAdapter(data.results)
        }

    }
}