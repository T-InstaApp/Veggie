package com.instaapp.veggietemp1.utils

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.instaapp.veggietemp1.network.responseModel.AddonContent
import com.instaapp.veggietemp1.network.responseModel.AddressListResultResponse
import com.instaapp.veggietemp1.network.responseModel.OrderMenuDetailsAddonContent
import java.lang.Double
import java.text.*
import java.util.*
import kotlin.Boolean
import kotlin.String
import kotlin.plus

@BindingAdapter("image")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view).load(url).into(view)
}

@BindingAdapter("imageRound")
fun loadRoundImage(view: RoundedImageView, url: String) {
    log("loadRoundImage", " URL $url")
    if (url.length > 5) {
        Glide.with(view).load(url).into(view)
    }

}

@SuppressLint("SetTextI18n")
@BindingAdapter("setPriceWithCurrency")
fun setPriceWithCurrency(view: TextView, price: String?) {
    if (price != null)
        view.text =
            PreferenceProvider(view.context).getStringValue(PreferenceKey.CURRENCY_TYPE) + " " + price
    else
        view.text =
            PreferenceProvider(view.context).getStringValue(PreferenceKey.CURRENCY_TYPE) + " 0.0"
}

@BindingAdapter("dateFormat")
fun formatDate(view: TextView, date1: String) {
    val old = "yyyy-MM-dd"
    val newFormat = "dd MMMM yyyy"
    var changeDate: String? = null
    try {
        val format: DateFormat = SimpleDateFormat(old, Locale.ENGLISH)
        val date = format.parse(date1)
        val newformat = SimpleDateFormat(newFormat, Locale.ENGLISH)
        val newDate = newformat.format(date!!)
        changeDate =
            newDate.split(" ".toRegex())
                .toTypedArray()[1] + " " + newDate.split(" ".toRegex())
                .toTypedArray()[0] + ", " + newDate.split(" ".toRegex()).toTypedArray()[2]
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    view.text = changeDate
}

@BindingAdapter("TextTitle")
fun setTextTitle(view: TextView, title: String) {
    view.text = title
}

@BindingAdapter("SetEditTextValue")
fun editTextValue(view: EditText, title: String) {
    view.setText(title)
}

@BindingAdapter("Ingredient")
fun setIngredient(view: TextView, ingredientarray: List<OrderMenuDetailsAddonContent>) {
    var strIngredient = ""
    for (i in ingredientarray.indices) {
        strIngredient =
            strIngredient + (i + 1) + "." + ingredientarray[i].addon_content!!.parent_addon.addon_title + " : " + ingredientarray[i].addon_content!!.title + " " + " Price : " + PreferenceProvider(
                view.context
            ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + ingredientarray[i].addon_content!!.price + " \n"
    }
    view.text = strIngredient
}

@BindingAdapter("CartIngredient")
fun setCartIngredient(view: TextView, ingredientarray: List<AddonContent>) {
    var strIngredient = ""
    for (i in ingredientarray.indices) {
        strIngredient =
            strIngredient + (i + 1) + "." + ingredientarray[i].addon_content!!.parent_addon!!.addon_title + " : " + ingredientarray[i].addon_content!!.title + " " + " Price : " + PreferenceProvider(
                view.context
            ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + ingredientarray[i].addon_content!!.price + " \n"
    }
    view.text = strIngredient

}

@BindingAdapter("shippingAddress")
fun setOldShippingAddress(view: TextView, data: AddressListResultResponse?) {
    log("Calleds ", " 12")
    if (data != null) {
        val address =
            data.name + ", " + data.house_number + ", " + data.address + ", " + data.city + ", " + data.country + ", " + data.zip
        view.text = address
    }
}

@BindingAdapter("enableDisableEditText")
fun setEnableDisableEditText(view: EditText, data: List<AddressListResultResponse>?) {
    if (data != null) {
        view.isEnabled = data.isEmpty()
    }
}

@BindingAdapter("enable")
fun setEnable(view: EditText, data: Boolean?) {
    if (data != null) {
        view.isEnabled = data
    }
}

fun convertToUSDFormat(value: String?): String? {
    val numberFormat = DecimalFormat("######0.00")
    return numberFormat.format(Double.valueOf(value!!))
}