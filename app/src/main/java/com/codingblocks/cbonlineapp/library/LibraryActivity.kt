package com.codingblocks.cbonlineapp.library

import android.app.Activity
import android.os.Bundle
import androidx.activity.invoke
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.DIALOG_TYPE
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TYPE
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.util.showConfirmDialog
import com.codingblocks.onlineapi.ErrorStatus
import kotlinx.android.synthetic.main.activity_library.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class LibraryActivity : BaseCBActivity() {

    val vm: LibraryViewModel by stateViewModel()
    private var confirmDialog: AlertDialog? = null

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            confirmDialog?.dismiss()
            toast(getString(R.string.logged_in))
            vm.fetchSections()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        setToolbar(libraryToolbar)
        libraryToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        vm.run?.distinctUntilChanged()?.observer(thisLifecycleOwner) { courseAndRun ->
            if (courseAndRun.runAttempt.paused) {
                showPauseDialog(courseAndRun.runAttempt.pauseTimeLeft)
            }
        }
        vm.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.UNAUTHORIZED -> {
                    if (confirmDialog == null)
                        confirmDialog = showConfirmDialog(DIALOG_TYPE.UNAUTHORIZED) {
                            cancelable = false
                            positiveBtnClickListener { startForResult(intentFor<LoginActivity>()) }
                            negativeBtnClickListener { finish() }
                        }
                    confirmDialog?.show()
                }
                else -> {
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }
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

    private fun showPauseDialog(pauseTimeLeft: String?) {
        var confirmDialog: AlertDialog? = null
        if (confirmDialog == null)
            confirmDialog = showConfirmDialog(DIALOG_TYPE.PAUSED) {
                cancelable = false
                positiveBtnClickListener { finish() }
                negativeBtnClickListener { finish() }
            }
        confirmDialog.show()
    }
}
