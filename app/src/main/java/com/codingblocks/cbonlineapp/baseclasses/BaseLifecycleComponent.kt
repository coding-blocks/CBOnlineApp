package com.codingblocks.cbonlineapp.baseclasses

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.codingblocks.cbonlineapp.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by championswimmer on 2020-02-02.
 */
interface BaseLifecycleComponent : LifecycleObserver {
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d("LIFECYCLE", msg)
        } else {
            FirebaseCrashlytics.getInstance().log(msg)
        }
    }

    val thisLifecycleOwner: LifecycleOwner

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun logOnCreate(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON CREATE ${lifecycleOwner::class.simpleName}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun logOnStart(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON START ${lifecycleOwner::class.simpleName}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun logOnResume(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON RESUME ${lifecycleOwner::class.simpleName}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun logOnPause(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON PAUSE ${lifecycleOwner::class.simpleName}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun logOnStop(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON STOP ${lifecycleOwner::class.simpleName}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun logOnDestroy(lifecycleOwner: LifecycleOwner = thisLifecycleOwner) {
        log("ON DESTROY ${lifecycleOwner::class.simpleName}")
    }
}
