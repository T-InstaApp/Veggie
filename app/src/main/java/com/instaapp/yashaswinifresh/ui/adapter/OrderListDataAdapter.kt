package com.instaapp.yashaswinifresh.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.databinding.SingleItemOrderListBinding
import com.instaapp.yashaswinifresh.interface_class.RecyclerViewClickListener
import com.instaapp.yashaswinifresh.network.responseModel.OrderListResponse
import com.instaapp.yashaswinifresh.ui.activity.Home
import com.instaapp.yashaswinifresh.ui.activity.TrackOrderActivity
import com.instaapp.yashaswinifresh.utils.log

class OrderListDataAdapter(
    private val orderData: List<OrderListResponse>,
    private val listener: RecyclerViewClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<OrderListDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = orderData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.single_item_order_list, parent, false
        )
    )


    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

        holder.orderListItemBinding.orderListData = orderData[position]

        holder.orderListItemBinding.headerLayout.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.orderListItemBinding.headerLayout, orderData[position]
                //catData[position].category_id!!
            )
        }
        if (orderData[position].order!!.borzo_point_data == null)
            holder.orderListItemBinding.btnTrack.visibility = View.GONE

        // android:text='@{orderListData.shippingmethod.name.contains(`Delivery`) ? "Order Type - Delivery" : "Order Type - Pickup"}'

        if (orderData[position].shippingmethod.name!!.contains("Delivery"))
            holder.orderListItemBinding.txtOrderType.text = "Delivery"
        else
            holder.orderListItemBinding.txtOrderType.text = "Pickup"


        holder.orderListItemBinding.btnTrack.setOnClickListener {
            if (orderData[position].order!!.borzo_point_data != null) {
                val intent = Intent(context, TrackOrderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(
                    "tracking_url", orderData[position].order!!.borzo_point_data!!.tracking_url
                )
                intent.putExtra("OrderId", orderData[position].order!!.order_id.toString())
                context.startActivity(intent)
            } else
                Toast.makeText(context, "Tracking URL not available!!", Toast.LENGTH_SHORT).show()
        }
    }

    inner class OrderViewHolder(
        val orderListItemBinding: SingleItemOrderListBinding
    ) : RecyclerView.ViewHolder(orderListItemBinding.root)
}