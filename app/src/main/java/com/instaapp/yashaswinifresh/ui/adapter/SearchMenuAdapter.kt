package com.instaapp.yashaswinifresh.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.bumptech.glide.Glide
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.GridMenuLayoutBinding
import com.instaapp.yashaswinifresh.databinding.MenuLayoutBinding
import com.instaapp.yashaswinifresh.interface_class.RecyclerViewClickListenerForAddToCart
import com.instaapp.yashaswinifresh.network.responseModel.MasterCategoryOpenHours
import com.instaapp.yashaswinifresh.network.responseModel.ProductSearch
import com.instaapp.yashaswinifresh.ui.activity.AddToCartActivity
import com.instaapp.yashaswinifresh.ui.activity.Home
import com.instaapp.yashaswinifresh.utils.*
import java.text.SimpleDateFormat
import java.util.*


class SearchMenuAdapter(
    private val searchData: List<ProductSearch>, private val context: Context,
    private val listener: RecyclerViewClickListenerForAddToCart
) :
    RecyclerView.Adapter<SearchMenuAdapter.SearchMenuViewHolder>() {

    override fun getItemCount() = searchData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchMenuViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.grid_menu_layout, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: SearchMenuViewHolder, position: Int) {
        Glide.with(context).load(searchData[position].productUrl)
            .into(holder.menuLayoutBinding.menuImg)

        holder.menuLayoutBinding.addToCardLayout.btnAddToCart.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.menuLayoutBinding.card, searchData[position], "ADD"
            )
        }
        holder.menuLayoutBinding.addToCardLayout.imgMinus.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.menuLayoutBinding.card, searchData[position], "Minus"
            )
        }
        holder.menuLayoutBinding.addToCardLayout.imgPlus.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.menuLayoutBinding.card, searchData[position], "Plus"
            )
        }

        if (CartManager.isProductInCart(searchData[position].productId)) {
            holder.menuLayoutBinding.addToCardLayout.btnAddToCart.visibility = View.GONE
            holder.menuLayoutBinding.addToCardLayout.layoutEditCart.visibility = View.VISIBLE
            holder.menuLayoutBinding.addToCardLayout.txtQTY.text =
                (CartManager.getCartForProduct(searchData[position].productId)!!.third).toString()
        }

        Glide.with(context)
            .load(searchData[position].productUrl)
            .error(R.drawable.no_img)
            .into(holder.menuLayoutBinding.menuImg)


        if (searchData[position].price > 0)
            holder.menuLayoutBinding.menuPrice.text =
                PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE) + searchData[position].price
        else
            holder.menuLayoutBinding.menuPrice.text = "Based on Your Choices"

        holder.menuLayoutBinding.mrpPrice.text =
            PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE) + searchData[position].MRP

        holder.menuLayoutBinding.menuName.text = "" + searchData[position].productName
        if (searchData[position].extra != "null")
            holder.menuLayoutBinding.menuDesc.text = "" + searchData[position].extra

        holder.menuLayoutBinding.menuImg.setOnClickListener {
            //  TODO Call DetailsActivity
            val intent = Intent(context, AddToCartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("product_id", searchData[position].productId.toString())
            intent.putExtra(
                "product_price", getFormatDoubleValue(searchData[position].price.toDouble())
            )
            intent.putExtra("product_name", searchData[position].productName)
            intent.putExtra("product_desc", searchData[position].extra)
            intent.putExtra("product_url", searchData[position].productUrl)
            intent.putExtra("category_id", searchData[position].category.categoryId)
            intent.putExtra("category_name", searchData[position].category.category)

            context.startActivity(intent)
        }

        try {
            if (!searchData[position].category.masterCategory!!.status!!) {
                holder.menuLayoutBinding.layoutOutOfStock.visibility = View.VISIBLE
                holder.menuLayoutBinding.card.isEnabled = false
            } else if (searchData[position].suspendedUntil != null && !parseDateSuspendedMenu(
                    searchData[position].suspendedUntil
                )
            ) {
                holder.menuLayoutBinding.layoutOutOfStock.visibility = View.VISIBLE
                holder.menuLayoutBinding.card.isEnabled = false
            } else if (!checkMasterCatOpenHour(searchData[position].category.masterCategory!!.MasterCategoryOpenHours!!)) {
                holder.menuLayoutBinding.layoutOutOfStock.visibility = View.VISIBLE
                holder.menuLayoutBinding.card.isEnabled = false
            } else if ((!searchData[position].category.availableTime.equals(
                    "All Time", ignoreCase = true
                ) && checkAvailableTime(
                    convertGMTtoLocalTime(searchData[position].category.availableTime!!)
                )) || (!searchData[position].inStock)
                || (!searchData[position].availableTime.equals("All Time", ignoreCase = true)
                        && checkAvailableTime(
                    convertGMTtoLocalTime(searchData[position].availableTime!!)
                ))
            ) {
                holder.menuLayoutBinding.layoutOutOfStock.visibility = View.VISIBLE
                holder.menuLayoutBinding.card.isEnabled = false
            }
        } catch (ignore: Exception) {
        }
    }

    private fun checkMasterCatOpenHour(openHours: ArrayList<MasterCategoryOpenHours>): Boolean {
        val day: String =
            SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis())
        for (k in openHours) {
            if (k.day.equals(day, ignoreCase = true)) {
                return if (k.status.toBoolean()) {
                    val start = k.startTime!!.split("@")[0]
                    val end = k.endTime.split("@")[0]
                    isShopOpen(
                        getCurrentTime(),
                        convertGMTtoLocalTime(start)!!,
                        convertGMTtoLocalTime(end)!!
                    )
                } else false
            }
        }
        return false
    }

    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return "$hour:$minute"
    }

    private fun isShopOpen(currentTime: String, shopStartTimes: String, shopEndTimes: String
    ): Boolean {
        Log.d("isShopOpen == ", " C=$currentTime && S=$shopStartTimes && E=$shopEndTimes")
        val dateFormat = SimpleDateFormat("HH:mm")
        val currentTimeDate = dateFormat.parse(currentTime)
        val shopStartDate = dateFormat.parse(shopStartTimes)
        val shopEndDate = dateFormat.parse(shopEndTimes)
        Log.d("isShopOpen == ", " C=$currentTimeDate && S=$shopStartDate && E=$shopEndDate")
        return currentTimeDate!!.after(shopStartDate) && currentTimeDate.before(shopEndDate)
    }

    inner class SearchMenuViewHolder(
        val menuLayoutBinding: GridMenuLayoutBinding
    ) : RecyclerView.ViewHolder(menuLayoutBinding.root)
}