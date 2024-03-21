package com.instaapp.yashaswinifresh.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        fun convertIntoDateFormat(
            startDate: String?
        ): String? {
            val old = "yyyy-MM-dd"
            val newFormat = "dd MMMM yyyy"
            var changeDate: String? = null
            try {
                val format: DateFormat = SimpleDateFormat(old, Locale.ENGLISH)
                val date = format.parse(startDate!!)
                val newformat = SimpleDateFormat(newFormat, Locale.ENGLISH)
                val newDate = newformat.format(date!!)
                changeDate =
                    newDate.split(" ".toRegex())
                        .toTypedArray()[1] + " " + newDate.split(" ".toRegex())
                        .toTypedArray()[0] + ", " + newDate.split(" ".toRegex()).toTypedArray()[2]
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return changeDate
        }
    }
}