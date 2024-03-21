package com.instaapp.veggietemp1.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.responseModel.MasterCategoryDataModel
import com.instaapp.veggietemp1.network.responseModel.ProductSearchResponse
import com.instaapp.veggietemp1.ui.adapter.CategoryAdapter
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import okhttp3.MediaType
import okhttp3.RequestBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.core.app.ActivityCompat
import com.instaapp.veggietemp1.network.responseModel.CartListResponse
import com.instaapp.veggietemp1.network.responseModel.CartListResult
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityHomeBinding
import com.instaapp.vegiestemp1.databinding.ToolbarWithCartBinding
import java.io.IOException

class Home : AppCompatActivity(), HomeListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toolbar: ToolbarWithCartBinding


    // private lateinit var progressBar: ProgressDialogLayoutBinding
    var masterCategoryData: ArrayList<MasterCategoryDataModel> = ArrayList()
    var masterCatName: ArrayList<String> = arrayListOf()
    var masterCatID: ArrayList<String> = arrayListOf()
    var selectedMasterCatID = 0
    var errorAlert = false
    var searchQuery = ""
    private lateinit var cartProductIds: List<Triple<Int?, Int?, Int>>

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolBar
        //  progressBar=binding.progressBar
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        toolbar.txtHeading.text = getString(R.string.home)
        toolbar.txtHeading.visibility = View.GONE
        toolbar.imgBack.visibility = View.GONE

        toolbar.txtUserLocationView.visibility = View.VISIBLE
        toolbar.txtUserLocationView.text = ""

        toolbar.edtSearch.visibility = View.GONE
        //Init Search Functionality
        initSearchView()

        toolbar.homeCartCount.text = "0"
        binding.txtCheckOpenHours.setOnClickListener {
            startActivity(Intent(applicationContext, ContactUsActivity::class.java))
        }

        binding.imgHours.setOnClickListener { openHoursDialog() }
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

        binding.autoMasterCat.onItemClickListener =
            AdapterView.OnItemClickListener { _: AdapterView<*>?, _: View?, pos: Int, _: Long ->
                selectedMasterCatID = masterCatID[pos].toInt()
                try {
                    if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS))
                        if (!masterCategoryData[pos].restaurant_open!!) {
                            binding.catClosetText.text =
                                "We apologize for the inconvenience, This menu is currently unavailable"
                            binding.layoutMasterCatOutOFStock.visibility = View.VISIBLE
                        } else
                            binding.layoutMasterCatOutOFStock.visibility = View.GONE
                } catch (ignore: java.lang.Exception) {
                }
                loadCategoryData(masterCatID[pos].toInt())
            }

        binding.autoMasterCat.setOnTouchListener { _: View?, _: MotionEvent? ->
            binding.autoMasterCat.showDropDown()
            false
        }

        binding.txtShowOpenHours.setOnClickListener { openHoursDialog() }

        //To Check Restaurant Open/Closed Status
        viewModel.getRestHours(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)
            }", StaticValue.REST_ID
        )

        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.DELIVERY_ADDRESS)!!.length > 5) {
            toolbar.txtUserLocationView.text =
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.DELIVERY_ADDRESS)
        } else
            requestLocationPermissions()

        //Check User Login status and Cart ID
        if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {

            checkCart()
        }
        binding.orderList.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                startActivity(Intent(applicationContext, OrderListActivity::class.java))
            } else toast("Please login first")
        }

        binding.drawerMenu.setOnClickListener {
            val intent = Intent(applicationContext, DrawerMenuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        binding.searchView.setText("")
        log("CheckCartBlank ", " 1 ")
        toolbar.homeCartCount.text =
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CART_COUNT).toString()
        super.onResume()
    }

    //Check Cart
    private fun checkCart() {
        viewModel.checkCart(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
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

    private fun initSearchView() {
        binding.searchView.setOnEditorActionListener(OnEditorActionListener { v: TextView, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

    //Fetch Master Category Data
    private fun getMasterCatData() {
        viewModel.getMasterCat(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID
        )
    }

    //Fetch  Category Data
    private fun loadCategoryData(id: Int) {
        viewModel.getCategory(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
            StaticValue.REST_ID, id
        )
        viewModel.categoryData.observe(this) { catData ->
            binding.idRecyclerViewCat.also {
                it.layoutManager =
                    GridLayoutManager(applicationContext, 2, RecyclerView.VERTICAL, false)
                it.setHasFixedSize(true)
                it.adapter = CategoryAdapter(catData, applicationContext)
            }
        }

    }

    override fun onStarted() {
        binding.progressBar.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        binding.progressBar.progressLayout.visibility = View.GONE
        if (!errorAlert) {
            errorAlert = true
            showAlert(this, getString(R.string.alert), message)
        }
    }

    override fun onSuccess(data: String, type: String) {
        binding.progressBar.progressLayout.visibility = View.GONE
        log("onSuccess ", "onSuccess = $type = $data")
        when (type) {
            "getRestHours" -> {
                if (data.equals("closed", ignoreCase = true)) {
                    PreferenceProvider(applicationContext).setBooleanValue(
                        false, PreferenceKey.RESTAURANT_OPEN_STATUS
                    )
                    binding.layoutClosed.visibility = View.VISIBLE
                } else {
                    PreferenceProvider(applicationContext).setBooleanValue(
                        true, PreferenceKey.RESTAURANT_OPEN_STATUS
                    )
                    binding.layoutClosed.visibility = View.GONE
                }
                //Fetch Master Category Data
                getMasterCatData()
            }
            "getCartCount" -> {
                log("CheckCartBlank ", " 2 = $data")
                try {
                    if (data.isNotEmpty()) {
                        toolbar.homeCartCount.text = data
                        PreferenceProvider(applicationContext).setIntValue(
                            data.toInt(), PreferenceKey.CART_COUNT
                        )
                    }
                } catch (ignore: Exception) {
                }
            }
            "checkCart" -> {
                PreferenceProvider(applicationContext).setIntValue(
                    data.toInt(),
                    PreferenceKey.CART_ID
                )
                checkCartCount()
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("SetTextI18n")
    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progressBar.progressLayout.visibility = View.GONE
        if (type == "getMasterCat") {
            val data1: ArrayList<MasterCategoryDataModel> =
                dataG as ArrayList<MasterCategoryDataModel>
            log("getMasterCat "," $data1")
            masterCategoryData = data1
            if (masterCategoryData.size > 0) {
                for (data in masterCategoryData) {
                    masterCatName.add(data.master_category_name!!)
                    masterCatID.add(data.master_category_id.toString())
                }
                binding.autoMasterCat.threshold = 50
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    applicationContext,
                    R.layout.autocomplete_item_layout,
                    masterCatName
                )
                binding.autoMasterCat.setAdapter(adapter)
                binding.autoMasterCat.setText(masterCatName[0])
                loadCategoryData(masterCatID[0].toInt())
                selectedMasterCatID = masterCatID[0].toInt()
                if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS))
                    if (masterCategoryData[0].restaurant_open!=null &&!masterCategoryData[0].restaurant_open!!) {
                        binding.layoutMasterCatOutOFStock.visibility = View.VISIBLE
                        binding.catClosetText.text =
                            "We apologize for the inconvenience, This menu is currently unavailable"
                    }
            }
        } else if (type == "searchItem") {
            val data: ProductSearchResponse = dataG as ProductSearchResponse
            if (data.data.productSearch.isNotEmpty()) {
                val intent = Intent(applicationContext, SearchActivity::class.java)
                intent.putExtra("searchQuery", searchQuery)
                startActivity(intent)
            } else
                toast("Oh Sorry! No search result has found")
        } else if (type == "getCartCount") {
            val data: CartListResponse = dataG as CartListResponse
            getCartAndProductID(data.results)
        }
    }

    @SuppressLint("SimpleDateFormat", "MissingInflatedId")
    private fun openHoursDialog() {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme2)
        // Inflate the custom layout
        val customLayout = layoutInflater.inflate(R.layout.working_hours_dialog, null)
        builder.setView(customLayout)

        val txDay = customLayout.findViewById<TextView>(R.id.opensHourDay)
        val txTime = customLayout.findViewById<TextView>(R.id.opensHourTime)
        val txtMasterCatName = customLayout.findViewById<TextView>(R.id.txtMasterCatName)
        val timeTextBuilder = SpannableStringBuilder()
        val dayTextBuilder = SpannableStringBuilder()

        try {
            val sdf = SimpleDateFormat("EEEE")
            val d = Date()
            val dayOfTheWeek = sdf.format(d)
            for (data in masterCategoryData) {
                if (data.master_category_id == selectedMasterCatID) {
                    txtMasterCatName.text = data.master_category_name
                    val openHours = data.open_hours
                    for (hoursData in openHours!!) {
                        val tempDate = "${hoursData.day!!.uppercase(Locale.getDefault())}\n\n"
                        if (dayOfTheWeek.equals(hoursData.day, ignoreCase = true) ||
                            !hoursData.status!!
                        ) {
                            val redSpannable = SpannableString(tempDate)
                            if (!hoursData.status!!)
                                redSpannable.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            applicationContext,
                                            R.color.new_theme_red
                                        )
                                    ), 0, tempDate.length, 0
                                )
                            else
                                redSpannable.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            applicationContext,
                                            R.color.new_theme_blue
                                        )
                                    ), 0, tempDate.length, 0
                                )
                            dayTextBuilder.append(redSpannable)
                        } else {
                            dayTextBuilder.append(tempDate)
                        }
                        if (hoursData.status) {
                            for (j in 0 until hoursData.start_time!!.split("@").size) {

                                val tempDateTime =
                                    "${convertGMTtoLocalTime(hoursData.start_time.split("@")[j])} To ${
                                        convertGMTtoLocalTime(hoursData.end_time!!.split("@")[j])
                                    }\n\n"


                                if (dayOfTheWeek.equals(hoursData.day, ignoreCase = true)) {
                                    val redSpannable = SpannableString(tempDateTime)
                                    redSpannable.setSpan(
                                        ForegroundColorSpan(
                                            ContextCompat.getColor(
                                                applicationContext,
                                                R.color.new_theme_blue
                                            )
                                        ), 0, tempDateTime.length, 0
                                    )
                                    timeTextBuilder.append(redSpannable)
                                } else {
                                    timeTextBuilder.append(tempDateTime)
                                }
                                if (j > 0)
                                    dayTextBuilder.append("\n\n")
                            }
                        } else {
                            val tempDateTime = "Closed\n\n"
                            val redSpannable = SpannableString(tempDateTime)
                            redSpannable.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        R.color.new_theme_red
                                    )
                                ), 0, tempDateTime.length, 0
                            )
                            timeTextBuilder.append(redSpannable)
                        }
                    }
                }
            }
        } catch (ignore: Exception) {

        }
        val dialog = builder.create()
        txDay.text = dayTextBuilder
        txTime.text = timeTextBuilder
        customLayout.findViewById<AppCompatButton>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadSearchData(searchKey: String) {
        searchQuery = searchKey
        val sss =
            "{\"query\":\"\\nquery productSearch (\$token: String,\$productName: String,\$restaurantId: Int, \$extra: String,\$first: Int, \$skip: Int ){\\n  # Category, ProductName is optional\\n  productSearch (token : \$token, productName: \$productName, restaurantId: \$restaurantId, extra: \$extra, ,first: \$first, skip: \$skip  ) " +
                    "{\\n productName\\n  productId\\n  productUrl\\n  MRP\\n  price\\n    taxExempt\\n  extra\\n  inStock\\n   availableTime\\n  deliveryType\\n suspendedUntil\\n category{\\n categoryId\\n category\\n availableTime\\n masterCategory{\\n masterCategoryId\\n status\\n MasterCategoryOpenHours{\\n day\\n startTime\\n endTime\\n status\\n}\\n}\\n}\\n }\\n}\",\"variables\":{\"productName\":\"" + searchKey + "\",\"restaurantId\":\"" + StaticValue.REST_ID + "\",\"extra\":\"\",\"first\":100,\"skip\":0,\"token\":\"0o6jcui8mfhmp56we69kcmu5rkejtock\"}}"

        log("loadSearchData ", " SSS $sss")
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sss)

        viewModel.searchItem(body)
    }


    private fun requestLocationPermissions() {
        // Check if the app has the necessary location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
            // Permissions are already granted
            // You can proceed with your location-related code here
        } else {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                startLocationUpdates()
                // Permissions granted, you can proceed with your location-related code here
            } else {
                // Permissions denied, handle accordingly (e.g., show a message, disable location features)
            }
        }
    }


    private fun startLocationUpdates() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // When the location changes, get the address
                getAddressFromLocation(location.latitude, location.longitude)
                // Stop location updates after obtaining the address
                locationManager.removeUpdates(this)
            }
        }
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                toolbar.txtUserLocationView.text = address
                log("CurrentAddress", "Address: $address")
                // Update your UI or perform other actions with the obtained address
            } else {
                log("CurrentAddress", "No address found")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getCartAndProductID(results: List<CartListResult>) {
        cartProductIds = results.map {
            Triple(it.cart_item_id, it.product?.product_id, it.quantity ?: -1)
        }
        CartManager.updateCartProductIds(cartProductIds)
    }
}