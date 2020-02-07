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
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryActivity : BaseCBActivity() {

    val vm by viewModel<LibraryViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setToolbar(libraryToolbar)
        title = intent.getStringExtra(COURSE_NAME)
        vm.type = intent.getStringExtra(TYPE) ?: ""
        vm.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        if (vm.type != "") {
            replaceFragmentSafely(LibraryViewFragment(), containerViewId = R.id.libraryContainer)
        } else {
            replaceFragmentSafely(LibraryHomeFragment(), containerViewId = R.id.libraryContainer)
        }
    }
}
