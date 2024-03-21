package com.instaapp.veggietemp1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.veggietemp1.interface_class.RecyclerViewCartClickListener
import com.instaapp.veggietemp1.network.responseModel.CartListResult
import com.instaapp.veggietemp1.utils.*
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.CartItemLayoutBinding

class CartListDataAdapter(
    private val cartData: List<CartListResult>,
    private val listener: RecyclerViewCartClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<CartListDataAdapter.OrderViewHolder>() {
    var count = 0
    override fun getItemCount() = cartData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_item_layout, parent, false
        )
    )


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.cartListItemBinding.cartListData = cartData[position]

        val strIngredient = getIngredientData(
            context, cartData[position].addon_content!!,
            cartData[position].size?.size, cartData[position].quantity!!
        )

        holder.cartListItemBinding.txtAddOns.text = strIngredient

        holder.cartListItemBinding.txtDetailSeeMore.setOnClickListener {
            if (holder.cartListItemBinding.txtDetailSeeMore.text.toString() == "See More") {
                holder.cartListItemBinding.txtAddOns.maxLines = Int.MAX_VALUE //your TextView
                holder.cartListItemBinding.txtDetailSeeMore.text = "Show Less"
            } else {
                holder.cartListItemBinding.txtAddOns.maxLines = 3 //your TextView
                holder.cartListItemBinding.txtDetailSeeMore.text = "See More"
            }
        }

        holder.cartListItemBinding.cartMenuDeleteBtn.setOnClickListener {
            val catId: String = cartData[position].cart_item_id.toString()
            listener.addSubDeleteItem(
                cartData[position],
                "",
                position,
                "",
                0.0,
                count.toString(),
                "Delete",
                catId
            )
        }

        holder.cartListItemBinding.cartMenuAddBtn.setOnClickListener {
            val seqId: String = cartData[position].size?.category_size_id.toString()
            val qty: Int = holder.cartListItemBinding.cartMenuQuantityText.text.toString().toInt()
            if (qty > 0) {
                count = (qty + 1)
                val productID: String = cartData[position].product!!.product_id.toString() + ""
                val cartItemId: String = cartData[position].cart_item_id.toString() + ""
                val b: Double = java.lang.Double.valueOf(
                    holder.cartListItemBinding.cartMenuAmountText.text.toString().trim()
                        .replace(
                            PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                            ""
                        )
                )
                val price: Double = roundOffDecimal((count * (b / qty)))

                holder.cartListItemBinding.cartMenuAmountText.text =
                    PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + price.toString()
                holder.cartListItemBinding.cartMenuQuantityText.text = count.toString()
                listener.addSubDeleteItem(
                    cartData[position],
                    productID,
                    position,
                    seqId,
                    price,
                    count.toString(),
                    "add",
                    cartItemId
                )
            }
        }

        holder.cartListItemBinding.cartMenuMinusBtn.setOnClickListener {
            val qty: Int = holder.cartListItemBinding.cartMenuQuantityText.text.toString().toInt()
            if (qty > 1) {
                val seqId: String = cartData[position].size?.category_size_id.toString()
                count = (qty - 1)
                val productID: String = cartData[position].product!!.product_id.toString() + ""
                val cartItemId: String = cartData[position].cart_item_id.toString() + ""
                val b: Double = java.lang.Double.valueOf(
                    holder.cartListItemBinding.cartMenuAmountText.text.toString().trim()
                        .replace(
                            PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE)!!,
                            ""
                        )
                )
                val price: Double = roundOffDecimal((count * (b / qty)))

                holder.cartListItemBinding.cartMenuAmountText.text =
                    PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE)!! + " " + price.toString()
                holder.cartListItemBinding.cartMenuQuantityText.text = count.toString()

                listener.addSubDeleteItem(
                    cartData[position],
                    productID,
                    position,
                    seqId,
                    price,
                    count.toString(),
                    "sub",
                    cartItemId
                )
            }
        }


        if (!cartData[position].category_available_time.equals("All Time", ignoreCase = true)
            && checkAvailableTime(convertGMTtoLocalTime(cartData[position].category_available_time!!))
            || (!cartData[position].product!!.in_stock) || (cartData[position].category_status.equals(
                "INACTIVE", ignoreCase = true
            )) || (!cartData[position].product!!.available_time.equals(
                "All Time", ignoreCase = true
            )) && checkAvailableTime(convertGMTtoLocalTime(cartData[position].product!!.available_time))
            || !cartData[position].master_category_open_hour || !cartData[position].cart_item_in_stock
            || !parseDateSuspendedMenu(cartData[position].product!!.suspended_until)
        ) {
            holder.cartListItemBinding.txtNotAvailable.visibility = View.VISIBLE
        }
    }

    inner class OrderViewHolder(
        val cartListItemBinding: CartItemLayoutBinding
    ) : RecyclerView.ViewHolder(cartListItemBinding.root)
}