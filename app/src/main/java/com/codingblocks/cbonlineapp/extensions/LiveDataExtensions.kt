package com.codingblocks.cbonlineapp.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException

fun <T> LiveData<T>.observer(owner: LifecycleOwner, onEmission: (T) -> Unit) {
    return observe(owner, Observer<T> {
        if (it != null) {
            onEmission(it)
        }
    })
}

fun <T> LiveData<T>.observeOnce(onEmission: (T) -> Unit) {
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            onEmission(value)
            removeObserver(this)
        }
    }
    observeForever(observer)
}

fun <T> LiveData<T>.getDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if ((obj == null && lastObj != null) ||
                obj != lastObj
            ) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })
    return distinctLiveData
}

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
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        this / Math.pow(
            1024.0,
            digitGroups.toDouble()
        )
    ) + " " + units[digitGroups]
}

fun String.greater(): Boolean {
    return this.toLong() >= (System.currentTimeMillis() / 1000)
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
    format = SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.US)
    return format.format(calender.time)
}

fun String.isotomillisecond(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    val newDate = format.parse(this)
    return newDate.time
}

fun secToTime(time: Double): String {
    val sec = time.toInt()
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
    return String.format("00:%02d:%02d", minutes, seconds)
}

fun pageChangeCallback(
    fnState: (Int) -> Unit,
    fnSelected: (Int) -> Unit,
    fnScrolled: (Int, Float, Int) -> Unit
): ViewPager.OnPageChangeListener {
    return object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = fnState(state)
        override fun onPageSelected(position: Int) = fnSelected(position)
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) =
            fnScrolled(position, positionOffset, positionOffsetPixels)
    }
}
