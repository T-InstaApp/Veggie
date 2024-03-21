package com.instaapp.yashaswinifresh.network.responseModel

data class ProductSearchResponse(val data: Data)

data class Data(
    val productSearch: List<ProductSearch>
)

data class ProductSearch(
    val category: SearchCategory,
    val extra: String,
    val price: Double,
    val productName: String,
    val productUrl: String,
    val MRP: String?,
    val taxExempt: Boolean,
    var productId: Int,
    val availableTime: String?,
    val inStock: Boolean = true,
    val deliveryType: String?,
    val suspendedUntil: String?,
) : java.io.Serializable


data class SearchCategory(
    val category: String,
    val categoryId: String,
    val availableTime: String?,
    val masterCategory: MasterCategory?
)

data class MasterCategory(
    val masterCategoryId: Int,
    val status: Boolean? = true,
    val MasterCategoryOpenHours: ArrayList<MasterCategoryOpenHours>?
)

data class MasterCategoryOpenHours(
    val day: String,
    val startTime: String?,
    val endTime: String,
    val status: String?,
)