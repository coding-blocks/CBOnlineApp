package com.codingblocks.cbonlineapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getCalculatedDate(days: Int): String {
        val dateFormat = "yyyy-MM-dd"
        val date: String = SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date())

        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat, Locale.getDefault())
        if (date.isNotEmpty()) {
            cal.time = s.parse(date)!!
        }
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    fun getToday(): String{
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

}
