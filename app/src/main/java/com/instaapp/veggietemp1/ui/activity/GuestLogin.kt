package com.instaapp.veggietemp1.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.instaapp.veggietemp1.listener.AuthListener
import com.instaapp.veggietemp1.network.responseModel.MobileVerifyResponse
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.AuthViewModel
import com.instaapp.veggietemp1.viewModelFactory.AuthViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityGuestMobileVerificationBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class GuestLogin : AppCompatActivity(), AuthListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    private lateinit var binding: ActivityGuestMobileVerificationBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_guest_mobile_verification)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.authListener = this
        viewModel.code = getCountryPhoneCode(applicationContext)
        binding.toolbar.imgBack.setOnClickListener { finish() }
        binding.toolbar.txtHeading.text = "Guest Login"

        binding.txtLogin.setOnClickListener {
            val intent=Intent(applicationContext, LoginActivity::class.java)
            intent.putExtra("callFrom", "GuestLogin")
            startActivity(intent)
            finish()
        }
        binding
        binding.etCode.setText(getCountryPhoneCode(applicationContext))
        binding.btnLogin.setOnClickListener {
            if (binding.etCode.text.isNullOrEmpty() || binding.etCode.text!!.isEmpty()) {
                toast("Enter your phone code")
            } else if (!binding.etCode.text.toString().trim().contains("+")) {
                toast("Enter valid country code")
            } else if (binding.etMobile.text.isNullOrEmpty() || binding.etMobile.text!!.length < 9) {
                toast("Enter your mobile no")
            } else {
                viewModel.generateOtp(
                    binding.etCode.text.toString(),
                    binding.etMobile.text.toString()
                )
            }
        }
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        when (type) {
            "validation" -> toast(message)
            else -> showAlert(
                this,
                getString(R.string.alert),
                message
            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        if (type == "generateOtp") {
            verifyOTP()
        } else if (type == "verifyOTP") {
            val data: MobileVerifyResponse = dataG as MobileVerifyResponse
            PreferenceProvider(applicationContext).setIntValue(
                data.id!!.toInt(), PreferenceKey.USER_ID
            )
            PreferenceProvider(applicationContext).setStringValue(
                data.token!!, PreferenceKey.APP_TOKEN
            )
            PreferenceProvider(applicationContext).setBooleanValue(true, PreferenceKey.LOGIN_STATUS)
            PreferenceProvider(applicationContext).setStringValue("2", PreferenceKey.USER_PROFILE)

            val intent = Intent(applicationContext, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun verifyOTP() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_forgot)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<EditText>(R.id.etInputData)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtInputHeading = dialog.findViewById<TextView>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<TextView>(R.id.btn_cancel)
        txtHeading.text = "Verify your mobile no."
        edtText.hint = getString(R.string.enter_otp)
        txtInputHeading.text = getString(R.string.enter_otp)
        edtText.inputType = InputType.TYPE_CLASS_NUMBER

        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener {
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                toast(getString(R.string.enter_otp))
            } else {
                dialog.dismiss()
                viewModel.verifyOTP(edtText.text.toString())
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }
}