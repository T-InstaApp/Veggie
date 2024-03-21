package com.instaapp.veggietemp1.interface_class

import android.view.View

interface RecyclerViewClickListenerForSubAddons {
    fun  onRecyclerViewItemSelect(check: Boolean, addonId:Int, addonContentId:Int,addonName:String)
}