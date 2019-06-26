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
