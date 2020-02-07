package com.codingblocks.cbonlineapp.baseclasses

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner

/**
 * Created by championswimmer on 2020-02-02.
 */
abstract class BaseCBActivity : AppCompatActivity(), BaseLifecycleComponent {
    override val thisLifecycleOwner: LifecycleOwner get() = this

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
    }
}
