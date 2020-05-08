package com.codingblocks.cbonlineapp.library

import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TYPE
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_library.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class LibraryActivity : BaseCBActivity() {

    val vm: LibraryViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setToolbar(libraryToolbar)
        intent.getStringExtra(COURSE_NAME)?.let {
            vm.name = it
        }
        intent.getStringExtra(TYPE)?.let {
            vm.type = it
        }
        intent.getStringExtra(RUN_ATTEMPT_ID)?.let {
            vm.attemptId = it
        }
        title = vm.name
        if (vm.type.isNullOrEmpty()) {
            replaceFragmentSafely(LibraryHomeFragment(), containerViewId = R.id.libraryContainer)
        } else {
            replaceFragmentSafely(LibraryViewFragment(), containerViewId = R.id.libraryContainer)
        }
    }
}
