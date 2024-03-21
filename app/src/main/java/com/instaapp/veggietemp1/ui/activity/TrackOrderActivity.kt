package com.instaapp.veggietemp1.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.instaapp.veggietemp1.utils.PreferenceKey
import com.instaapp.veggietemp1.utils.PreferenceProvider
import com.instaapp.veggietemp1.utils.toast
import com.instaapp.vegiestemp1.databinding.ActivityTrackOrderBinding

class TrackOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackOrderBinding

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.txtHeading.text = "Order ID-${intent.getStringExtra("OrderId")}"
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
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        // Set a WebViewClient to handle page navigation
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // You can add progress handling logic here if needed
            }
        }
        // Load the URL
        val url =
            intent.getStringExtra("tracking_url")!! //"https://apitest.borzodelivery.com/in/track/PGILP2YZN571IN"
        binding.webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}