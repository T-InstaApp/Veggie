package com.instaapp.veggietemp1.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.responseModel.RestaurantsAboutUS
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityContactBinding
import com.instaapp.vegiestemp1.databinding.ToolbarBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ContactUsActivity : AppCompatActivity(), HomeListener, KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityContactBinding
    private lateinit var toolbar: ToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolBar
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this

        viewModel.getAboutUS(
            "Token " + PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.APP_TOKEN
            )!!,StaticValue.REST_ID
        )

        toolbar.txtHeading.text = getString(R.string.menu_contactus)
        toolbar.imgBack.setOnClickListener { finish() }
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        binding.progress.progressLayout.visibility = View.GONE
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun onSuccess(data: String, type: String) {
    }

    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        val data: RestaurantsAboutUS = dataG as RestaurantsAboutUS
        binding.contactUSData = data
    }
}