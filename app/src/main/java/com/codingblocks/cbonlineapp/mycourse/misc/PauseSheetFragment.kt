package com.codingblocks.cbonlineapp.mycourse.misc

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.batches.RUNTIERS
import com.codingblocks.cbonlineapp.course.checkout.CheckoutActivity
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.DIALOG_TYPE
import com.codingblocks.cbonlineapp.util.livedata.observeOnce
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.util.showConfirmDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.course_pause_fragment.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class PauseSheetFragment : BottomSheetDialogFragment() {

    val vm: MyCourseViewModel by sharedViewModel()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogView ->
            val d = dialogView as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.course_pause_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCancelBtn.setOnClickListener {
            dismiss()
        }
        vm.getRunAttempt().observer(viewLifecycleOwner) { runAttempt ->
            pauseDescriptionTv.text = buildSpannedString {
                append(getString(R.string.pause_desc))
                runAttempt.pauseTimeLeft?.let {
                    bold {
                        val time = PrettyTime().format(Date(System.currentTimeMillis() + it.toLong()))
                        append(getString(R.string.pause_time_left, time))
                    }
                }
            }
            pauseCourseBtn.setOnClickListener {
                if (!runAttempt.premium) {
                    showConfirmDialog(DIALOG_TYPE.PURCHASE) {
                        positiveBtnClickListener {
                            dialog?.dismiss()
                            vm.addToCart().observeOnce {
                                this@PauseSheetFragment.dismiss()
                                requireContext().startActivity<CheckoutActivity>()
                            }
                        }
                    }.show()
                } else if (runAttempt.premium && runAttempt.runTier != RUNTIERS.LITE.name) {
                    if (runAttempt.pauseTimeLeft.toString() != "0")
                        vm.pauseCourse().observeOnce { res ->
                            if (res) {
                                requireActivity().finish()
                            } else {
                                dismiss()
                            }
                        }
                } else {
                    val upgradeCourse = UpgradeSheetFragment()
                    upgradeCourse.show(childFragmentManager, upgradeCourse.tag)
                }
            }
        }
    }
}
