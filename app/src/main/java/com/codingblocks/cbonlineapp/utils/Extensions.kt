package com.codingblocks.cbonlineapp.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException

fun <T> LiveData<T>.observe(owner: LifecycleOwner, onEmission: (T) -> Unit) {
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