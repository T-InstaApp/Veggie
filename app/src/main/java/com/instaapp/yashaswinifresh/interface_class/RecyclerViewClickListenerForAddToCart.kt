package com.instaapp.yashaswinifresh.interface_class

import android.view.View
import androidx.cardview.widget.CardView

interface RecyclerViewClickListenerForAddToCart {
    //fun onRecyclerItemClick(view: View, catID: Int)
    fun <T> onRecyclerItemClick(cardView:CardView,dataG: T,type:String)
}