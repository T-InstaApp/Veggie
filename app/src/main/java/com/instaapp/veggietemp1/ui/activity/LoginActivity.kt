package com.instaapp.veggietemp1.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.listener.AuthListener
import com.instaapp.veggietemp1.network.responseModel.MainLoginResponse
import com.instaapp.veggietemp1.network.responseModel.UserIDResponse
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.AuthViewModel
import com.instaapp.veggietemp1.viewModelFactory.AuthViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityLoginBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: ActivityLoginBinding
    private var callFromActivity: String = "Home"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.authListener = this

        callFromActivity = intent.getStringExtra("callFrom")!!

        binding.txtAlreadyRegister.setOnClickListener {
            val i = Intent(this, SignUpActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.toolbar.txtHeading.text = "Login"
        binding.toolbar.imgBack.setOnClickListener { finish() }
        binding.txtLoginAsGuest.setOnClickListener {
            val i = Intent(this, GuestLogin::class.java)
            startActivity(i)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.onLoginButton(
                binding.etUserName.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                StaticValue.REST_ID
            )
        }
        binding.txtForgotUserName.setOnClickListener {
            forgotUserPassword("User")
        }
        binding.txtForgotPassword.setOnClickListener {
            forgotUserPassword("Password")
        }

    }

    @SuppressLint("SetTextI18n")
    private fun forgotUserPassword(type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_forgot)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<EditText>(R.id.etInputData)
        val txtInputHeading = dialog.findViewById<TextView>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)

        if (type == "User") {
            edtText.hint = getString(R.string.enter_your_email)
            txtInputHeading.text = getString(R.string.email)
            txtHeading.text = "Reset your username"
        } else {
            edtText.hint = getString(R.string.enter_your_user_name)
            txtInputHeading.text = getString(R.string.user_name)
            txtHeading.text = "Reset your password"
        }
        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener {
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                if (type == "User")
                    toast(getString(R.string.enter_your_email))
                else
                    toast(getString(R.string.enter_your_user_name))
            } else {
                dialog.dismiss()
                if (type == "User")
                    viewModel.resetUserName(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
                    )
                else if (type == "Password")
                    viewModel.resetPassword(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
                    )
            }
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        when (type) {
            CommonKey.NO_INTERNET -> {
                showAlert(
                    this,
                    getString(R.string.h_no_internet),
                    getString(R.string.no_internet)
                )
            }
            CommonKey.ERROR_CODE_401 -> {
                showAlert(
                    this,
                    getString(R.string.h_login_401),
                    getString(R.string.login_401)
                )
            }
            CommonKey.ERROR_CODE_403 -> {
                if (message.contains(getString(R.string.no_restro_access))) {
                    RestroAccess()
                } else showAlert(this, getString(R.string.h_login_403), message)
            }
            else -> showAlert(this, getString(R.string.alert), message)
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        when (type) {
            "userLoginCheck" -> {
                val data: MainLoginResponse = dataG as MainLoginResponse
                PreferenceProvider(applicationContext).setIntValue(
                    data.id.toInt(), PreferenceKey.USER_ID
                )
                PreferenceProvider(applicationContext).setStringValue(
                    data.token, PreferenceKey.APP_TOKEN
                )
                PreferenceProvider(applicationContext).setBooleanValue(
                    true, PreferenceKey.LOGIN_STATUS
                )
                PreferenceProvider(applicationContext).setStringValue(
                    "1", PreferenceKey.USER_PROFILE
                )
                if (callFromActivity != "AddToCart") {
                    val intent = Intent(this, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                finish()
            }
            "MainLogin" -> {
                val data: MainLoginResponse = dataG as MainLoginResponse
                PreferenceProvider(applicationContext).setStringValue(
                    data.token,
                    PreferenceKey.APP_TOKEN
                )
                viewModel.getUserID(data.token)

            }
            "getUserID" -> {
                val data: UserIDResponse = dataG as UserIDResponse
                viewModel.insertUser(
                    PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
                    data.results?.get(0)?.id.toString(),
                    PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID)
                        .toString()
                )
            }
            "insertUser" -> {
                viewModel.checkLogin(
                    PreferenceProvider(applicationContext).getIntValue(PreferenceKey.REST_ID)
                        .toString()
                )
            }
            "resetUserName" -> {
                showAlert(
                    this,
                    getString(R.string.congratulations),
                    getString(R.string.username_reset_success)
                )
            }
            "resetPassword" -> {
                showAlert(
                    this,
                    getString(R.string.congratulations),
                    getString(R.string.password_reset_success)
                )
            }
        }
    }

    private fun RestroAccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val edtEmail = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)

        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_logo_instaapp))
            .into(imgAppLogo)

        button.text = getString(R.string.ok)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        edtEmail.text = resources.getString(R.string.confirm_user)
        (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
            viewModel.mainLogin(
                StaticValue.REST_USER_NAME,
                StaticValue.REST_PASSWORD,
                StaticValue.TEMP_RESTRO_ID
            )
        }
        button.setOnClickListener { dialog.dismiss() }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }
}