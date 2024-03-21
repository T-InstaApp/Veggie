package com.instaapp.yashaswinifresh.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.SingleItemOrderDetailBinding
import com.instaapp.yashaswinifresh.network.responseModel.OrderMenuDetailsResult

class OrderDetailsMenuAdapter(
    private val orderMenuData: List<OrderMenuDetailsResult>,
) :
    RecyclerView.Adapter<OrderDetailsMenuAdapter.OrderViewHolder>() {

    override fun getItemCount() = orderMenuData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.single_item_order_detail, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.orderListItemBinding.orderListMenuData = orderMenuData[position]
        holder.orderListItemBinding.txtDetailSeeMore.setOnClickListener {
            if (holder.orderListItemBinding.txtDetailSeeMore.text.toString() == "See More") {
                holder.orderListItemBinding.txtIngredient.maxLines = Int.MAX_VALUE //your TextView
                holder.orderListItemBinding.txtDetailSeeMore.text = "Show Less"
            } else {
                holder.orderListItemBinding.txtIngredient.maxLines = 3 //your TextView
                holder.orderListItemBinding.txtDetailSeeMore.text = "See More"
            }
        }
    }

    inner class OrderViewHolder(
        val orderListItemBinding: SingleItemOrderDetailBinding
    ) : RecyclerView.ViewHolder(orderListItemBinding.root)
}