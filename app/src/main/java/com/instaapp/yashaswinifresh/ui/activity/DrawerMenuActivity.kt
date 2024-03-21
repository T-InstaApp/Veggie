package com.instaapp.yashaswinifresh.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.listener.HomeListener
import com.instaapp.yashaswinifresh.ui.adapter.DrawerAdapter
import com.instaapp.yashaswinifresh.utils.*
import com.instaapp.yashaswinifresh.viewModel.HomeViewModel
import com.instaapp.yashaswinifresh.viewModelFactory.HomeViewModelFactory
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class DrawerMenuActivity : AppCompatActivity(), HomeListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var gridView: GridView
    private lateinit var mContext: Context
    private lateinit var alertDialog: AlertDialog
    private lateinit var menuArray: Array<String>
    private lateinit var adapter: DrawerAdapter
    private lateinit var imgAppLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_menu)
        mContext = this

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this

        val loginStatus =
            PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)
        val userType =
            PreferenceProvider(applicationContext).getStringValue(PreferenceKey.USER_PROFILE)

        imgAppLogo = findViewById(R.id.imgAppLogo)
        Glide.with(applicationContext)
            .load(PreferenceProvider(applicationContext).getSplashScreenImageUrl()).into(imgAppLogo)

        log("DrawerMenuActivity", " $userType")
        menuArray = if (loginStatus) {
            if (userType == "1") {
                resources.getStringArray(R.array.menu_array_registered)
            } else {
                resources.getStringArray(R.array.menu_array_guest)
            }
        } else {
            resources.getStringArray(R.array.menu_array_logged_out)
        }
        gridView = findViewById(R.id.drawer_gridview)
        adapter = DrawerAdapter(mContext, menuArray)
        gridView.adapter = adapter
        gridView.setOnItemClickListener { _, _, position, _ ->
            log("gridView ", "Clicked $position ${menuArray[position]}")
            when (menuArray[position]) {
                getString(R.string.menu_menu) -> {
                    val intent = Intent(mContext, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                getString(R.string.menu_profile) -> {
                    val intent = Intent(mContext, Profile::class.java)
                    startActivity(intent)
                    finish()
                }
                getString(R.string.menu_orders) -> {
                    val intent = Intent(mContext, OrderListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                getString(R.string.cart) -> {
                    if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.RESTAURANT_OPEN_STATUS)) {
                        val intent = Intent(mContext, CartListActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        toast(getString(R.string.rest_closed))
                    }
                }
                getString(R.string.menu_aboutus) -> {
                    val intent = Intent(mContext, AboutUs::class.java)
                    startActivity(intent)
                    finish()
                }
                getString(R.string.menu_contactus) -> {
                    val intent = Intent(mContext, ContactUsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                getString(R.string.menu_log_out) -> logoutAlert()
                getString(R.string.menu_login) -> {
                    val intent = Intent(mContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("callFrom", "Home")
                    startActivity(intent)
                    finish()
                }
                getString(R.string.delete_account) -> deleteAlert()
            }
        }
    }

    private fun logoutAlert() {
        val li = LayoutInflater.from(mContext)
        val promptsView: View = li.inflate(R.layout.common_alert_layout2, null)
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setView(promptsView)
        val titalText = promptsView.findViewById<TextView>(R.id.alert_msg)
        titalText.text = "Are you sure you want to log out?"

        promptsView.findViewById<View>(R.id.btn_ok).setOnClickListener {
            alertDialog.cancel()
            PreferenceProvider(applicationContext).setBooleanValue(
                false, PreferenceKey.LOGIN_STATUS
            )
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_COUNT)
            PreferenceProvider(applicationContext).setStringValue("0", PreferenceKey.USER_PROFILE)
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_ID)
            CartManager.clearAllItem()
            val intent = Intent(mContext, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        promptsView.findViewById<View>(R.id.btn_cancel).setOnClickListener { alertDialog.cancel() }

        alertDialog = alertDialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun deleteAlert() {
        val li = LayoutInflater.from(mContext)
        val promptsView: View = li.inflate(R.layout.common_alert_layout2, null)
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setView(promptsView)
        val titalText = promptsView.findViewById<TextView>(R.id.alert_msg)
        titalText.text = "Are you sure you want to delete your account?"

        promptsView.findViewById<View>(R.id.btn_ok).setOnClickListener {
            alertDialog.cancel()
            viewModel.deleteAccount(
                "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID).toString()
            )
        }

        promptsView.findViewById<View>(R.id.btn_cancel).setOnClickListener { alertDialog.cancel() }

        alertDialog = alertDialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    override fun onStarted() {

    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(data: String, type: String) {
        if (type == "deleteAccount") {
            toast("Your account is Successfully deleted!!")
            PreferenceProvider(applicationContext).setBooleanValue(
                false,
                PreferenceKey.LOGIN_STATUS
            )
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_COUNT)
            PreferenceProvider(applicationContext).setStringValue("0", PreferenceKey.USER_PROFILE)
            PreferenceProvider(applicationContext).setIntValue(0, PreferenceKey.CART_ID)

            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

    }

    override fun <T> onSuccessData(dataG: T, type: String) {

    }

}