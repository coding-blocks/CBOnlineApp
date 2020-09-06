package com.codingblocks.cbonlineapp.mycourse.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.onlineapi.models.SendFeedback
import kotlinx.android.synthetic.main.fragment_misc.*
import kotlinx.android.synthetic.main.rating_dialog.*
import kotlinx.android.synthetic.main.rating_dialog.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseMiscFragment : BaseCBFragment(), AnkoLogger {

    val vm by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_misc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pauseCourse.setOnClickListener {
            val pauseCourse = PauseSheetFragment()
            pauseCourse.show(childFragmentManager, pauseCourse.tag)
        }

        upgradeCourse.setOnClickListener {
            val upgradeCourse = UpgradeSheetFragment()
            upgradeCourse.show(childFragmentManager, upgradeCourse.tag)
        }

        rateCourse.setOnClickListener {
            showRatingDialog()
        }
    }

    private fun showRatingDialog() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val ratingDialog = requireContext().layoutInflater.inflate(R.layout.rating_dialog, null)
        vm.getFeedback().observer(viewLifecycleOwner) {
            dialog.overallExp.setText(it?.userScore?.heading)
            dialog.publicRev.setText(it?.userScore?.review)
            dialog.ratingBar.rating = it?.userScore?.value?.toFloat() ?: 0.0f
        }
        with(dialog) {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(ratingDialog)
            setCancelable(false)
            show()
            ratingDialog.dialogPositiveBtn.setOnClickListener {
                if (ratingDialog.overallExp.text?.isNotEmpty() == true && ratingDialog.publicRev.text?.isNotEmpty() == true) {
                    val feedback = SendFeedback(
                        ratingDialog.overallExp.text.toString(),
                        ratingDialog.publicRev.text.toString(),
                        ratingDialog.ratingBar.rating
                    )
                    vm.sendFeedback(feedback).observer(viewLifecycleOwner) {
                        dismiss()
                        requireView().snackbar("Feedback submitted")
                    }
                } else {
                    toast("Cannot send empty Feedback, Fill all fields properly")
                }
            }
            ratingDialog.dialogNegativeBtn.setOnClickListener {
                dismiss()
            }
        }
    }
}
