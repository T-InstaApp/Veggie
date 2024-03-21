package com.instaapp.veggietemp1.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.responseModel.RestaurantsAboutUS
import com.instaapp.veggietemp1.utils.PreferenceKey
import com.instaapp.veggietemp1.utils.PreferenceProvider
import com.instaapp.veggietemp1.utils.StaticValue
import com.instaapp.veggietemp1.utils.showAlert
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityAboutBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AboutUs : AppCompatActivity(), HomeListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this

        viewModel.getAboutUS(
            "Token " + PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.APP_TOKEN
            )!!, StaticValue.REST_ID
        )

        binding.toolBar.txtHeading.text = getString(R.string.menu_aboutus)
        binding.toolBar.imgBack.setOnClickListener { finish() }
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String) {
        binding.progress.progressLayout.visibility = View.GONE
        showAlert(this, getString(R.string.alert), message)
    }

    override fun onSuccess(data: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
    }

    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        val data: RestaurantsAboutUS = dataG as RestaurantsAboutUS
        binding.aboutUSData = data
        Glide.with(this).load(data.restaurant_url).into(binding.aboutImage)
    }


}