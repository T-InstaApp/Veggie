package com.instaapp.veggietemp1.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.instaapp.veggietemp1.MainActivity
import com.instaapp.veggietemp1.network.responseModel.AddonContent
import com.instaapp.veggietemp1.network.responseModel.ProductContainItem
import com.instaapp.vegiestemp1.R
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun log(tag: String, message: String) {
    Log.d(tag, message)
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}


fun showAlert(activity: Activity, title: String, message: String) {
    AlertDialog.Builder(activity)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            "Ok"
        ) { dialog, _ -> dialog?.dismiss() }.show()
}

fun showAlertWithAndCloseActivity(activity: Activity, title: String, message: String) {
    AlertDialog.Builder(activity)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            dialog?.dismiss()
            activity.finish()
        }.show()
}

fun hideKeyboard(activity: Activity) {
    try {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    } catch (ignore: Exception) {
    }
}

fun CurrentTimeStamp(): String {
    //Date object
    val date = Date()
    //getTime() returns current time in milliseconds
    val time = date.time
    //Passed the milliseconds to constructor of Timestamp class
    val ts = Timestamp(time)
    return ts.toString()
}


fun getIngredientData(
    context: Context,
    data: List<AddonContent>,
    menuSize: String?,
    qty: Int
): String {
    val length = data.size
    var ingrPrice = 0.0
    var ingrSinglePrice = 0.0
    var strIngredient = ""

    for (i in 0 until length) {
        if (data[i].addon_content!!.parent_addon!!.addon_title == "Toppings" || data[i].addon_content!!.parent_addon!!.addon_title == "Extra Toppings") {
            log("getIngredientData", " 11 ")
            if (menuSize != null) {
                if (menuSize == "Small") {
                    val price = (1.10) * qty
                    val s = String.format("%.2f", price)
                    ingrPrice += price
                    strIngredient =
                        strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                            context
                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + s + " \n"
                    ingrSinglePrice += 1.10
                } else if (menuSize == "Medium") {
                    val price = (1.20) * qty
                    val s = String.format("%.2f", price)
                    ingrPrice += price
                    strIngredient =
                        strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                            context
                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + s + " \n"
                    ingrSinglePrice += 1.20
                } else if (menuSize == "Large") {
                    val price = (1.30) * qty
                    val s = String.format("%.2f", price)
                    ingrPrice += price
                    strIngredient =
                        strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                            context
                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + s + " \n"
                    ingrSinglePrice += 1.30
                } else {
                    val price: Double =
                        java.lang.Double.valueOf(data[i].addon_content!!.price.toString())
                    ingrPrice += price * qty
                    ingrSinglePrice += price
                    strIngredient =
                        strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                            context
                        ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + (price * qty) + " \n"
                }

            } else {
                val price: Double =
                    java.lang.Double.valueOf(data[i].addon_content!!.price.toString())
                ingrPrice += price * qty
                val s = String.format("%.2f", price * qty)
                ingrSinglePrice += price
                strIngredient =
                    strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                        context
                    ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + s + " \n"
            }
        } else {

            val price: Double = java.lang.Double.valueOf(data[i].addon_content!!.price.toString())
            log("getIngredientData", " Size $price")

            if (price != 0.0) {
                val s = String.format("%.2f", price * qty)
                strIngredient =
                    strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " " + PreferenceProvider(
                        context
                    ).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + s + " \n"

            } else {
                strIngredient =
                    strIngredient + (i + 1) + ". " + data[i].addon_content!!.parent_addon!!.addon_title + " : " + data[i].addon_content!!.title + " \n"
            }
            ingrPrice += price * qty
            ingrSinglePrice += price
        }
    }

    //log("getIngredientData", " Size $strIngredient")
    return strIngredient

}

fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}

fun dialogSessionExpire(context: Context) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
    dialog.setContentView(R.layout.custom_dialog)

    val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
    txtHeading.text = context.resources.getString(R.string.Session_first_line)
    val txtMsg = dialog.findViewById<TextView>(R.id.txtMsg)
    txtMsg.text = context.resources.getString(R.string.Session_second_line)


    dialog.setCancelable(false)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        dialog.dismiss()
    }
    (dialog.findViewById<View>(R.id.imgCancel) as ImageView).visibility = View.GONE

    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    dialog.window!!.attributes = lp
}

fun getAddOnPrice(
    context: Context,
    addonTitle: String,
    type: Int,
    sizeId: String
): Double {
    var price: String
    price = if (type == 1)
        addonTitle
    else
        addonTitle.split(PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE)!!)[1].replace(
            ")",
            ""
        )

    when (sizeId) {
        "1" -> {
            price = "1.10"
        }
        "2" -> {
            price = "1.20"
        }
        "3" -> {
            price = "1.30"
        }
    }
    Log.d("TAG", "getAddOnPrice: $price  SizeID=$sizeId")
    return price.toDouble()
}

fun getAddOnId(
    addOnDataCollection: ArrayList<ProductContainItem>,
    addonTitle: String
): String {
    Log.d("TAG", "getAddOnId: ${addOnDataCollection.size}")
    for (i in 0 until addOnDataCollection.size) {
        if (addOnDataCollection[i].title.trim().equals(addonTitle.trim(), ignoreCase = true)) {
            return addOnDataCollection[i].addon_content_id.toString()
        }
    }
    return ""
}

@SuppressLint("SimpleDateFormat")
fun checkAvailableTime(dateTime: String?): Boolean {
    var status = false
    try {
        val dateFormat = SimpleDateFormat("HH:mm")
        val endTime = dateFormat.parse(dateTime!!)
        val currentTime = dateFormat.parse(dateFormat.format(Date()))
        if (currentTime != null) {
            status = !currentTime.before(endTime)
        }
    } catch (ignore: java.lang.Exception) {
    }
    return status
}

fun parseDateSuspendedMenu(data: String?): Boolean {
    return try {
        var strTempDate = data
        strTempDate =
            strTempDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val compareDate = sdf.parse(strTempDate)
        val c = Calendar.getInstance()
        val strCurrentDate = sdf.format(c.time)
        val currentDate = sdf.parse(strCurrentDate)!!
        Log.d("TAG", "parseDateSuspendedMenu: " + currentDate.after(compareDate))
        currentDate.after(compareDate)
    } catch (ex: java.lang.Exception) {
        Log.d("TAG", "parseDateSuspended: ex$ex")
        true
    }
}

fun getFormatDoubleValue(value: Double): String? {
    val numberFormatBtc = DecimalFormat("#0.00")
    return numberFormatBtc.format(value)
}

fun convertGMTtoLocalTime(gmtTime: String): String? {
    return gmtTime
    /* return try {
         // Create a date object with today's date and the GMT time
         val gmtFormat = SimpleDateFormat("HH:mm")
         gmtFormat.timeZone = TimeZone.getTimeZone("GMT")
         val gmtDate: Date = gmtFormat.parse(gmtTime)!!

         // Convert GMT time to local time
         val localFormat = SimpleDateFormat("HH:mm")
         localFormat.timeZone = TimeZone.getDefault() // Use the default time zone of the device
         val localTime: String = localFormat.format(gmtDate)
         localTime
     } catch (e: Exception) {
         e.printStackTrace()
         null
     }*/
}

fun getCountryPhoneCode(context: Context): String {
    try {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCode = telephonyManager.networkCountryIso
        log("HomeTestCountry ", " $countryCode")
        val phoneCodeMap = mapOf(
            "US" to "+1", // United States
            "CA" to "+1", // Canada
            "GB" to "+44", // United Kingdom
            "IN" to "+91",
            // Add more country code mappings as needed
        )
        return phoneCodeMap[countryCode.toUpperCase()] ?: "Unknown"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "Unknown"
}

fun cleanAddress(input: String): String {
    // Remove consecutive whitespaces and commas, and the trailing comma
    val cleanedString =
        input.trim().replace("\\s+,+".toRegex(), "").replace(",+".toRegex(), ",").replace(",$", "")
    return cleanedString.trim().trimEnd(',')
}

fun customRounding(amount: Double): Int {
    val decimalPart = amount % 1.0
    return if (decimalPart >= 0.5) {
        ceil(amount).toInt()
    } else {
        amount.toInt()
    }
}