package com.instaapp.veggietemp1.interface_class



interface OnAddOnBaseAdapterClick {
    fun onBaseAdapterClick(
        item: Any,
        name: String,
        prise: String,
      //  holder: AddOnBaseAdapter.BaseViewHolder,
        position: Int
    )
}