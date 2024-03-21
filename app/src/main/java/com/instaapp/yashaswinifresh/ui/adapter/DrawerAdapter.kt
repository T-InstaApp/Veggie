package com.instaapp.yashaswinifresh.ui.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.utils.PreferenceProvider

class DrawerAdapter(private val context: Context, private val menuArray: Array<String>) :
    BaseAdapter() {

    private var inflater: LayoutInflater? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getCount(): Int {
        return menuArray.size
    }

    override fun getItem(position: Int): Any {
        return ""
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (inflater == null) {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = inflater?.inflate(R.layout.drawer_menu_layout, null)
        }

        val imageView: ImageView = convertView?.findViewById(R.id.drawer_menu_img)!!
        val textView: TextView = convertView.findViewById(R.id.drawer_menu_name)!!

        textView.text = menuArray[position]

        when {
            menuArray[position].equals("Menu", ignoreCase = true) -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_new_menu
                    )
                )
            }
            menuArray[position].equals("Order list", ignoreCase = true) -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_purchase_order
                    )
                )
            }
            menuArray[position].equals("Cart", ignoreCase = true) -> {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_cart))
            }
            menuArray[position].equals("About Us", ignoreCase = true) -> {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.about_us1))
            }
            menuArray[position].equals("Contact Us", ignoreCase = true) -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_contact
                    )
                )
            }
            menuArray[position].equals(
                "Log Out",
                ignoreCase = true
            ) || menuArray[position].equals("Login", ignoreCase = true) -> {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_login))
            }
            menuArray[position].equals("Profile", ignoreCase = true) -> {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_profile
                    )
                )
            }
            menuArray[position].equals("Delete Account", ignoreCase = true) -> {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_delete))
            }
        }

        return convertView!!
    }
}
