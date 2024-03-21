package com.instaapp.yashaswinifresh.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActivityPaymentBinding
import com.instaapp.yashaswinifresh.databinding.ToolbarWithCartBinding
import com.instaapp.yashaswinifresh.listener.PaymentListener
import com.instaapp.yashaswinifresh.network.requestModel.AddFreeRequest
import com.instaapp.yashaswinifresh.network.requestModel.PrintOrderRequest
import com.instaapp.yashaswinifresh.network.requestModel.UpdateOrderRequest
import com.instaapp.yashaswinifresh.network.responseModel.FreeResponse
import com.instaapp.yashaswinifresh.network.responseModel.OrderDetailResponse
import com.instaapp.yashaswinifresh.network.responseModel.ProfileResponse
import com.instaapp.yashaswinifresh.network.responseModel.ShipmentResponse
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.PaymentViewModel
import com.instaapp.yashaswinifresh.viewModelFactory.PaymentViewModelFactory
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class PaymentActivity : AppCompatActivity(), KodeinAware, PaymentListener, PaymentResultListener {
    override val kodein by kodein()
    private lateinit var viewModel: PaymentViewModel
    private val factory: PaymentViewModelFactory by instance()
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var toolbar: ToolbarWithCartBinding
    var tax = "0.0"
    private var count = 0
    private var subTotal: String? = "0"
    private var shippingId: String? = "0"
    private var orderId: String? = "0"
    private var phone_no: String? = ""
    private var email: String? = ""
    private var userName = ""
    private var isCouponApplied: Boolean = false
    private var couponCode: String = "0"
    private var shipId: String? = ""
    private var latitude = "0.0"
    private var longitude = "0.0"
    private lateinit var freeResponse: FreeResponse
    private lateinit var orderDetailResponse: OrderDetailResponse
    private var shippingAddress = ""
    private var billingAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_payment)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        // binding = ActivityPaymentBinding.inflate(layoutInflater)
        toolbar = binding.toolbar

        viewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
        viewModel.paymentListener = this

        binding.toolbar.cartlayout.visibility=View.GONE

        toolbar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()

        shipId =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.SHIPPING_ID).toString()

        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.IS_CASH_AVAILABLE))
            binding.rbCash.visibility = View.VISIBLE
        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.IS_CARD_AVAILABLE))
            binding.rbCard.visibility = View.VISIBLE

        toolbar.txtHeading.text = "Payment"

        toolbar.imgBack.setOnClickListener {
            finish()
        }
        toolbar.cartlayout.setOnClickListener {
            startActivity(Intent(applicationContext, CartListActivity::class.java))
        }
        extractCartData()

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            if (checkedId > -1) {
                if (rb.text.equals(getString(R.string.card))) {
                    binding.cardDetailLayout.visibility = View.VISIBLE
                } else if (rb.text.equals(getString(R.string.cash))) {
                    binding.cardDetailLayout.visibility = View.GONE
                }
            }
        }

        binding.btnApplyCoupon.setOnClickListener {
            if (binding.etCouponCode.text.toString().trim().isEmpty()) {
                toast(getString(R.string.coupon_validate))
            } else {
                if (isCouponApplied) {
                    couponCode = ""
                    viewModel.getFees(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!,
                        AddFreeRequest(
                            0,
                            shipId,
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID),
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            subTotal, 0,
                            0, couponCode,
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                                .toString(),
                            getDeliveryData(), "Restaurant"
                        ), "ApplyCoupon"
                    )
                } else {
                    couponCode = binding.etCouponCode.text.toString().trim()
                    viewModel.getFees(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!,
                        AddFreeRequest(
                            0,
                            shipId,
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID),
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            subTotal, 0, 0, couponCode,
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                                .toString(),
                            getDeliveryData(), "Restaurant"
                        ), "ApplyCoupon"
                    )
                }
            }
        }

        binding.btnPayment.setOnClickListener {
            validation()
        }

    }

    private fun extractCartData() {
        subTotal = PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CART_TOTAL)
        shippingAddress = intent.getStringExtra("shippingAddress")!!
        billingAddress = intent.getStringExtra("billingAddress")!!
        tax = intent.getStringExtra("tax")!!
        latitude = intent.getStringExtra("latitude")!!
        longitude = intent.getStringExtra("longitude")!!

        log(
            "CheckCountry ",
            " =${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.COUNTRY_NAME)}"
        )
        if (Regex("(?i)\\b(India|in|ind)\\b", RegexOption.IGNORE_CASE).containsMatchIn(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.COUNTRY_NAME)
                    .toString()
            )
        ) {
            binding.deliveryAddressLayout.visibility = View.VISIBLE
            if (shipId!!.toInt() == PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)) {
                binding.txtDeliveryAddress.text = shippingAddress
                binding.txtDeliveryTypeHeading.text = "Delivery Address : "
            } else {
                binding.txtDeliveryAddress.text = billingAddress
                binding.txtDeliveryTypeHeading.text = "Billing Address : "
            }
            binding.radioGroup.visibility = View.GONE
            binding.cardDetailLayout.visibility = View.GONE
            binding.paymentDetails.visibility = View.GONE
        }

        getFees()

        viewModel.getShipmentMethod(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID).toString(),
        )

        viewModel.getUserProfile(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString(),
        )

    }

    private fun getFees() {
        val data = getDeliveryData()
        log("getFees Request = ", " $shipId")
        viewModel.getFees(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            AddFreeRequest(
                0,
                shipId,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID),
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                subTotal, 0, 0, "",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString(),
                data, "Restaurant"
            ), "getFees"
        )
    }

    private fun getDeliveryData(): JsonObject {
        val data = JsonObject()
        data.addProperty(
            "matter",
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.ITEM_NAME)
        )
        data.addProperty("address", "")
        data.addProperty("apartment_number", "")
        data.addProperty("username", "")
        if (shipId!!.toInt() == PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)) {
            data.addProperty("address", shippingAddress)
            data.addProperty("apartment_number", "")
            data.addProperty("username", shippingAddress.split(",")[0])
        }
        data.addProperty("total_weight_kg", "1")
        data.addProperty("phone", phone_no)
        data.addProperty("latitude", latitude)
        data.addProperty("longitude", longitude)
        data.addProperty("note", "")
        data.addProperty("building_number", "")
        data.addProperty("entrance_number", "")
        data.addProperty("intercom_code", "")
        data.addProperty("floor_number", "")
        return data
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String, errorBody: String?) {
        binding.progress.progressLayout.visibility = View.GONE
        log("PaymentActivity onFailure ", " message=$message type=$type errorBody=$errorBody")
        when (type) {
            CommonKey.ERROR_CODE_401 -> {
                dialogSessionExpire(
                    applicationContext,
                )
            }
            CommonKey.ERROR_CODE_400 -> {
                if (errorBody == "placeOrder") {
                    showAlert(
                        this,
                        getString(R.string.alert),
                        message
                    )
                } else {
                    val errorObj = JSONObject(errorBody!!)
                    if (errorObj.getString("error") == "Invalid Coupon") {
                        toast(errorObj.getString("error"))
                    } else if (errorObj.getJSONArray("error")
                            .get(0) == "This coupon is valid for Min. Purchase of"
                    ) {
                        val arrErr = errorObj.getJSONArray("error")
                        toast("" + arrErr.get(0))
                    }
                    toast(errorObj.getString("error"))
                }
            }
            else -> showAlert(
                this,
                getString(R.string.alert),
                message
            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "getFees" || type == "ApplyCoupon") {
            freeResponse = dataG as FreeResponse
            binding.feeDetails = freeResponse
            binding.btnPayment.visibility = View.VISIBLE
            if (type == "ApplyCoupon") {
                if (isCouponApplied) {
                    isCouponApplied = false
                    binding.btnApplyCoupon.text = getString(R.string.apply_coupon)
                    binding.etCouponCode.setText("")
                    binding.etCouponCode.isEnabled = true
                } else {
                    isCouponApplied = true
                    binding.btnApplyCoupon.text = getString(R.string.cancel_coupon)
                    binding.etCouponCode.isEnabled = false
                }
            }
        } else if (type == "getShipmentMethod") {
            val data: ShipmentResponse = dataG as ShipmentResponse
            if (data.results!!.isEmpty())
                showAlert(
                    this,
                    getString(R.string.alert),
                    getString(R.string.no_shipping_available)
                )
            else
                shippingId = data.results[0].id.toString()
        } else if (type == "getUserProfile") {
            val data: ProfileResponse = dataG as ProfileResponse
            phone_no = data.results?.get(0)?.phone_number
            email = data.results?.get(0)?.customer?.email
            userName =
                data.results?.get(0)!!.customer!!.first_name!! + " " + data.results?.get(0)!!.customer!!.last_name!!

        } else if (type == "updateOrder") {
            orderDetailResponse = dataG as OrderDetailResponse
            orderId = orderDetailResponse.order_id.toString()
            orderPayment()
        } else if (type == "placeOrder") {
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_COUNT)
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_ID)
            CartManager.clearAllItem()
            printOrder()
        } else if (type == "printOrder") {
            toast("Order placed successfully")
            val intent = Intent(applicationContext, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun validation() {
        if (binding.cardDetailLayout.visibility == View.GONE) {
            if (count == 0) {
                getOrderDetails()
                count++
            } else {
                orderPayment()
            }
        } else {
            if (binding.etCardHolderName.text.toString().trim().isEmpty()) {
                toast(resources.getString(R.string.card_holder_name_validate))
            } else if (binding.etCardNo.text.toString().trim().isEmpty()) {
                toast(getString(R.string.card_no_validate))
            } else if (binding.etCardCVV.text.toString().trim().isEmpty()) {
                toast(getString(R.string.security_code_validate))
            } else if (binding.etMonth.text.toString().trim().isEmpty()) {
                toast(getString(R.string.month_validate))
            } else if (binding.etYear.text.toString().trim().isEmpty()) {
                toast(getString(R.string.year_validate))
            } else {
                if (count == 0) {
                    getOrderDetails()
                    count++
                } else {
                    orderPayment()
                }
            }
        }
    }

    private fun getOrderDetails() {
        viewModel.updateOrder(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            UpdateOrderRequest(
                binding.edtExtraAnnotation.text.toString().trim(),
                freeResponse.tip!!,
                freeResponse.shipping_fee!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
                    .toString(),
                "active",
                freeResponse.total!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                    .toString(),
                freeResponse.tax!!,
                freeResponse.sub_total!!,
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_NAME)!!,
                "0",
                freeResponse.discount!!,
                shippingAddress,
                billingAddress
            )
        )
    }

    private fun placeOrder(transactionID: String, paymentMode: String) {
        val reqObj = JsonObject()
        val cardData = JsonObject()
        val metaData = JsonObject()
        val address = JsonObject()
        val billingDetails = JsonObject()

        metaData.addProperty("order_id", orderId)
        metaData.addProperty(
            "shippingmethod_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.SHIPPING_ID)
        )
        metaData.addProperty(
            "restaurant_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID)!!
        )
        metaData.addProperty("phone", phone_no)
        metaData.addProperty(
            "customer_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)!!.toInt()
        )
        metaData.addProperty(
            "special_instruction",
            binding.edtExtraAnnotation.text.toString().trim()
        )

        if (shipId!!.toInt() == PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_DELIVERY_ID)) {
            metaData.addProperty("name", shippingAddress.split(",")[0])
            address.addProperty("city", shippingAddress.split(",")[3])
            address.addProperty("state", shippingAddress.split(",")[4])
            address.addProperty("line1", shippingAddress.split(",")[2])
            address.addProperty("line2", "")
            address.addProperty("house_no", shippingAddress.split(",")[1])
            address.addProperty("postal_code", shippingAddress.split(",")[6])
        } else {
            metaData.addProperty("name", billingAddress.split(",")[0])
            address.addProperty("city", billingAddress.split(",")[3])
            address.addProperty("state", billingAddress.split(",")[4])
            address.addProperty("line1", billingAddress.split(",")[2])
            address.addProperty("line2", "")
            address.addProperty("house_no", billingAddress.split(",")[1])
            address.addProperty("postal_code", billingAddress.split(",")[6])
        }

        billingDetails.add("address", address)
        //Card/Payment
        cardData.addProperty("number", "")
        cardData.addProperty("exp_month", "")
        cardData.addProperty("exp_year", "")
        cardData.addProperty("cvc", "")
        cardData.addProperty("name", "")
        reqObj.addProperty("type", "Online")

        if (!Regex("(?i)\\b(India|in|ind)\\b", RegexOption.IGNORE_CASE).containsMatchIn(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.COUNTRY_NAME)
                    .toString()
            )
        )
        //binding.cardDetailLayout
            if (binding.cardDetailLayout.visibility == View.VISIBLE) {
                cardData.addProperty("number", binding.etCardNo.text.toString().trim())
                cardData.addProperty("exp_month", binding.etMonth.text.toString().trim())
                cardData.addProperty("exp_year", binding.etYear.text.toString().trim())
                cardData.addProperty("cvc", binding.etCardCVV.text.toString().trim())
                cardData.addProperty("name", binding.etCardHolderName.text.toString())
                reqObj.addProperty("type", "card")
            } else {
                cardData.addProperty("number", "")
                cardData.addProperty("exp_month", "")
                cardData.addProperty("exp_year", "")
                cardData.addProperty("cvc", "")
                cardData.addProperty("name", "")
                reqObj.addProperty("type", "Cash")
            }

        reqObj.addProperty("amount", freeResponse.total!!.toDouble())
        reqObj.addProperty(
            "currency",
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_NAME)
        )
        reqObj.addProperty("receipt_email", email)
        reqObj.addProperty("source", "mobile")
        reqObj.add("card", cardData)
        reqObj.add("metadata", metaData)
        reqObj.add("billing_details", billingDetails)
        reqObj.add("borzo_order", getDeliveryData())
        reqObj.addProperty("o_type", "Restaurant")
        reqObj.addProperty("transaction_id", transactionID)
        reqObj.addProperty("payment_method", paymentMode)
        reqObj.addProperty(
            "cart_id",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_ID)
        )

        Log.d("PaymentActivity ", "placeOrder: $reqObj")

        if (Regex("(?i)\\b(India|in|ind)\\b", RegexOption.IGNORE_CASE).containsMatchIn(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.COUNTRY_NAME)
                    .toString()
            )
        ) {
            Log.d("PaymentActivity ", "placeOrder: For INDIA ")
            viewModel.placeOrder(
                "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
                reqObj
            )
        } else viewModel.placeOrderForOtherCountry(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            reqObj
        )
    }

    private fun printOrder() {
        viewModel.printOrder(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PrintOrderRequest(
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID)
                    .toString(),
                orderDetailResponse.order_id.toString(),
                ""
            )
        )
    }

    private fun orderPayment() {
        if (Regex("(?i)\\b(India|in|ind)\\b", RegexOption.IGNORE_CASE).containsMatchIn(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.COUNTRY_NAME)
                    .toString()
            )
        )
            startPayment(freeResponse.total!!.toString())
        else
            placeOrder("", "")
    }

    private fun startPayment(amount: String) {
        val activity = this
        val co = Checkout()
        log(
            "startPayment ",
            " ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.PAYMENT_TOKEN)}"
        )
        co.setKeyID(PreferenceProvider(applicationContext).getStringValue(PreferenceKey.PAYMENT_TOKEN)) // For Testing Mode
        try {
            val options = JSONObject()
            options.put(
                "currency",
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_NAME)!!
                    .toUpperCase()
            )

            options.put("amount", customRounding((amount.toDouble() * 100)))
            // Convert amount to double
            options.put("name", getString(R.string.app_name))
            val preFill = JSONObject()
            preFill.put("name", userName)
            preFill.put("contact", phone_no)
            preFill.put("email", email)
            options.put("prefill", preFill)

            val checkoutObj = JSONObject()

            val methodObj = JSONObject()
            methodObj.put("netbanking", 0)
            methodObj.put("upi", 1)
            methodObj.put("card", 0)
            methodObj.put("wallet", 0)
            checkoutObj.put("method", methodObj)
            options.put("checkout", checkoutObj)
            co.open(activity, options)
        } catch (e: Exception) {
            toast("Payment failed , $e")
            log("PlaceOrderActivity", "onPaymentFailed2:  ${e.message}")
        }
    }

    fun calculateSixMonthsLater(currentTimestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimestamp
        calendar.add(Calendar.MONTH, 6)
        return calendar.timeInMillis
    }

    override fun onPaymentSuccess(p0: String?) {
        placeOrder(p0!!, "Card")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        log("onPaymentError ", " $p0 == $p1")
        toast("Payment failed, Please try after some time")
    }

}