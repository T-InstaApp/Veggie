package com.instaapp.veggietemp1.utils

data class AddonsSelectedDataModel(
    val addonContentName: String?,
    val addonContentPrice: String?,
    val addonContentID: Int?,
    var totalCountInt: Int = 0,
    var isSelected: Boolean = false,
    var totalSelectedCount: Int,
    var isSubAddonAvailable: Boolean,
    val parentAddonID: Int?,
    val suspended_until: String?

)
