package com.instaapp.yashaswinifresh.interface_class



interface OnAddOnBaseAdapterClick {
    fun onBaseAdapterClick(
        item: Any,
        name: String,
        prise: String,
      //  holder: AddOnBaseAdapter.BaseViewHolder,
        position: Int
    )
}