package com.instaapp.veggietemp1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.listener.HomeListener
import com.instaapp.veggietemp1.network.responseModel.CompanyData
import com.instaapp.veggietemp1.ui.activity.Home
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.HomeViewModel
import com.instaapp.veggietemp1.viewModelFactory.HomeViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivitySplashBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

private const val DELAY_TIME = 1000

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), KodeinAware,
    HomeListener {//InAppUpdateManager.InAppUpdateHandler

    var topAnim: Animation? = null
    var bottomAnim: Animation? = null
    private lateinit var binding: ActivitySplashBinding

    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceProvider(applicationContext).setIntValue(
            StaticValue.REST_ID.toInt(),
            PreferenceKey.REST_ID
        )

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)


        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                // handle callback
                if (result.resultCode != RESULT_OK) {

                    // If the update is canceled or fails,
                    // you can request to start the update again.
                }
            }

      //  inAppUpdate()

        dialogShowMsg()

    }


    private fun dialogShowMsg() {
        if (PreferenceProvider(applicationContext).getSplashScreenImageUrl().equals("N/A")) {
            Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.logo1))
                .into(binding.txtAppLogo)
            binding.txtAppName.text = getString(R.string.app_name)
        } else {
            Glide.with(this).load(PreferenceProvider(applicationContext).getSplashScreenImageUrl())
                .into(binding.txtAppLogo)
            binding.txtAppName.text =
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_NAME)
        }
        binding.txtAppLogo.animation = topAnim
        binding.txtAppName.animation = bottomAnim

        Handler(Looper.getMainLooper()).postDelayed({
            log(
                "SplashActivity ",
                " ${PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)} == ${
                    PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)
                }"
            )
            viewModel.getAppDetails("User", StaticValue.REST_ID.toInt())

        }, DELAY_TIME.toLong())
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onFailure(message: String) {
        binding.progressBar.hide()
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun onSuccess(data: String, type: String) {

    }

    override fun <T> onSuccessData(dataG: T, type: String) {
        binding.progressBar.hide()
        val data: CompanyData = dataG as CompanyData

        log("Rest_data SplashActivity =", "$data")

        PreferenceProvider(applicationContext).setStringValue(
            data.currency!!, PreferenceKey.CURRENCY_NAME
        )
        PreferenceProvider(applicationContext).setStringValue(
            data.country!!, PreferenceKey.COUNTRY_NAME
        )
        PreferenceProvider(applicationContext).setStringValue(
            data.currency_symbol!!, PreferenceKey.CURRENCY_TYPE
        )
        PreferenceProvider(applicationContext).setStringValue(
            data.Token!!, PreferenceKey.APP_TOKEN
        )

        PreferenceProvider(applicationContext).setSplashScreenImageUrl(data.compony_image!!)

        PreferenceProvider(applicationContext).setBooleanValue(
            data.IS_CASH_AVAILABLE, PreferenceKey.IS_CASH_AVAILABLE
        )
        PreferenceProvider(applicationContext).setBooleanValue(
            data.IS_CARD_AVAILABLE, PreferenceKey.IS_CARD_AVAILABLE
        )
        PreferenceProvider(applicationContext).setIntValue(
            data.REST_PICKUP_ID!!.toInt(), PreferenceKey.REST_PICKUP_ID
        )
        PreferenceProvider(applicationContext).setIntValue(
            data.REST_DELIVERY_ID!!.toInt(), PreferenceKey.REST_DELIVERY_ID
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.compony_name!!, PreferenceKey.APP_NAME
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.payment_key!!, PreferenceKey.PAYMENT_TOKEN
        )

        startActivity(Intent(applicationContext, Home::class.java))
        finish()
    }

  /*  fun inAppUpdate() {
        log("inAppUpdate ", " Called ")
        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                log("inAppUpdate ", " Update Available ")
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // an activity result launcher registered via registerForActivityResult
                    activityResultLauncher,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true)
                        .build()
                )
            } else {
                dialogShowMsg()
                log("inAppUpdate ", " Update Not Available ")
            }
        }
    }*/

}