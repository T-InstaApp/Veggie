package com.instaapp.yashaswinifresh.ui.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instaapp.yashaswinifresh.R
import com.instaapp.yashaswinifresh.interface_class.RecyclerViewClickListenerForSubAddons
import com.instaapp.yashaswinifresh.ui.activity.CollectPrice
import com.instaapp.yashaswinifresh.utils.AddonsSelectedDataModel
import com.instaapp.yashaswinifresh.utils.parseDateSuspendedMenu

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class MultipleCheckBoxListAdapter(
    context: Context, textViewResourceId: Int,
    countryList: ArrayList<AddonsSelectedDataModel>, maxCount: Int,
    collectPrice: ArrayList<CollectPrice>,
    listener: RecyclerViewClickListenerForSubAddons,
    val currencyType:String?
) :
    ArrayAdapter<AddonsSelectedDataModel>(context, textViewResourceId, countryList) {

    var addonContentList = ArrayList<AddonsSelectedDataModel>()
    var collectPrice = ArrayList<CollectPrice>()
    private val context: Context
    private val maxCount: Int
    var totalCount = 0
    private val listener: RecyclerViewClickListenerForSubAddons

    init {
        this.addonContentList.addAll(countryList)
        calculateTotal()
        this.context = context
        this.maxCount = maxCount
        this.collectPrice = collectPrice
        this.listener = listener
    }

    private fun calculateTotal() {
        for (a in addonContentList)
            if (a.isSelected)
                totalCount += (1 + a.totalCountInt)
    }

    private class ViewHolder {
        var txtContentName: TextView? = null
        var checkBox: CheckBox? = null
        var multiSelectItemLayout: RelativeLayout? = null
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder?
        var convertViewVar = convertView
        if (convertViewVar == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val vc = AtomicReference(AtomicInteger(0))
            convertViewVar = vi.inflate(R.layout.country_info, null)

            holder = ViewHolder()
            holder.txtContentName = convertViewVar.findViewById(R.id.txtContentName)
            holder.checkBox = convertViewVar.findViewById(R.id.checkBox)
            convertViewVar.tag = holder

            holder.checkBox?.setOnClickListener {
                val cb = it as CheckBox

                cb.isEnabled = false
                Handler().postDelayed(
                    { cb.isEnabled = true }, 2000 //Specific time in milliseconds
                )
                if (cb.isChecked) {
                    totalCount += 1
                    if (maxCount >= totalCount)
                        if (addonContentList[position].isSubAddonAvailable)
                            listener.onRecyclerViewItemSelect(
                                true,
                                addonContentList[position].parentAddonID!!,
                                addonContentList[position].addonContentID!!,
                                addonContentList[position].addonContentName!!
                            )

                    collectPrice.add(
                        CollectPrice(
                            addonContentList[position].addonContentName,
                            addonContentList[position].addonContentPrice!!.toDouble()
                        )
                    )
                } else {
                    if (addonContentList[position].isSubAddonAvailable)
                        listener.onRecyclerViewItemSelect(
                            false,
                            addonContentList[position].parentAddonID!!,
                            addonContentList[position].addonContentID!!,
                            addonContentList[position].addonContentName!!
                        )
                    holder.txtContentName?.text =
                        addonContentList[position].addonContentName + " (${currencyType}" +
                                "${addonContentList[position].addonContentPrice})"
                    val contentData = holder.checkBox!!.tag as AddonsSelectedDataModel
                    totalCount -= (1 + contentData.totalCountInt)
                    contentData.totalCountInt = 0
                    collectPrice.removeAll { it.name == addonContentList[position].addonContentName }
                }

                Log.d("TAG", "getView:Check maxCount $maxCount and totalCount=$totalCount")

                if (maxCount >= totalCount) {
                    val contentData = cb.tag as AddonsSelectedDataModel
                    contentData.isSelected = (cb.isChecked)
                    vc.get().set(0)
                } else {
                    collectPrice.removeAll { it.name == addonContentList[position].addonContentName }
                    cb.isChecked = false
                    totalCount -= 1
                    val mag = "You can't choose more than $maxCount"
                    val toast = Toast.makeText(
                        context,
                        mag,
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }

            }

            holder.txtContentName?.setOnClickListener {
                if (holder.checkBox?.isChecked == true && maxCount > totalCount) {
                    holder.txtContentName?.text =
                        addonContentList[position].addonContentName + " (${
                            currencyType
                        }${addonContentList[position].addonContentPrice}) +${vc.get().addAndGet(1)}"
                    totalCount += 1
                    val contentData = holder.checkBox!!.tag as AddonsSelectedDataModel
                    contentData.totalCountInt = vc.get().get()
                    collectPrice.add(
                        CollectPrice(
                            addonContentList[position].addonContentName,
                            addonContentList[position].addonContentPrice!!.toDouble()
                        )
                    )
                    if (addonContentList[position].isSubAddonAvailable) {
                        holder.txtContentName!!.isEnabled = false
                        Handler().postDelayed(
                            { holder.txtContentName!!.isEnabled = true },
                            2000 //Specific time in milliseconds
                        )
                        listener.onRecyclerViewItemSelect(
                            true,
                            addonContentList[position].parentAddonID!!,
                            addonContentList[position].addonContentID!!,
                            addonContentList[position].addonContentName!!
                        )
                    }
                }
            }

        } else {
            holder = convertViewVar.tag as ViewHolder?
        }


        if (addonContentList[position].totalCountInt == 0)
            holder?.txtContentName?.text = addonContentList[position].addonContentName + " (${
                currencyType
            }${addonContentList[position].addonContentPrice})"
        else
            holder?.txtContentName?.text =
                addonContentList[position].addonContentName + " (${
                    currencyType
                }${addonContentList[position].addonContentPrice}) +" + addonContentList[position].totalCountInt

        holder?.checkBox?.isChecked = addonContentList[position].isSelected
        holder?.checkBox?.tag = addonContentList[position]

        if (addonContentList[position].suspended_until != null) {
            if (!parseDateSuspendedMenu(addonContentList[position].suspended_until)) {
                holder?.multiSelectItemLayout!!.isEnabled = false
                holder.checkBox?.isEnabled = false
                holder.checkBox?.buttonTintList = ColorStateList.valueOf(Color.GRAY)
                holder.txtContentName?.setTextColor(Color.parseColor("#D7D7D7"))
            }
        }

        return convertViewVar!!


    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

