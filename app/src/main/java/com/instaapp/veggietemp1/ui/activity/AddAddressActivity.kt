package com.instaapp.veggietemp1.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.instaapp.veggietemp1.listener.CheckOutListener
import com.instaapp.veggietemp1.network.requestModel.AddBillingShippingAddressRequest
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.CheckOutModel
import com.instaapp.veggietemp1.viewModelFactory.CheckOutViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityAddAddressBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class AddAddressActivity : AppCompatActivity(), KodeinAware, CheckOutListener, OnMapReadyCallback,
    GoogleMap.OnMapClickListener {
    override val kodein by kodein()
    private lateinit var viewModel: CheckOutModel
    private val factory: CheckOutViewModelFactory by instance()
    private lateinit var binding: ActivityAddAddressBinding
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var locationName = ""
    private var selectedMarker: Marker? = null
    private var googleMap: GoogleMap? = null
    private var isShippingAddressAvailable = false
    private var isBillingAddressAvailable = false
    private var shippingAddressID = 0
    private var billingAddressID = 0
    private var tax = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)

        viewModel = ViewModelProvider(this, factory)[CheckOutModel::class.java]
        viewModel.checkOutListener = this

        binding.toolBar.txtHeading.text = getString(R.string.add_new_address_h)

        binding.toolBar.imgBack.setOnClickListener {
            finish()
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        isShippingAddressAvailable = intent.getBooleanExtra("isShippingAddressAvailable", false)
        isBillingAddressAvailable = intent.getBooleanExtra("isBillingAddressAvailable", false)
        shippingAddressID = intent.getIntExtra("shippingAddressID", 0)
        billingAddressID = intent.getIntExtra("billingAddressID", 0)
        tax = intent.getStringExtra("tax")!!


        val apiKey = getString(R.string.google_places_api_key)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Initialize Autocomplete Fragments from the main activity layout file
        val autocompleteSupportFragment1 =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment

        autocompleteSupportFragment1.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.ADDRESS_COMPONENTS,
            )
        )

        // Display the fetched information after clicking on one of the options
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            @SuppressLint("SetTextI18n")
            override fun onPlaceSelected(place: Place) {
                var address = place.address
                val latlng = place.latLng
                locationName = place.name!!
                latitude = latlng?.latitude!!
                longitude = latlng.longitude
                var city = ""
                var state = ""
                var country = ""
                var postalCode = ""
                val addressComponents = place.addressComponents
                for (component in addressComponents!!.asList()) {
                    for (type in component.types) {
                        when (type) {
                            "locality" -> city = component.name //
                            "administrative_area_level_1" -> state = component.shortName!!
                            "country" -> country = component.name
                            "postal_code" -> postalCode = component.name
                        }
                    }
                }

                log(" AddAddressActivity ", " first Address $address")
                address = address!!.replace(postalCode, "")
                address = address.replace(city, "")
                address = address.replace(state, "")
                address = address.replace(country, "")
                log(" AddAddressActivity ", " Second Address $address")
                binding.etAddress.setText(cleanAddress(address.toString()))
                binding.etCity.setText(city)
                binding.etState.setText(state)
                binding.etCountry.setText(country)
                binding.etZipCode.setText(postalCode)
                binding.layoutMapView.visibility = View.VISIBLE
                binding.layoutAddress.visibility = View.GONE
                updateMap()
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnConfirmAddress.setOnClickListener {
            binding.layoutMapView.visibility = View.GONE
            binding.layoutAddress.visibility = View.VISIBLE
        }

        binding.btnAddAddress.setOnClickListener { validation() }

        binding.toolBar.cartlayout.visibility = View.GONE
        binding.toolBar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()

        binding.toolBar.cartlayout.setOnClickListener {
            startActivity(Intent(applicationContext, CartListActivity::class.java))
        }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap!!.uiSettings.isZoomControlsEnabled = true
        // Set a click listener to detect when the map is clicked
        googleMap!!.setOnMapClickListener(this)

        val location = LatLng(latitude, longitude)
        selectedMarker =
            googleMap?.addMarker(MarkerOptions().position(location).title(locationName))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
        getLocation()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onMapClick(location: LatLng) {
        // Remove the previous marker
        selectedMarker?.remove()

        // Set the new latitude and longitude
        latitude = location.latitude
        longitude = location.longitude

        // Add a new marker to the selected location
        selectedMarker =
            googleMap?.addMarker(MarkerOptions().position(location).title("Selected Location"))

        // Perform reverse geocoding to get location details
        val geocoder = Geocoder(this)
        try {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addressList!!.isNotEmpty()) {
                val address = addressList[0]
                val city = address.locality
                val state = address.adminArea
                val country = address.countryName
                val postalCode = address.postalCode
                var fullAddress = address.getAddressLine(0)
                fullAddress = fullAddress!!.replace(postalCode, "")
                fullAddress = fullAddress.replace(city, "")
                fullAddress = fullAddress.replace(state, "")
                fullAddress = fullAddress.replace(country, "")
                binding.etAddress.setText(cleanAddress(fullAddress.toString()))
                binding.etCity.setText(city)
                binding.etState.setText(state)
                binding.etCountry.setText(country)
                binding.etZipCode.setText(postalCode)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error while reverse geocoding", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateMap() {
        selectedMarker?.remove()
        val location = LatLng(latitude, longitude)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
        selectedMarker =
            googleMap?.addMarker(MarkerOptions().position(location).title(locationName))
        binding.layoutMapView.visibility = View.VISIBLE
        binding.layoutAddress.visibility = View.GONE


        val geocoder = Geocoder(this)
        try {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addressList!!.isNotEmpty()) {
                val address = addressList[0]
                val city = address.locality
                val state = address.adminArea
                val country = address.countryName
                val postalCode = address.postalCode
                var fullAddress = address.getAddressLine(0)
                fullAddress = fullAddress!!.replace(postalCode, "")
                fullAddress = fullAddress.replace(city, "")
                fullAddress = fullAddress.replace(state, "")
                fullAddress = fullAddress.replace(country, "")
                binding.etAddress.setText(cleanAddress(fullAddress.toString()))
                binding.etCity.setText(city)
                binding.etState.setText(state)
                binding.etCountry.setText(country)
                binding.etZipCode.setText(postalCode)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error while reverse geocoding", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        updateMap()
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun validation() {
        if (binding.etName.text.toString().trim().isEmpty()) {
            toast("Enter your full name")
        } else if (binding.etAptNO.text.toString().trim().isEmpty()) {
            toast("Enter your House/APT no.")
        } else if (binding.etZipCode.text.toString().trim().isEmpty()) {
            toast("Enter zip/postal code")
        } else if (binding.etBuildingName.text.toString().trim().isEmpty()) {
            toast("Enter society/building name")
        } else if (binding.etAddress.text.toString().trim().isEmpty()) {
            toast("Enter address")
        } else if (binding.etCity.text.toString().trim().isEmpty()) {
            toast("Enter city")
        } else if (binding.etState.text.toString().trim().isEmpty()) {
            toast("Enter state")
        } else if (binding.etCountry.text.toString().trim().isEmpty()) {
            toast("Enter country")
        } else {
            if (isShippingAddressAvailable) {
                viewModel.updateShippingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    shippingAddressID.toString(),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etAptNO.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etZipCode.text.toString().trim(),
                        "${
                            binding.etBuildingName.text.toString().trim()
                        }, ${binding.etAddress.text.toString().trim()}",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        latitude.toString(),
                        longitude.toString()
                    )
                )
            } else {
                viewModel.addShippingAddress(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN),
                    AddBillingShippingAddressRequest(
                        "",
                        binding.etCountry.text.toString().trim(),
                        binding.etAptNO.text.toString().trim(),
                        binding.etCity.text.toString().trim(),
                        binding.etZipCode.text.toString().trim(),
                        "${
                            binding.etBuildingName.text.toString().trim()
                        }, ${binding.etAddress.text.toString().trim()}",
                        PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                            .toString(),
                        binding.etState.text.toString().trim(),
                        1,
                        binding.etName.text.toString().trim(),
                        latitude.toString(),
                        longitude.toString()
                    )
                )
            }
        }
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onFailure(message: String, type: String) {
        binding.progressBar.hide()
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
        binding.progressBar.hide()
        log("AddAddressActivity ", " Success $type")
        if (type == "updateShippingAddress" || type == "addShippingAddress") {
            if (binding.checkboxUseAsBilling.isChecked) {
                if (isBillingAddressAvailable) {
                    viewModel.updateBillingAddress(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        billingAddressID.toString(),
                        AddBillingShippingAddressRequest(
                            "",
                            binding.etCountry.text.toString().trim(),
                            binding.etAptNO.text.toString().trim(),
                            binding.etCity.text.toString().trim(),
                            binding.etZipCode.text.toString().trim(),
                            "${
                                binding.etBuildingName.text.toString().trim()
                            }, ${binding.etAddress.text.toString().trim()}",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            binding.etState.text.toString().trim(),
                            1,
                            binding.etName.text.toString().trim(),
                            latitude.toString(),
                            longitude.toString()
                        )
                    )
                } else {
                    viewModel.addBillingAddress(
                        "Token " + PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        ),
                        AddBillingShippingAddressRequest(
                            "",
                            binding.etCountry.text.toString().trim(),
                            binding.etAptNO.text.toString().trim(),
                            binding.etCity.text.toString().trim(),
                            binding.etZipCode.text.toString().trim(),
                            "${
                                binding.etBuildingName.text.toString().trim()
                            }, ${binding.etAddress.text.toString().trim()}",
                            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                                .toString(),
                            binding.etState.text.toString().trim(),
                            1,
                            binding.etName.text.toString().trim(),
                            latitude.toString(),
                            longitude.toString()
                        )
                    )
                }
            } else {
                val intent = Intent(applicationContext, BillingAddressActivity::class.java)
                val address = "${
                    binding.etBuildingName.text.toString().trim()
                } ${binding.etAddress.text.toString().trim()}"
                val shippingAddress =
                    " ${binding.etName.text.toString().trim()}, ${
                        binding.etAptNO.text.toString().trim()
                    }, $address,${
                        binding.etCity.text.toString().trim()
                    }, ${binding.etState.text.toString().trim()}, ${
                        binding.etCountry.text.toString().trim()
                    }, ${binding.etZipCode.text.toString().trim()}"
                intent.putExtra("shippingAddress", shippingAddress)
                intent.putExtra("tax", tax)
                intent.putExtra("latitude", latitude.toString())
                intent.putExtra("longitude", longitude.toString())
                startActivity(intent)
                finish()
            }
        } else if (type == "updateBillingAddress" || type == "addBillingAddress") {
            val intent = Intent(applicationContext, PaymentActivity::class.java)
            val address = "${
                binding.etBuildingName.text.toString().trim()
            } ${binding.etAddress.text.toString().trim()}"
            val shippingAddress =
                " ${binding.etName.text.toString().trim()}, ${
                    binding.etAptNO.text.toString().trim()
                }, $address,${
                    binding.etCity.text.toString().trim()
                }, ${binding.etState.text.toString().trim()}, ${
                    binding.etCountry.text.toString().trim()
                }, ${binding.etZipCode.text.toString().trim()}"
            intent.putExtra("shippingAddress", shippingAddress)
            intent.putExtra("billingAddress", shippingAddress)
            intent.putExtra("tax", tax)
            intent.putExtra("latitude", latitude.toString())
            intent.putExtra("longitude", longitude.toString())
            startActivity(intent)
            finish()
        }
    }

}
