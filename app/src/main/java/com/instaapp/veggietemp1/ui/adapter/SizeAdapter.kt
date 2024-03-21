package com.instaapp.veggietemp1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.veggietemp1.interface_class.SizeAdapterOnClick
import com.instaapp.veggietemp1.network.responseModel.FetchSizeItem
import com.instaapp.veggietemp1.utils.PreferenceKey
import com.instaapp.veggietemp1.utils.PreferenceProvider
import com.instaapp.vegiestemp1.R


class SizeAdapter(
    private val adapterOnClick: SizeAdapterOnClick,
    var sizeName: String,
    private var context: Context
) :
    ListAdapter<FetchSizeItem, SizeAdapter.BooksViewHolder>(DiffUtil()) {
    private var lastCheckedPosition = -1

    class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.findViewById<TextView>(R.id.txt_id)
        }
    }


    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_item_size, parent, false)
        return BooksViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind()
        val txtID = holder.itemView.findViewById<RadioButton>(R.id.txt_id)
        txtID.isChecked = position == lastCheckedPosition

        txtID.text =
            item.size.size + " (" + PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_TYPE) + item.price + ")"

        var isSelect = false
        txtID.setOnClickListener {
            if (!isSelect) {
                isSelect = true
                adapterOnClick.sizeOnClick(
                    item.size.category_size_id!!.toInt(),
                    "size",
                    item.price,
                    false
                )
                val copyOfLastCheckedPosition: Int = lastCheckedPosition
                lastCheckedPosition = holder.adapterPosition
                notifyItemChanged(copyOfLastCheckedPosition)
                notifyItemChanged(lastCheckedPosition)
            }
        }
        if (sizeName.equals(item.size.size, ignoreCase = true)) {
            txtID.isChecked = true
            sizeName = ""
            lastCheckedPosition = holder.adapterPosition
            adapterOnClick.sizeOnClick(
                item.size.category_size_id!!.toInt(),
                "size",
                item.price,
                true
            )
        }

    }


    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<FetchSizeItem>() {
        override fun areItemsTheSame(oldItem: FetchSizeItem, newItem: FetchSizeItem): Boolean {
            return oldItem.size.category_size_id == newItem.size.category_size_id

        }

        override fun areContentsTheSame(oldItem: FetchSizeItem, newItem: FetchSizeItem): Boolean {
            return oldItem == newItem
        }
    }


}