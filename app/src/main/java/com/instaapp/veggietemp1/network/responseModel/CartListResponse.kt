package com.instaapp.veggietemp1.network.responseModel

import java.io.Serializable


data class CartListResponse(
    val count: Int?,
    val results: List<CartListResult>,
    val total_cost: String?,
)

data class CartListResult(
    val cart_item_id: Int?,
    val cart: Int?,
    val product: Product?,
    val size: Size?,
    val product_price: String?,
    val category_available_time: String?,
    val category_status: String?,
    val quantity: Int?,
    val price: String?,
    val extra: String?,
    val addon_content: List<AddonContent>?,
    val master_category_open_hour: Boolean = true,
    val cart_item_in_stock: Boolean = true
) : Serializable

data class Size(
    val category: Category?,
    val category_size_id: Int?,
    val size: String?
) : Serializable

data class Category(
    val category: String?,
    val category_id: Int?,
    val category_url: String?,
    val restaurant_id: Int?
) : Serializable

data class Product(
    val product_id: Int,
    val product_name: String,
    val product_url: String,
    val price: String,
    val media: String?,
    val caption: String?,
    val extra: String?,
    val image: String?,
    val status: String,
    val tax_exempt: Boolean,
    val source_id: String,
    val optional: Boolean,
    val sequence: Int,
    val in_stock: Boolean,
    val available_time: String,
    val delivery_type: String,
    val pos_id: String,
    val tax_rate: Int,
    val suspended_until: String?,
    val brand: String?,
    val model: String?,
    val sub_category: String?,
    val created_at: String,
    val restaurant: Int,
    val category: Int
) : Serializable

data class AddonContent(
    val cartitem_addon_id: Int?,
    val cartitem: CartItem?,
    val addon_content: AddonContentX?,
    val nested_parent_addon_content_id:String?

) : Serializable

data class AddonContentX(
    val addon_content_id: Int?,
    val parent_addon: ParentAddon?,
    val title: String?,
    val price: String?,
    val is_subaddons: Boolean = false,
    val pos_id: String?,
    val suspended_until: String?,
    val user_input_type: String?,
    val selection_value: String?,
    val Input_required: Boolean = false,
    val is_extra_data_required: Boolean = false
) : Serializable

data class ParentAddon(
    val addon_id: Int?,
    val addon_title: String?,
    val selection_method: String?,
    val addon_name:String?,
    val pos_id:String?,
    val allow_modifier_multiple_quantity:Boolean=false,
    val restaurant: Int?,
) : Serializable

data class CartItem(
    val cart_item_id: Int?,
    val quantity: Int?,
    val price: String?,
    val extra: String?,
    val cart: Int?,
    val product: Int?,
    val size: Int?
) : Serializable
