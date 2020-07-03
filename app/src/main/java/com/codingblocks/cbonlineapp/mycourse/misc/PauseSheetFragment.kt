package com.codingblocks.cbonlineapp.mycourse.misc

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.RoundedBottomSheetDialogFragment
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import com.codingblocks.cbonlineapp.util.livedata.observeOnce
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.course_pause_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PauseSheetFragment : BottomSheetDialogFragment() {

    val vm: MyCourseViewModel by sharedViewModel()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_pause_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCancelBtn.setOnClickListener {
            dialog?.dismiss()
        }
        vm.run?.observer(viewLifecycleOwner) { courseRunPair ->
            pauseDescriptionTv.text = buildSpannedString {
                append(getString(R.string.pause_desc))
                bold {
                    append(getString(R.string.pause_time_left, courseRunPair.runAttempt.lastPausedLeft?.timeAgo()))
                }
            }

        }
        pauseCourseBtn.setOnClickListener {
            vm.pauseCourse().observeOnce { res ->
                if (res) {
                    requireActivity().finish()
                } else {
                    dialog?.dismiss()
                }
            }
        }
    }


}
