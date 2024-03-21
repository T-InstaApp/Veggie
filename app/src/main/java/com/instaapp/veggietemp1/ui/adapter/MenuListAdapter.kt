package com.instaapp.veggietemp1.ui.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.instaapp.veggietemp1.interface_class.RecyclerViewClickListenerForAddToCart
import com.instaapp.veggietemp1.network.responseModel.MenuResponseData
import com.instaapp.veggietemp1.ui.activity.AddToCartActivity
import com.instaapp.veggietemp1.ui.activity.MenuActivity
import com.instaapp.veggietemp1.utils.*
import com.instaapp.vegiestemp1.R
import java.util.*

class MenuListAdapter(
    private var mMenuDataModel: ArrayList<MenuResponseData>,
    private val mContext: Context, private val callFrom: String, private val catID: Int,
    private val catName: String,
    private val listener: RecyclerViewClickListenerForAddToCart
) : RecyclerView.Adapter<MenuListAdapter.ViewHolderClass>() {
    fun updateAdapter(
        mMenuDataModel: ArrayList<MenuResponseData>
    ) {
        this.mMenuDataModel.clear()
        this.mMenuDataModel = mMenuDataModel
        notifyDataSetChanged()
    }

    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var product_imgview: ShapeableImageView = itemView.findViewById(R.id.menu_img)
        var product_name: TextView = itemView.findViewById(R.id.menu_name)
        var product_price: TextView = itemView.findViewById(R.id.menu_price)
        var mrpPrice: TextView = itemView.findViewById(R.id.mrpPrice)
        var product_desc: TextView = itemView.findViewById(R.id.menu_desc)
        var layoutOutOfStock: RelativeLayout = itemView.findViewById(R.id.layoutOutOfStock)
        var cat_name: TextView = itemView.findViewById(R.id.cat_name)
        var view: CardView = itemView.findViewById(R.id.card)
        var btnAddToCart: AppCompatButton = itemView.findViewById(R.id.btnAddToCart)
        var layoutEditCart: LinearLayout = itemView.findViewById(R.id.layoutEditCart)
        var imgMinus: ImageView = itemView.findViewById(R.id.imgMinus)
        var imgPlus: ImageView = itemView.findViewById(R.id.imgPlus)
        var txtQTY: TextView = itemView.findViewById(R.id.txtQTY)
        var cardView: CardView = itemView.findViewById(R.id.card)
        var menu_img: ImageView = itemView.findViewById(R.id.menu_img)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): ViewHolderClass {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view: View = inflater.inflate(R.layout.grid_menu_layout, viewGroup, false)
        return ViewHolderClass(view)
    }

    override fun getItemCount(): Int {
        return mMenuDataModel.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        @NonNull viewHolderClass: ViewHolderClass,
        @SuppressLint("RecyclerView") position: Int
    ) {
        viewHolderClass.btnAddToCart.setOnClickListener {
            listener.onRecyclerItemClick(
                viewHolderClass.cardView, mMenuDataModel[position], "ADD"
            )
        }
        viewHolderClass.imgMinus.setOnClickListener {
            listener.onRecyclerItemClick(
                viewHolderClass.cardView, mMenuDataModel[position], "Minus"
            )
        }
        viewHolderClass.imgPlus.setOnClickListener {
            listener.onRecyclerItemClick(viewHolderClass.cardView, mMenuDataModel[position], "Plus")
        }

        if (CartManager.isProductInCart(mMenuDataModel[position].product_id!!)) {
            viewHolderClass.btnAddToCart.visibility = View.GONE
            viewHolderClass.layoutEditCart.visibility = View.VISIBLE
            viewHolderClass.txtQTY.text =
                (CartManager.getCartForProduct(mMenuDataModel[position].product_id!!)!!.third).toString()
        }

        if (mMenuDataModel[position].product_url != null && mMenuDataModel[position].product_url!!.length > 8)
            Glide.with(mContext)
                .load(mMenuDataModel[position].product_url)
                .error(R.drawable.no_img)
                .into(viewHolderClass.product_imgview)

        if (mMenuDataModel[position].price != null && !mMenuDataModel[position].price.equals("0"))
            viewHolderClass.product_price.text =
                PreferenceProvider(mContext).getStringValue(PreferenceKey.CURRENCY_TYPE) + mMenuDataModel[position].price
        else
            viewHolderClass.product_price.text = "Based on Your Choices"

        viewHolderClass.mrpPrice.text =
            PreferenceProvider(mContext).getStringValue(PreferenceKey.CURRENCY_TYPE) + mMenuDataModel[position].MRP

        viewHolderClass.product_name.text = "" + mMenuDataModel[position].product_name
        if (mMenuDataModel[position].extra != null && mMenuDataModel[position].extra != "null" && mMenuDataModel[position].extra!!.length > 6) {
            log("viewHolderClass ", " Else True ${mMenuDataModel[position].extra}")
            viewHolderClass.product_desc.visibility = View.VISIBLE
            viewHolderClass.product_desc.text = "" + mMenuDataModel[position].extra
        }

        viewHolderClass.menu_img.setOnClickListener {
            val intent = Intent(mContext, AddToCartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("product_id", mMenuDataModel[position].product_id.toString())
            intent.putExtra(
                "product_price", getFormatDoubleValue(mMenuDataModel[position].price!!.toDouble())
            )
            intent.putExtra("product_name", mMenuDataModel[position].product_name)
            intent.putExtra("product_desc", mMenuDataModel[position].extra)
            intent.putExtra("product_url", mMenuDataModel[position].product_url)
            intent.putExtra("category_id", catID.toString())
            intent.putExtra("category_name", catName)

            mContext.startActivity(intent)
        }

        if (callFrom.equals("Search", ignoreCase = true))
            viewHolderClass.cat_name.visibility = View.VISIBLE
        viewHolderClass.cat_name.text = catName

        // Check Menu Availability
        if ((!mMenuDataModel[position].category_available_time.equals("All Time", ignoreCase = true)
                    && checkAvailableTime(mMenuDataModel[position].category_available_time))
            || (!mMenuDataModel[position].in_stock!!)
            || (!mMenuDataModel[position].available_time.equals("All Time", ignoreCase = true)
                    && checkAvailableTime(mMenuDataModel[position].available_time))
        ) {
            viewHolderClass.layoutOutOfStock.visibility = View.VISIBLE
            viewHolderClass.view.isEnabled = false
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
