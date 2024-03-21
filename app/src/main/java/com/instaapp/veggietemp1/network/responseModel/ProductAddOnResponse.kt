package com.instaapp.veggietemp1.network.responseModel

class ProductAddOnResponse : ArrayList<ProductAddonItem>()

data class ProductAddonItem(
 val addon: Addon,
    val addon_quantity: Int,
    val addon_sequence: Int,
    val product: Int,
    val product_addon_id: Int,
    val size: Size,
    val min_selection_toppings: Int?,
    val max_selection_toppings: Int?,
    val free_topping_count: Int?,
    val available_time: String?,
    val status: Boolean = true

    )

data class ProductAddonSize(
    val category: Int?,
    val category_size_id: Int?,
    val size: String?
)

data class Addon(
    val addon_id: Int?,
    val addon_title: String?,
    val restaurant: Int?,
    val selection_method: String?
)

class AddonContentSubAddon : ArrayList<AddonContentSubAddonItem>()
data class AddonContentSubAddonItem(
    val addon: Addon,
    val addon_quantity: Int?,
    val addon_sequence: Int?,
    val min_selection_toppings: Int?,
    val max_selection_toppings: Int?,
    val free_topping_count: Int?,
    val available_time: String?,
    val status:Boolean=true,
)
