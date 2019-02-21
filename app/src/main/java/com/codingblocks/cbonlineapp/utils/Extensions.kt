package com.codingblocks.cbonlineapp.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException

fun <T> LiveData<T>.observer(owner: LifecycleOwner, onEmission: (T) -> Unit) {
    return observe(owner, Observer<T> {
        if (it != null) {
            onEmission(it)
        }
    })
}

fun formatDate(date: String): String {
    var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    if (date.isEmpty()){
        throw NoSuchElementException("Invalid Date")
    }
    val newDate = format.parse(date)

    format = SimpleDateFormat("MMM dd,yyyy hh:mm", Locale.US)
    return format.format(newDate)
}

fun pageChangeCallback(
        fnState: (Int) -> Unit,
        fnSelected: (Int) -> Unit,
        fnScrolled: (Int, Float, Int) -> Unit
): ViewPager.OnPageChangeListener {
    return object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = fnState(state)
        override fun onPageSelected(position: Int) = fnSelected(position)
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =
                fnScrolled(position, positionOffset, positionOffsetPixels)
    }
}