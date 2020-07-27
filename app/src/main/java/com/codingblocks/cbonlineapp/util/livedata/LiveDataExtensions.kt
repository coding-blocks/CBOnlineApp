package com.codingblocks.cbonlineapp.util.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.codingblocks.cbonlineapp.database.models.ContentModel

fun <T> LiveData<T>.observer(owner: LifecycleOwner, onEmission: (T) -> Unit) {
    return observe(
        owner,
        Observer<T> {
            if (it != null) {
                onEmission(it)
            }
        }
    )
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
    distinctLiveData.addSource(
        this,
        object : Observer<T> {
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
                    /** [ContentModel] do not required an update from live data when progress is updated */
                    if (lastObj is ContentModel && obj is ContentModel) {
                        val oldObj = (lastObj as ContentModel)
                        val newObj = (obj as ContentModel)
                        if (oldObj.ccid != newObj.ccid) {
                            lastObj = obj
                            distinctLiveData.postValue(lastObj)
                        }
                    } else {
                        lastObj = obj
                        distinctLiveData.postValue(lastObj)
                    }
                }
            }
        }
    )
    return distinctLiveData
}

fun pageChangeCallback(
    fnState: (Int) -> Unit = { },
    fnSelected: (Int) -> Unit = { },
    fnScrolled: (Int, Float, Int) -> Unit
): ViewPager2.OnPageChangeCallback {
    return object : ViewPager2.OnPageChangeCallback() {
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

class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> = NonNullMediatorLiveData()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}

fun <T> NonNullMediatorLiveData<T>.observe(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(
        owner,
        Observer {
            it?.let(observer)
        }
    )
}

/**
 * Emits the items that pass through the predicate
 */
inline fun <T> LiveData<List<T>>.filterList(crossinline predicate: (T?) -> Boolean): LiveData<List<T>> {
    val mutableLiveData: MediatorLiveData<List<T>> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        val destination = ArrayList<T>()
        for (element in it)
            if (predicate(element)) destination.add(element)
        mutableLiveData.value = destination
    }
    return mutableLiveData
}
