package com.instaapp.yashaswinifresh.network.responseModel

data class ProductContainItem(
    val addon_content_id: Int,
    val parent_addon: ParentAddon,
    val price: String,
    val title: String,
    val is_subaddons:Boolean=false,
    val suspended_until: String?
)