package com.instaapp.veggietemp1.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.veggietemp1.interface_class.RecyclerViewClickListener
import com.instaapp.veggietemp1.network.responseModel.OrderListResponse
import com.instaapp.veggietemp1.ui.adapter.OrderListDataAdapter
import com.instaapp.veggietemp1.utils.PreferenceKey
import com.instaapp.veggietemp1.utils.PreferenceProvider
import com.instaapp.veggietemp1.utils.StaticValue
import com.instaapp.veggietemp1.utils.toast
import com.instaapp.veggietemp1.viewModel.OrderViewModel
import com.instaapp.veggietemp1.viewModelFactory.OrderViewModelFactory
import com.instaapp.vegiestemp1.databinding.ActivityOrderListBinding
import com.instaapp.vegiestemp1.databinding.ToolbarWithCartBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class OrderListActivity : AppCompatActivity(), KodeinAware, RecyclerViewClickListener {
    override val kodein by kodein()
    private lateinit var viewModel: OrderViewModel
    private val factory: OrderViewModelFactory by instance()
    lateinit var adapter: OrderListDataAdapter
    private var binding: ActivityOrderListBinding? = null
    private lateinit var toolbar: ToolbarWithCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]

        toolbar = binding!!.toolbar
        toolbar.txtHeading.text = "Your Orders"
        toolbar.imgBack.visibility = View.VISIBLE
        toolbar.imgBack.setOnClickListener { finish() }
        toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
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
        loadOrderData()
    }

    private fun loadOrderData() {
        binding!!.progress.progressLayout.visibility = View.VISIBLE
        viewModel.getOrderListData(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!}",
            StaticValue.REST_ID,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
        viewModel.orderListData.observe(this) { orderData ->
            binding!!.rlcOrderList.also {
                it.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = OrderListDataAdapter(orderData, this,applicationContext)
                binding!!.progress.progressLayout.visibility = View.GONE
            }
        }
    }
    override fun <T> onRecyclerItemClick(view: View, dataG: T) {
        val data: OrderListResponse = dataG as OrderListResponse
        val intent = Intent(applicationContext, OrderDetailsActivity::class.java)
        intent.putExtra("order_data", data)
        startActivity(intent)
    }
}