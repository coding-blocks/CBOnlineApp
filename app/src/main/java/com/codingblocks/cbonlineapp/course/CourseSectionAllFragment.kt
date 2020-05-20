package com.codingblocks.cbonlineapp.course

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.batches.RUNTIERS
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_all_section_course.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseSectionAllFragment : BottomSheetDialogFragment() {

    private val viewModel: CourseViewModel by sharedViewModel()
    private val courseSectionListAllAdapter = CourseSectionListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_all_section_course, container, false)

        viewModel.fetchCourse()

        view.closeBtn.setOnClickListener {
            dialog?.dismiss()
        }

        viewModel.course.distinctUntilChanged().observer(this) { course ->
            course.getContentRun(RUNTIERS.PREMIUM.name)?.let {
                it.sections?.let { it1 -> viewModel.fetchSections(it1) }
            }
        }

        viewModel.sections.observe(this) { sections ->
            if (!sections.isNullOrEmpty()) {
                view.contentShimmer.stopShimmer()
                view.contentShimmer.isVisible = false
                courseSectionListAllAdapter.submitList(sections)
            }
        }

        view.courseContentAllRv.setRv(requireContext(), courseSectionListAllAdapter, true)

        return view
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener(DialogInterface.OnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        })
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val sheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
        val layoutParams = sheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        sheet.layoutParams = layoutParams
        BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int { // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}
