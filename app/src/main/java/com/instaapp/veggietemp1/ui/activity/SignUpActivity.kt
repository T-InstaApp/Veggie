package com.instaapp.veggietemp1.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.listener.AuthListener
import com.instaapp.veggietemp1.network.responseModel.MainLoginResponse
import com.instaapp.veggietemp1.network.responseModel.UserRegistrationResponse
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.AuthViewModel
import com.instaapp.veggietemp1.viewModelFactory.AuthViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivitySignUpBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SignUpActivity : AppCompatActivity(), AuthListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding.signUpModel = viewModel
        viewModel.authListener = this
        viewModel.code = getCountryPhoneCode(applicationContext)
        binding.toolBar.txtHeading.text="Sign Up"

        binding.txtAlreadyRegistered.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.putExtra("callFrom", "SignUp")
            startActivity(intent)
            finish()
        }
        binding.toolBar.imgBack.setOnClickListener { finish() }
        binding.txtAutoCompleteSalutation.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.select_dialog_item,
                StaticValue.SALUTATION_DATA
            )
        )
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        log("SignUpActivity ", " onFailure $type")
        hideKeyboard(this)
        when (type) {
            "validation" -> {
                toast(message)
            }
            CommonKey.NO_INTERNET -> {
                showAlert(
                    this,
                    getString(R.string.h_no_internet),
                    getString(R.string.no_internet)
                )
            }
            CommonKey.ERROR_CODE_400 -> {
                showAlert(
                    this,
                    getString(R.string.warning),
                    getString(R.string.login_400)
                )
            }
            else -> {
                showAlert(
                    this,
                    getString(R.string.alert),
                    getString(R.string.no_response)
                )
            }
        }

    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE

        when (type) {
            "mainLogin" -> {
                val data: MainLoginResponse = dataG as MainLoginResponse
                val token = data.token
                viewModel.registerUser(
                    token,
                    StaticValue.REST_ID
                )
                PreferenceProvider(applicationContext).setStringValue(
                    data.token, PreferenceKey.APP_TOKEN
                )
            }
            "registerUser" -> {
                val data: UserRegistrationResponse = dataG as UserRegistrationResponse
                viewModel.createUser(
                    data.id.toString(), PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )!!
                )
            }
            "createUser" -> {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
                dialog.setContentView(R.layout.custom_dialog)
                val txtMsg = dialog.findViewById<TextView>(R.id.txtMsg)
                val button = dialog.findViewById<Button>(R.id.btnOk)
                val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
                val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
                val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
                txtHeading.text = getString(R.string.congratulations)
                Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_logo_instaapp))
                    .into(imgAppLogo)
                button.text = getString(R.string.ok)
                dialog.setCancelable(false)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog.window!!.attributes)
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                txtMsg.text = getString(R.string.registration_success)
                (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
                    val intent = Intent(
                        applicationContext, LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("callFrom", "Registration")
                    startActivity(intent)
                }
                imgCancel.setOnClickListener { dialog.dismiss() }
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
                dialog.window!!.attributes = lp
            }
        }

    }
}