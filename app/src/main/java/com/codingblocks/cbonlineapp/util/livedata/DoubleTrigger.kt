package com.codingblocks.cbonlineapp.util.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Custom [MediatorLiveData] which takes an [Pair] instead of a single variable
 */
class DoubleTrigger<A, B>(a: LiveData<A>, b: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(a) { value = it to b.value }
        addSource(b) { value = a.value to it }
    }
}
