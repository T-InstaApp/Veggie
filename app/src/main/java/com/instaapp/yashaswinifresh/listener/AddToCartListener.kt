package com.instaapp.yashaswinifresh.listener

import android.widget.AutoCompleteTextView
import android.widget.TextView

interface AddToCartListener {
    fun onStarted()
    fun onFailure(message: String, type: String)
    fun <T> onSuccess(dataG: T, type: String)
    fun <T> onSuccessForProductAddON(
        dataG: T,
        type: String,
        multiSelectText: TextView,
        strTitle: String,
        textID: TextView,
        minS:Int,maxS:Int,freeS:Int,availTime:String,
        txtExtraParentAddOnHeading: TextView, txtParentAddOnID: TextView,txtExtTotalPrice: TextView,position:Int
    )

    fun <T> onSuccessForProductAddON2(
        dataG: T,
        type: String,
        autoCompleteTextView: AutoCompleteTextView,
        textID: TextView
    )

    fun <T> onSuccessForProductAddONEdit(
        dataG: T,
        type: String,
        multiSelectText: TextView,
        strTitle: String,
        textID: TextView,
        testCount: Int,
        extraToppingPriceO: Double
    )
}