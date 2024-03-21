package com.instaapp.yashaswinifresh.interface_class

import android.view.View

interface RecyclerViewClickListener {
    //fun onRecyclerItemClick(view: View, catID: Int)
    fun <T> onRecyclerItemClick(view: View, dataG: T)
}