package com.instaapp.veggietemp1.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.listener.AuthListener
import com.instaapp.veggietemp1.network.requestModel.ChangePassword
import com.instaapp.veggietemp1.network.responseModel.ProfileResponse
import com.instaapp.veggietemp1.utils.*
import com.instaapp.veggietemp1.viewModel.AuthViewModel
import com.instaapp.veggietemp1.viewModelFactory.AuthViewModelFactory
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.ActivityProfileBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class Profile : AppCompatActivity(), AuthListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.authListener = this
        binding.updateProfileData = viewModel

        if (PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE) == "2") {
            binding.profileChangePass.visibility = View.GONE
        }

        viewModel.getUserProfile(
            "Token " + PreferenceProvider(applicationContext).getStringValue(
                PreferenceKey.APP_TOKEN
            )!!,
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
        )

        binding.profileUpdate.setOnClickListener {
            viewModel.onUpdateUserProfile(
                "Token " + PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )!!,
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
            )
        }

        binding.toolBar.txtHeading.text = getString(R.string.profile)
        binding.toolBar.imgBack.setOnClickListener { finish() }

        binding.profileChangePass.setOnClickListener { changePassword() }

        binding.txtDeleteYourAccount.setOnClickListener {
            alertDeleteAccount()
        }


        binding.toolBar.homeCartCount.text = PreferenceProvider(applicationContext).getIntValue(
            PreferenceKey.CART_COUNT
        ).toString()

        binding.toolBar.cartlayout.setOnClickListener {
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
        /*binding.profileSalutation.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.select_dialog_item,
                StaticValue.SALUTATION_DATA
            )
        )*/
    }

    override fun onStarted() {
        binding.progress.progressLayout.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        showAlert(
            this,
            getString(R.string.alert),
            message
        )
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progress.progressLayout.visibility = View.GONE
        when (type) {
            "onUpdateUserProfile" -> {
                viewModel.updateCustomerProfile(
                    "Token " + PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )!!,
                    PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
                        .toString()
                )
            }
            "updateCustomerProfile" -> {
                toast("Profile updated successfully")
            }
            "changePassword" -> {
                dialog.dismiss()
                showAlert(
                    this,
                    getString(R.string.congratulations),
                    getString(R.string.password_change_successfully)
                )
            }
            "deleteAccount" -> {
                toast("Your account is Successfully deleted!!")
                PreferenceProvider(applicationContext).setBooleanValue(
                    false,
                    PreferenceKey.LOGIN_STATUS
                )
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else -> {
                val data: ProfileResponse = dataG as ProfileResponse
                binding.profileData = data.results?.get(0)
            }
        }
    }

    private fun changePassword() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_pasword)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val etOldPassword = dialog.findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = dialog.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialog.findViewById<EditText>(R.id.etConfirmPassword)
        val btnSubmit = dialog.findViewById<AppCompatButton>(R.id.btnSubmit)

        Glide.with(this).load(PreferenceProvider(applicationContext).getSplashScreenImageUrl())
            .into(imgAppLogo)

        btnSubmit.setOnClickListener {
            hideKeyboard(this)
            if (etOldPassword.text.isNullOrEmpty() || etOldPassword.text.toString().length < 5) {
                toast("Please enter old password")
                return@setOnClickListener
            } else if (etNewPassword.text.isNullOrEmpty()) {
                toast("Please enter new password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString().length < 7) {
                toast("Password must be greater than 8 character")
                return@setOnClickListener
            } else if (etConfirmPassword.text.isNullOrEmpty() || etConfirmPassword.text.toString().length < 7) {
                toast("Please enter confirm  password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
                // toast("New password and confirm password does not match")
                toast("New password and confirm password does not match")
                return@setOnClickListener
            }
            viewModel.changePassword(
                "Token " + PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )!!, ChangePassword(
                    etOldPassword.text.toString(),
                    binding.etUserName.text.toString(),
                    etNewPassword.text.toString(),
                    etNewPassword.text.toString()
                )
            )
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp

    }

    private fun alertDeleteAccount() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val edtEmail = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)

        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_logo_instaapp))
            .into(imgAppLogo)

        button.text = getString(R.string.delete)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        edtEmail.text = "Are you sure you want to delete your account?"

        button.setOnClickListener {
            viewModel.deleteAccount(
                "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()

            )
        }
        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }


}