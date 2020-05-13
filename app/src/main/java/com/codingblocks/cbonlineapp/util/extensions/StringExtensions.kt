package com.codingblocks.cbonlineapp.util.extensions

import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import androidx.core.text.color
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException
import kotlin.math.floor
import kotlin.math.log10
import org.ocpsoft.prettytime.PrettyTime

fun folderSize(directory: File): Long {
    var length: Long = 0
    for (file in directory.listFiles()) {
        length += if (file.isFile)
            file.length()
        else
            folderSize(file)
    }
    return length
}

fun Long.readableFileSize(): String {
    if (this <= 0) return "0 MB"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        this / Math.pow(
            1024.0,
            digitGroups.toDouble()
        )
    ) + " " + units[digitGroups]
}

fun String.greater(): Boolean {
    return this.toLong() <= (System.currentTimeMillis() / 1000)
}

fun String.timeAgo(): String {
    return if (this.isNullOrEmpty())
        ""
    else {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val time = sdf.parse(this).time
        val prettyTime = PrettyTime(Locale.getDefault())
        prettyTime.format(Date(time))
    }
}

fun Long.getDurationBreakdown(): String {
    if (this <= 0) {
        return "---"
    }
    var millis = this
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)

    val sb = StringBuilder(64)
    sb.append(hours)
    sb.append(" Hours ")
    sb.append(minutes)
    sb.append(" Mins ")
    return (sb.toString())
}

fun formatDate(date: String): String {
    var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    if (date.isEmpty()) {
        throw NoSuchElementException("Invalid Date")
    }
    val newDate = format.parse(date)
    val calender = Calendar.getInstance()
    calender.time = newDate
    calender.add(Calendar.HOUR, 5)
    calender.add(Calendar.MINUTE, 30)
    format = SimpleDateFormat("dd.MM.yy | hh:mma", Locale.US)
    return format.format(calender.time)
}

fun String.isotomillisecond(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    val newDate = format.parse(this)
    return newDate.time
}

fun Double.secToTime(): String {
    val sec = this.toInt()
    val seconds = sec % 60
    var minutes = sec / 60
    if (minutes >= 60) {
        val hours = minutes / 60
        minutes %= 60
        if (hours >= 24) {
            val days = hours / 24
            return String.format("%d days %02d:%02d:%02d", days, hours % 24, minutes, seconds)
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    return String.format("%02d:%02d", minutes, seconds)
}

fun getDateForTime(time: String): String {
    val dateFormat = SimpleDateFormat("dd MMM " + "''" + "yy", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("IST")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time.toLong() * 1000

    return dateFormat.format(calendar.time)
}

fun getDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()

    return dateFormat.format(calendar.time)
}

fun getDateForRun(time: String): String {
    val dateFormat = SimpleDateFormat("MMM " + "''" + "yy", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time.toLong() * 1000
    calendar.timeZone = TimeZone.getTimeZone("IST")
    return dateFormat.format(calendar.time)
}

fun getSpannableSring(boldText: String, normalText: String): SpannableStringBuilder =
    SpannableStringBuilder()
        .bold { append(boldText) }
        .append(normalText)

fun getSpannableString(text: String): SpannableStringBuilder =
    SpannableStringBuilder()
        .color(Color.parseColor("#f2734c")) {
            append(text)
        }

fun getSpannableStringSecondBold(normalText: String, boldText: String): SpannableStringBuilder =
    SpannableStringBuilder()
        .append(normalText)
        .bold { append(boldText) }

fun timeAgo(time: Long): String {
    val diff = floor(((System.currentTimeMillis() - time) / 1000).toDouble())
    var interval = floor(diff / 31536000).toInt()
    if (interval >= 1) {
        return "$interval Years Ago"
    }
    interval = floor(diff / 2592000).toInt()
    if (interval >= 1) {
        return "$interval Months Ago"
    }
    interval = floor(diff / 604800).toInt()
    if (interval >= 1) {
        return "$interval Weeks Ago"
    }
    interval = floor(diff / 86400).toInt()
    if (interval >= 1) {
        return "$interval Days Ago"
    }
    interval = floor(diff / 3600).toInt()
    if (interval >= 1) {
        return "$interval Hours Ago"
    }
    interval = floor(diff / 60).toInt()
    if (interval >= 1) {
        return "$interval Minutes Ago"
    }
    return "Just Now"
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
