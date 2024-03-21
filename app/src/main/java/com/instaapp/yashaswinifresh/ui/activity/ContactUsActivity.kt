package com.instaapp.yashaswinifresh.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.ActivityContactBinding
import com.instaapp.yashaswinifresh.databinding.ToolbarBinding
import com.instaapp.yashaswinifresh.listener.HomeListener
import com.instaapp.yashaswinifresh.network.responseModel.RestaurantsAboutUS
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.HomeViewModel
import com.instaapp.yashaswinifresh.viewModelFactory.HomeViewModelFactory
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