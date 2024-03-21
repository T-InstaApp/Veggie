package com.instaapp.yashaswinifresh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActitivyCheckoutBinding
import com.instaapp.yashaswinifresh.listener.CheckOutListener
import com.instaapp.yashaswinifresh.network.responseModel.*
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.CheckOutModel
import com.instaapp.yashaswinifresh.viewModelFactory.CheckOutViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CheckoutActivity : AppCompatActivity(), KodeinAware, CheckOutListener {
    override val kodein by kodein()
    private lateinit var viewModel: CheckOutModel
    private val factory: CheckOutViewModelFactory by instance()
    private lateinit var binding: ActitivyCheckoutBinding
    private var isShipAddressAvail = false
    private var isBillAddressAvail = false
    private var isPickUp = false
    private var isShipAddAvail = false
    private var cartTotal: Double = 0.0
    private var shippingAddressID: Int = 0
    private var billingAddressID: Int = 0
    private lateinit var addresslist: List<AddressListResultResponse?>

    lateinit var taxData: ArrayList<StateListResponse>
    lateinit var stateNameList: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.actitivy_checkout)
        stateNameList = arrayListOf()
        viewModel = ViewModelProvider(this, factory)[CheckOutModel::class.java]
        viewModel.checkOutListener = this

        cartTotal = intent.getDoubleExtra("total_price", 0.0)
        taxData = arrayListOf()

        PreferenceProvider(applicationContext).setStringValue(
            cartTotal.toString(), PreferenceKey.CART_TOTAL
        )

        binding.toolBar.txtHeading.text = "Checkout"

        binding.toolBar.imgBack.setOnClickListener { finish() }
        binding.checkboxCurrentAddress.isEnabled = false
        getBillingAddress()

        viewModel.getStateList(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)
            }"
        )

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            if (checkedId > -1) {
                binding.btnNext.visibility = View.VISIBLE
                if (rb.text.equals("Pickup")) {
                    if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.ONLY_DELIVERY)) {
                        showAlert(
                            this,
                            getString(R.string.alert),
                            getString(R.string.only_delivery)
                        )
                        rb.isChecked = false
                    } else {
                        isPickUp = true
                        isShipAddressAvail = false
                        PreferenceProvider(applicationContext).setStringValue(
                            PreferenceKey.DELIVERY_TYPE,
                            "PICKUP"
                        )
                        binding.btnAddNewAddress.visibility = View.GONE
                        PreferenceProvider(applicationContext).setIntValue(
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_PICKUP_ID),
                            PreferenceKey.SHIPPING_ID
                        )
                        isShipAddAvail = true
                        binding.layoutCurrentShippingAddress.visibility = View.GONE
                        binding.checkboxCurrentAddress.isChecked = false
                    }
                } else if (rb.text.equals("Delivery")) {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnAddNewAddress.visibility = View.VISIBLE
                    if (cartTotal < 10.0) {
                        showAlert(
                            this,
                            getString(R.string.alert),
                            "Delivery option is available for a minimum purchase of ${
                                PreferenceProvider(
                                    applicationContext
                                ).getStringValue(PreferenceKey.CURRENCY_TYPE)
                            }10"
                        )
                        rb.isChecked = false
                    } else if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.ONLY_PICKUP)) {
                        showAlert(
                            this,
                            getString(R.string.alert),
                            getString(R.string.only_pickup)
                        )
                        rb.isChecked = false
                    } else {
                        isPickUp = false
                        isShipAddressAvail = true
                        isShipAddAvail = false
                        PreferenceProvider(applicationContext).setStringValue(
                            PreferenceKey.DELIVERY_TYPE,
                            "SHIPMENT"
                        )
                        binding.btnAddNewAddress.visibility = View.VISIBLE
                        PreferenceProvider(applicationContext).setIntValue(
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID),
                            PreferenceKey.SHIPPING_ID,
                        )
                        binding.layoutCurrentShippingAddress.visibility = View.VISIBLE
                        binding.txtNote.visibility = View.VISIBLE
                        getShippingAddress()
                    }
                }
            }
        }

        binding.checkboxCurrentAddress.isEnabled = false

        binding.checkboxCurrentAddress.setOnClickListener {
            if (binding.checkboxCurrentAddress.isChecked) {
                binding.enableValue = false
                isShipAddressAvail = true
                binding.btnAddNewAddress.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
            } else {
                isShipAddressAvail = false
                binding.enableValue = true
                binding.btnAddNewAddress.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
            }
        }

        getShippingFee()

        binding.btnNext.setOnClickListener {
            if (isPickUp || binding.checkboxCurrentAddress.isChecked)
                onSubmitButtonClick()
            else
                toast("Please select/Add address first")
        }

        binding.btnAddNewAddress.setOnClickListener {
            val intent = Intent(applicationContext, AddAddressActivity::class.java)
            intent.putExtra("isShippingAddressAvailable", isShipAddressAvail)
            intent.putExtra("isBillingAddressAvailable", isBillAddressAvail)
            intent.putExtra("shippingAddressID", shippingAddressID)
            intent.putExtra("billingAddressID", billingAddressID)
            intent.putExtra("tax", "0")
            startActivity(intent)
            finish()
        }
        binding.toolBar.cartlayout.visibility=View.GONE
        binding.toolBar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
        binding.toolBar.cartlayout.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS)) {
                if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                    if (PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT) > 0) {
                        startActivity(Intent(applicationContext, CartListActivity::class.java))
                    } else
                        toast("Your cart is empty!")
                } else
                    toast("Please login first!")
            } else
                toast("Sorry, we are closed!")
        }
    }

    private fun getShippingAddress() {
        viewModel.getShippingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun getBillingAddress() {
        viewModel.getBillingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun getShippingFee() {
        viewModel.getShippingDetails(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID
        )
    }

    override fun onStarted() {

    }

    override fun onFailure(message: String, type: String) {
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

            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        if (type == "getShippingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            if (data.results.isNotEmpty()) {
                addresslist = data.results
                binding.addressData = data
                isShipAddAvail = true
                binding.checkboxCurrentAddress.isChecked = true
                binding.checkboxCurrentAddress.isEnabled = true
                binding.btnAddNewAddress.visibility = View.GONE
                binding.btnNext.visibility = View.VISIBLE
                shippingAddressID = addresslist[0]!!.id!!
                isShipAddressAvail = true
                PreferenceProvider(applicationContext).setStringValue(
                    addresslist[0]!!.address!!,
                    PreferenceKey.DELIVERY_ADDRESS
                )
            } else {
                isShipAddAvail = false
                isShipAddressAvail = false
                binding.btnAddNewAddress.visibility = View.VISIBLE
                binding.btnNext.visibility = View.GONE
            }
        } else if (type == "getBillingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            if (data.results.isNotEmpty()) {
                billingAddressID = data.results[0]!!.id!!
                isBillAddressAvail = true
            } else {
                isBillAddressAvail = false
            }
        } else if (type == "getShippingDetails") {
            val data: ShipmentResponse = dataG as ShipmentResponse
            if (data.results!!.isEmpty())
                showAlert(
                    this,
                    getString(R.string.alert),
                    getString(R.string.no_shipping_available)
                )
        } else if (type == "getStateList") {
            val data: StateListRequest = dataG as StateListRequest
            taxData = arrayListOf()
            taxData = data.results!!
            if (taxData.size > 0) {
                for (i in 0 until taxData.size) {
                    stateNameList.add(taxData[i].state!!)
                }
            }
        }
    }

    private fun onSubmitButtonClick() {
        val intent = Intent(applicationContext, BillingAddressActivity::class.java)
        if (!isPickUp) {
            val shippingAddress =
                " ${addresslist[0]!!.name}, ${addresslist[0]!!.house_number}, ${addresslist[0]!!.address}, " +
                        "${addresslist[0]!!.city}, ${addresslist[0]!!.state}, ${addresslist[0]!!.country}, ${addresslist[0]!!.zip}"
            intent.putExtra("shippingAddress", shippingAddress)
            intent.putExtra("tax", "0")
            intent.putExtra("latitude", addresslist[0]!!.latitude)
            intent.putExtra("longitude", addresslist[0]!!.longitude)
        } else {
            intent.putExtra("shippingAddress", "")
            intent.putExtra("tax", "0")
            intent.putExtra("latitude", "")
            intent.putExtra("longitude", "")
        }
        startActivity(intent)
    }
}