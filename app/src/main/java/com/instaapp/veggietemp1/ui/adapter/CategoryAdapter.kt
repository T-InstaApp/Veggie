package com.instaapp.veggietemp1.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.instaapp.veggietemp1.network.responseModel.CategoryResponse
import com.instaapp.veggietemp1.ui.activity.Home
import com.instaapp.veggietemp1.ui.activity.MenuActivity
import com.instaapp.veggietemp1.utils.checkAvailableTime
import com.instaapp.veggietemp1.utils.log
import com.instaapp.vegiestemp1.R
import com.instaapp.vegiestemp1.databinding.HomeMenuLayoutBinding

var selectedItem = 0

class CategoryAdapter(
    private val catData: List<CategoryResponse>, private val context: Context
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun getItemCount() = catData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_menu_layout, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryListItemBinding.categoryData = catData[position]



        //TODO Call Menu Activity
        holder.categoryListItemBinding.mainLayout.setOnClickListener { v: View? ->
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("cat_id", catData[position].category_id)
            intent.putExtra("cat_name", catData[position].category)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        if (!catData[position].available_time.equals(
                "All Time",
                ignoreCase = true
            ) && checkAvailableTime(catData[position].available_time)
        ) {
            holder.categoryListItemBinding.layoutOutOfStock.visibility = View.VISIBLE
            holder.categoryListItemBinding.mainLayout.isEnabled=false
        }
        log("CategoryAdapter ImageURl", catData[position].category_url!! + " " + catData[position].category_id)
    }

    inner class CategoryViewHolder(
        val categoryListItemBinding: HomeMenuLayoutBinding
    ) : RecyclerView.ViewHolder(categoryListItemBinding.root)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}