package com.instaapp.veggietemp1.network.responseModel

data class MasterCategoryDataModel(
    val master_category_id: Int?,
    val open_hours: ArrayList<OpenHours>?,
    val master_category_name: String?,
    val pos_id: String?,
    val restaurant_open: Boolean?,
    val restaurant_id: Int?
)

data class OpenHours(
    val Hours_id: Int?,
    val day: String?,
    val start_time: String?,
    val end_time: String?,
    val status: Boolean?,
    val master_category: Int?
)
