package com.instaapp.yashaswinifresh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActivityBillingAddressBinding
import com.instaapp.yashaswinifresh.listener.CheckOutListener
import com.instaapp.yashaswinifresh.network.requestModel.AddBillingShippingAddressRequest
import com.instaapp.yashaswinifresh.network.responseModel.AddressListResponse
import com.instaapp.yashaswinifresh.network.responseModel.AddressListResultResponse
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.CheckOutModel
import com.instaapp.yashaswinifresh.viewModelFactory.CheckOutViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class BillingAddressActivity : AppCompatActivity(), KodeinAware, CheckOutListener {

    override val kodein by kodein()
    private lateinit var viewModel: CheckOutModel
    private val factory: CheckOutViewModelFactory by instance()
    private lateinit var binding: ActivityBillingAddressBinding
    private var isBillAddressAvail = false
    private var addressid: String = ""
    private lateinit var addresslist: List<AddressListResultResponse?>
    var shipTax = ""
    private var latitude = "0"
    private var longitude = "0"
    private var shippingAddress = ""
    private var billingAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_billing_address)
        viewModel = ViewModelProvider(this, factory)[CheckOutModel::class.java]
        viewModel.checkOutListener = this

        shippingAddress = intent.getStringExtra("shippingAddress")!!
        shipTax = intent.getStringExtra("tax").toString()
        latitude = intent.getStringExtra("latitude")!!
        longitude = intent.getStringExtra("longitude")!!

        binding.toolBar.txtHeading.text = getString(R.string.billing_address)

        binding.toolBar.imgBack.setOnClickListener { finish() }

        binding.btnNext.setOnClickListener {
            performNextAction()
        }

        getBillingAddress()
        binding.checkboxCurrentAddress.isEnabled = false

        binding.checkboxCurrentAddress.setOnClickListener {
            if (binding.checkboxCurrentAddress.isChecked) {
                binding.enableValue = false
                isBillAddressAvail = true
                binding.layoutAddress.visibility = View.GONE
            } else {
                isBillAddressAvail = false
                binding.enableValue = true
                binding.layoutAddress.visibility = View.VISIBLE
            }
        }

        binding.toolBar.cartlayout.visibility=View.GONE
        binding.toolBar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()
        binding.toolBar.cartlayout.setOnClickListener {
            startActivity(Intent(applicationContext, CartListActivity::class.java))
        }
    }

    private fun getBillingAddress() {
        viewModel.getBillingAddressData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )
    }

    private fun performNextAction() {
        if (isBillAddressAvail && binding.checkboxCurrentAddress.isChecked) {
            val intent = Intent(applicationContext, PaymentActivity::class.java)
            intent.putExtra("shippingAddress", shippingAddress)
            intent.putExtra("billingAddress", billingAddress)
            intent.putExtra("tax", shipTax)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            startActivity(intent)
            finish()
        } else {
            validation()
        }
    }

    private fun validation() {
        if (binding.etFullName.text.toString().trim().isEmpty()) {
            toast("Enter your full name")
        } else if (binding.etAptNo.text.toString().trim().isEmpty()) {
            toast("Enter your House/APT no.")
        } else if (binding.etZipCode.text.toString().trim().isEmpty()) {
            toast("Enter zip/postal code")
        } else if (binding.etAddressLine.text.toString().trim().isEmpty()) {
            toast("Enter address")
        } else if (binding.etCity.text.toString().trim().isEmpty()) {
            toast("Enter city")
        } else if (binding.etState.text.toString().trim().isEmpty()) {
            toast("Enter state")
        } else if (binding.etCountry.text.toString().trim().isEmpty()) {
            toast("Enter country")
        } else {
            billingAddress = "${binding.etFullName.text.toString().trim()}, ${
                binding.etAptNo.text.toString().trim()
            }, " +
                    "${binding.etAddressLine.text.toString().trim()}, ${
                        binding.etCity.text.toString().trim()
                    }," +
                    " ${binding.etState.text.toString().trim()}, ${
                        binding.etCountry.text.toString().trim()
                    }, ${binding.etZipCode.text.toString().trim()}"
            if (addresslist.isNotEmpty()) {
                viewModel.updateBillingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    addressid,
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etAptNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etZipCode.text.toString().trim(),
                        binding.etAddressLine.text.toString().trim(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etFullName.text.toString().trim(),
                        "0.0",
                        "0.0"
                    )
                )
            } else {
                viewModel.addBillingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etAptNo.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etZipCode.text.toString().trim(),
                        binding.etAddressLine.text.toString().trim(),
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etFullName.text.toString().trim(),
                        "0.0",
                        "0.0"
                    )
                )
            }
        }
    }

    override fun onStarted() {
        binding.progressLayout.progressLayout.visibility = View.VISIBLE

    }

    override fun onFailure(message: String, type: String) {
        binding.progressLayout.progressLayout.visibility = View.GONE
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
        binding.progressLayout.progressLayout.visibility = View.GONE
        if (type == "getBillingAddressData") {
            val data: AddressListResponse = dataG as AddressListResponse
            addresslist = data.results
            if (addresslist.isNotEmpty()) {
                binding.enableValue = false
                binding.addressData = data
                binding.txtNote.visibility = View.VISIBLE
                binding.checkboxCurrentAddress.isEnabled = true
                binding.checkboxCurrentAddress.isChecked = true
                addressid = addresslist[0]!!.id.toString()
                billingAddress =
                    " ${addresslist[0]!!.name}, ${addresslist[0]!!.house_number}, ${addresslist[0]!!.address}, " +
                            "${addresslist[0]!!.city}, ${addresslist[0]!!.state}, ${addresslist[0]!!.country}, ${addresslist[0]!!.zip}"
                isBillAddressAvail = true
            } else {
                binding.enableValue = true
                binding.layoutAddress.visibility = View.VISIBLE
            }
        } else if (type == "addBillingAddress" || type == "updateBillingAddress") {
            val intent = Intent(applicationContext, PaymentActivity::class.java)
            intent.putExtra("tax", shipTax)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("shippingAddress", shippingAddress)
            intent.putExtra("billingAddress", billingAddress)
            startActivity(intent)
            finish()
        }
    }
}