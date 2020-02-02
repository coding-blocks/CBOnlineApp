package com.codingblocks.cbonlineapp.baseclasses

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 * Created by championswimmer on 2020-02-02.
 */
abstract class BaseCBFragment : Fragment(), BaseLifecycleComponent {
    override val thisLifecycleOwner: LifecycleOwner get() = this

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(this)
    }
}
